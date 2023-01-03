package com.example.app.ui.home.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

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
import com.example.app.ui.home.adapters.HomeListAdapter;
import com.example.app.ui.home.dialogs.DialogAddHome;
import com.example.app.ui.home.models.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;

    private RecyclerView homeRecyclerView;
    private HomeListAdapter homeListAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Setup Action Bar
        setHasOptionsMenu(true);

        // Fetch ViewModel and Build Adapter
        this.viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // RecyclerView Adapter
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        this.homeListAdapter = new HomeListAdapter(this.viewModel.getResidences(), navController);

        // Recycler View Settings
        this.homeRecyclerView = view.findViewById(R.id.home_list);
        this.homeRecyclerView.setAdapter(this.homeListAdapter);

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
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
        // searchView.setOnQueryTextListener(new ResidencesFilterListener((HomeListAdapter) this.homeRecyclerView.getAdapter()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_home) {
            DialogAddHome fragment = new DialogAddHome(viewModel);
            this.homeListAdapter.updateResidences(this.viewModel.getResidences());
            fragment.show(getChildFragmentManager(), "AddHomeDialog");
            Objects.requireNonNull(homeRecyclerView.getAdapter()).notifyDataSetChanged();
        }
        return true;
    }
}