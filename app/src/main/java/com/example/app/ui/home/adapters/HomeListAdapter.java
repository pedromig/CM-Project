package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.ui.home.fragments.HomeFragmentDirections;
import com.example.app.ui.home.models.Home;
import com.example.app.ui.home.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {
    private final NavController navController;
    private ArrayList<Home> residences;
    private ImageButton editbtn;

    public HomeListAdapter(ArrayList<Home> residences, NavController navController) {
        this.residences = residences;
        this.navController = navController;
    }

    public void updateResidences(ArrayList<Home> residences) {
        this.residences = residences;
    }

    @NonNull
    @Override
    public HomeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Home model = residences.get(position);
        holder.getName().setText(model.getName());

        holder.getView().setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavigationHomeToItemsFragment();
            navController.navigate(action);
        });

        holder.getView().setOnLongClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavigationHomeToHomeEditFragment();
            navController.navigate(action);
            return true;
        });
        this.editbtn = holder.getView().findViewById(R.id.edit_btn);
        editbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionNavigationHomeToHomeEditFragment();
                navController.navigate(action);
            }
        });

    }

    @Override
    public int getItemCount() {
        return residences.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.home_name);
        }

        public TextView getName() {
            return name;
        }

        public View getView() {
            return this.itemView;
        }
    }
}

