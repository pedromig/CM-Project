package com.example.app.ui.home.fragments;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.model.Person;
import com.example.app.ui.home.adapters.ItemListAdapter;
import com.example.app.model.Item;
import com.example.app.ui.home.models.HomeViewModel;
import com.example.app.ui.home.models.ItemViewModel;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

// TODO: fix bugs on search item
public class ItemsFragment extends Fragment {
    private ItemViewModel viewModel;
    private HomeViewModel homeViewModel;
    private PersonViewModel personViewModel;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private ChildEventListener itemsEventListener;
    private ChildEventListener houseMembersEventListener;

    private Home home;

    private RecyclerView itemRecyclerView;
    private FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_items, container, false);

        this.personViewModel = new ViewModelProvider(requireActivity()).get(PersonViewModel.class);
        this.homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        if (homeViewModel.getMembers() != null) {
            homeViewModel.getMembers().clear();
        }

        // Setup View Model
        this.viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        if (this.viewModel.getItems() != null) {
            this.viewModel.getItems().clear();
        }

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        this.home = homeViewModel.getResidences().get(bundle.getInt("selectedHome"));

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Floating Action Button
        this.floatingActionButton = view.findViewById(R.id.add_item);

        // Recycler View Settings
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        ItemListAdapter itemListAdapter = new ItemListAdapter(this.viewModel.getItems(), home, navController, viewModel);
        this.itemRecyclerView = view.findViewById(R.id.items_list);
        this.itemRecyclerView.setAdapter(itemListAdapter);
        this.setupRecyclerViewFirebaseListeners();

        return view;
    }

    private void setupRecyclerViewFirebaseListeners() {
        // Setup Firebase Actions on Incoming Changes
        this.itemsEventListener = db.getReference("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                Item item = snapshot.getValue(Item.class);
                if (item != null && item.getHomeKey().equals(home.getKey())) {
                    for (Item it : viewModel.getItems()) {
                        if (it.getKey().equals(item.getKey())) {
                            return;
                        }
                    }
                    item.setKey(snapshot.getKey());
                    viewModel.addItem(snapshot.getValue(Item.class));
                    ItemListAdapter adapter = (ItemListAdapter) itemRecyclerView.getAdapter();
                    assert adapter != null;
                    adapter.update();
                    adapter.notifyItemInserted(viewModel.getItems().size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
                Item item = snapshot.getValue(Item.class);
                if (item != null && item.getHomeKey().equals(home.getKey())) {
                    for (int i = 0; i < viewModel.getItems().size(); ++i) {
                        Item it = viewModel.getItems().get(i);
                        if (it.getKey().equals(snapshot.getKey())) {
                            viewModel.getItems().set(i, item);
                            ItemListAdapter adapter = (ItemListAdapter) itemRecyclerView.getAdapter();
                            assert adapter != null;
                            adapter.update();
                            adapter.notifyItemChanged(i);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < viewModel.getItems().size(); ++i) {
                    Item it = viewModel.getItems().get(i);
                    if (it.getKey().equals(snapshot.getKey())) {
                        viewModel.getItems().remove(i);
                        ItemListAdapter adapter = (ItemListAdapter) itemRecyclerView.getAdapter();
                        assert adapter != null;
                        adapter.update();
                        adapter.notifyItemRemoved(i);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        this.houseMembersEventListener = db.getReference("homes")
                .child(this.home.getKey())
                .child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                        String member = snapshot.getValue(String.class);
                        for (Person person : personViewModel.getPeople()) {
                            if (person.getKey().equals(member) && !homeViewModel.getMembers().contains(person)) {
                                homeViewModel.addMember(person);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        String member = snapshot.getValue(String.class);
                        Person person = null;
                        for (Person p : personViewModel.getPeople()) {
                            if (p.getKey().equals(member)) {
                                person = p;
                            }
                        }
                        if (person != null) {
                            for (int i = 0; i < homeViewModel.getMembers().size(); ++i) {
                                Person m = homeViewModel.getMembers().get(i);
                                if (m.getKey().equals(person.getKey())) {
                                    homeViewModel.getMembers().remove(i);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.floatingActionButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = ItemsFragmentDirections.actionItemsFragmentToItemEditFragment(Item.NEW_ITEM, home.getKey());
            navController.navigate(action);
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.item_bar_menu, menu);

        // Setup Filter Button Properties
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        // Setup Text Field and Hints
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHint("Search Item");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        ItemListAdapter adapter = (ItemListAdapter) itemRecyclerView.getAdapter();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                assert adapter != null;
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.db.getReference().removeEventListener(this.itemsEventListener);
        this.db.getReference().removeEventListener(this.houseMembersEventListener);
    }
}