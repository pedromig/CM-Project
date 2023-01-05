package com.example.app.ui.home.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.ui.home.adapters.HomeListAdapter;
import com.example.app.ui.home.dialogs.DialogAddHome;
import com.example.app.ui.home.models.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class HomeFragment extends Fragment {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private HomeViewModel viewModel;

    private RecyclerView homeRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Setup View Model
        this.viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Recycler View Settings
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        HomeListAdapter homeListAdapter = new HomeListAdapter(viewModel.getResidences(), navController);
        this.homeRecyclerView = view.findViewById(R.id.home_list);
        this.homeRecyclerView.setAdapter(homeListAdapter);
        this.setupRecyclerViewFirebaseListeners();

        return view;
    }

    private void setupRecyclerViewFirebaseListeners() {
        // Setup Firebase Actions on Incoming Changes
        db.getReference("homes").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                Home home = snapshot.getValue(Home.class);
                if (home != null && home.getMembers().contains(auth.getCurrentUser().getUid())) {
                    for (Home h : viewModel.getResidences()) {
                        if (h.getKey().equals(home.getKey())){
                            return;
                        }
                    }
                    home.setKey(snapshot.getKey());
                    viewModel.addResidence(snapshot.getValue(Home.class));
                    HomeListAdapter adapter = (HomeListAdapter) homeRecyclerView.getAdapter();
                    assert adapter != null;
                    adapter.update();
                    adapter.notifyItemInserted(viewModel.getResidences().size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
                Home home = snapshot.getValue(Home.class);
                if (home != null && home.getMembers().contains(auth.getCurrentUser().getUid())) {
                    for (int i = 0; i < viewModel.getResidences().size(); ++i) {
                        Home h = viewModel.getResidences().get(i);
                        if (h.getKey().equals(snapshot.getKey())) {
                            viewModel.getResidences().set(i, home);

                            HomeListAdapter adapter = (HomeListAdapter) homeRecyclerView.getAdapter();
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
                for (int i = 0; i < viewModel.getResidences().size(); ++i) {
                    Home h = viewModel.getResidences().get(i);
                    if (h.getKey().equals(snapshot.getKey())) {
                        viewModel.getResidences().remove(i);
                        HomeListAdapter adapter = (HomeListAdapter) homeRecyclerView.getAdapter();
                        assert adapter != null;
                        adapter.update();
                        adapter.notifyItemRemoved(i);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_bar_menu, menu);

        // Setup Filter Button Properties
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        // Setup Text Field and Hints
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHint("Search Residence");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Setup Filter Button Listener
        HomeListAdapter adapter = (HomeListAdapter) homeRecyclerView.getAdapter();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);
                assert adapter != null;
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_home) {
            DialogAddHome fragment = new DialogAddHome();
            fragment.show(getChildFragmentManager(), "AddHomeDialog");
        }
        return true;
    }

}