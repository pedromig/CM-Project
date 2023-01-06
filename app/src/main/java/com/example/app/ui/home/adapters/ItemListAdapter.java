package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.model.Item;
import com.example.app.ui.home.fragments.ItemsFragment;
import com.example.app.ui.home.fragments.ItemsFragmentDirections;
import com.example.app.ui.home.models.ItemViewModel;

import java.sql.Array;
import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> implements Filterable {
    private ItemViewModel viewModel;
    private Home home;

    private ArrayList<Item> items;
    private ArrayList<Item> itemsFull;
    private final NavController navController;


    public ItemListAdapter(ArrayList<Item> items, Home home, NavController navController, ItemViewModel viewModel) {
        this.items = items;
        this.home = home;
        this.itemsFull = new ArrayList<>(items);
        this.navController = navController;
        this.viewModel = viewModel;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item model = items.get(position);
        holder.getName().setText(model.getName());
        holder.getName().setText(model.getName());

        holder.getQuantity().setText("Quantity: " + model.getQuantity());
        holder.getExpiration().setText("Expiration Date: " + model.getExpirationDate());

        // Card General Listeners
        holder.getView().setOnClickListener(v -> {
            // Flush owners
            if (this.viewModel.getOwners() != null) {
                this.viewModel.getOwners().clear();
            }
            NavDirections action = ItemsFragmentDirections.actionItemsFragmentToItemEditFragment(position, home.getKey());
            navController.navigate(action);
        });

        holder.getView().setOnLongClickListener(v -> true);
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
        private final ImageView picture;
        private final TextView name;
        private final TextView expiration;
        private final TextView quantity;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.item_name);
            this.picture = view.findViewById(R.id.item_image);
            this.expiration = view.findViewById(R.id.expiration_date);
            this.quantity = view.findViewById(R.id.quantity);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getPicture() {
            return picture;
        }

        public TextView getExpiration() {
            return expiration;
        }

        public TextView getQuantity() {
            return quantity;
        }

        public View getView() {
            return this.itemView;
        }
    }
}
