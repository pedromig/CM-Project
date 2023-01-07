package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.app.R;
import com.example.app.ui.home.adapters.HomeListAdapter;
import com.example.app.ui.home.adapters.ShoppingListAdapter;
import com.example.app.ui.home.dialogs.DialogAddShoppingListItem;
import com.example.app.ui.home.models.HomeViewModel;
import com.example.app.ui.home.models.ItemViewModel;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingListFragment extends Fragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private ProgressBar bar;

    private FloatingActionButton button;
    private ArrayList<String> shoppingList = new ArrayList<>();
    private ArrayList<String> selected = new ArrayList<>();
    private RecyclerView recyclerView;
    private MenuItem check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // Setup Action Bar
        setHasOptionsMenu(true);

        this.recyclerView = view.findViewById(R.id.shopping_list);
        this.bar = view.findViewById(R.id.progress_bar);
        this.bar.setVisibility(View.VISIBLE);
        this.button = view.findViewById(R.id.add_item);

        // Setup Toolbar button
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        System.out.println(toolbar);

        // Setup Firebase Actions on Incoming Changes
        FirebaseUser user = auth.getCurrentUser();
        db.getReference("profiles").child(user.getUid()).child("shopping")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String item = snap.getValue(String.class);
                            shoppingList.add(item);
                        }

                        bar.setVisibility(View.GONE);
                        ShoppingListAdapter shoppingListAdapter = new ShoppingListAdapter(getContext(), shoppingList, selected, check);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(shoppingListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.button.setOnClickListener(v -> {
            DialogAddShoppingListItem fragment = new DialogAddShoppingListItem(this.shoppingList);
            fragment.show(getChildFragmentManager(), "DialogAddShoppingListItem");
            ((ShoppingListAdapter) recyclerView.getAdapter()).update();
            recyclerView.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.shopping_list_bar_menu, menu);

        // Setup Filter Button Properties
        this.check = menu.findItem(R.id.action_check);
        this.check.setVisible(false);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        // Setup Text Field and Hints
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHint("Item");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Setup Filter Button Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ShoppingListAdapter adapter = (ShoppingListAdapter) recyclerView.getAdapter();
                assert adapter != null;
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_check) {
            for (String i : this.selected) {
                this.shoppingList.remove(i);
            }
            this.selected.clear();
            db.getReference("profiles").child(auth.getUid()).child("shopping").setValue(this.shoppingList);
            ((ShoppingListAdapter) recyclerView.getAdapter()).update();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        return true;
    }
}