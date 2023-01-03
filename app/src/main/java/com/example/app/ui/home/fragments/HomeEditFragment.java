package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.example.app.ui.home.models.HomeViewModel;

public class HomeEditFragment extends Fragment {

    private HomeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_home, container, false);

        // Navigation Controller Setup
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Fetch ViewModel and Build Adapter
        this.viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_home_bar_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}