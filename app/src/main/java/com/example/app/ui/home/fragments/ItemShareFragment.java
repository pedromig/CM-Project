package com.example.app.ui.home.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.example.app.model.Person;
import com.example.app.ui.home.adapters.PersonMultiSelectListAdapter;
import com.example.app.ui.home.models.HomeViewModel;
import com.example.app.ui.home.models.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class ItemShareFragment extends Fragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private HomeViewModel homeViewModel;
    private ItemViewModel viewModel;

    private String homeKey;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_share, container, false);

        this.homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        this.viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        Bundle bundle = this.getArguments();
        assert bundle != null;
        this.homeKey = bundle.getString("selectedHome");

        this.recyclerView = view.findViewById(R.id.multiselect_share_list);

        PersonMultiSelectListAdapter multiSelectAdapter = new PersonMultiSelectListAdapter(getContext(), this.homeViewModel.getMembers(), this.viewModel.getOwners());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(multiSelectAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
};