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
import com.example.app.model.Home;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> implements Filterable {
    private final NavController navController;
    private ArrayList<Home> residences;
    private ArrayList<Home> residencesFull;

    private final Filter filter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            Filter.FilterResults results = new Filter.FilterResults();
            ArrayList<Home> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() <= 0) {
                filtered.addAll(residencesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Home item : residencesFull) {
                    if (item.getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                        filtered.add(item);
                    }
                }
            }
            results.values = filtered;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            residences = (ArrayList<Home>) results.values;
            notifyDataSetChanged();
        }
    };


    public HomeListAdapter(ArrayList<Home> residences, NavController navController) {
        this.residences = residences;
        this.residencesFull = new ArrayList<>(residences);
        this.navController = navController;
    }

    public void update() {
        this.residencesFull = new ArrayList<>(residences);
    }

    @NonNull
    @Override
    public HomeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Home model = residences.get(position);
        String location = model.getLocation();
        int residents = model.getMembers().size();

        // Card Properties and Listeners
        holder.getName().setText(model.getName());
        holder.getEditBtn().setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavigationHomeToHomeEditFragment(position);
            navController.navigate(action);
        });

        holder.getResidentsText().setText("Residents: " + residents);
        holder.getLocationText().setText("Location: " + (location.isEmpty() ? "?" : location));
        // Card General Listeners
        holder.getView().setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavigationHomeToItemsFragment(position);
            navController.navigate(action);
        });

        holder.getView().setOnLongClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionNavigationHomeToHomeEditFragment(position);
            navController.navigate(action);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return residences.size();
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageButton editBtn;
        private final TextView residentsText;
        private final TextView locationText;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.home_name);
            this.editBtn = view.findViewById(R.id.edit_btn);
            this.residentsText = view.findViewById(R.id.member_count);
            this.locationText = view.findViewById(R.id.location_name);
        }

        public TextView getName() {
            return name;
        }

        public ImageButton getEditBtn() {
            return editBtn;
        }

        public TextView getResidentsText() {
            return residentsText;
        }

        public TextView getLocationText() {
            return locationText;
        }

        public View getView() {
            return this.itemView;
        }
    }
}