package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.model.Item;
import com.example.app.ui.home.fragments.ItemsFragment;
import com.example.app.ui.home.fragments.ItemsFragmentDirections;

import java.sql.Array;
import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> implements Filterable {
    private Home home;
    private ArrayList<Item> items;
    private ArrayList<Item> itemsFull;
    private final NavController navController;

    public ItemListAdapter(ArrayList<Item> items, Home home, NavController navController) {
        this.items = items;
        this.home = home;
        this.itemsFull = new ArrayList<>(items);
        this.navController = navController;
    }

    private final Filter filter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            Filter.FilterResults results = new Filter.FilterResults();
            ArrayList<Item> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() <= 0) {
                filtered.addAll(itemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Item item : itemsFull) {
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
            items = (ArrayList<Item>) results.values;
            notifyDataSetChanged();
        }
    };


    public void update() {
        this.itemsFull = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item model = items.get(position);
        holder.getName().setText(model.getName());
        holder.getName().setText(model.getName());

        // Card General Listeners
        holder.getView().setOnClickListener(v -> {
            NavDirections action = ItemsFragmentDirections.actionItemsFragmentToItemEditFragment(position, home.getKey());
            navController.navigate(action);
        });

        holder.getView().setOnLongClickListener(v -> true);

        holder.getEditBtn().setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageButton editBtn;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.item_name);
            this.editBtn = view.findViewById(R.id.edit_btn);
        }

        public TextView getName() {
            return name;
        }

        public ImageButton getEditBtn() {
            return editBtn;
        }

        public View getView() {
            return this.itemView;
        }
    }
}
