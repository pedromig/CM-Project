package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> implements Filterable {
    private final Context context;

    private MenuItem check;
    private ArrayList<String> shoppingItems;
    private ArrayList<String> shoppingItemsFull;
    private ArrayList<String> selected;
    private int activated;

    public ShoppingListAdapter(Context context, ArrayList<String> shoppingItems, ArrayList<String> selected, MenuItem check) {
        this.context = context;
        this.selected = selected;
        this.shoppingItems = shoppingItems;
        this.shoppingItemsFull = new ArrayList<>(shoppingItems);
        this.check = check;
    }

    private final Filter filter = new Filter() {
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            Filter.FilterResults results = new Filter.FilterResults();
            ArrayList<String> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() <= 0) {
                filtered.addAll(shoppingItemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : shoppingItemsFull) {
                    if (item.toLowerCase().contains(filterPattern.toLowerCase())) {
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
            shoppingItems = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    };


    public void update() {
        this.shoppingItemsFull = new ArrayList<>(shoppingItems);
    }

    @NonNull
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_list_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = shoppingItems.get(position);
        holder.getName().setText(item);

        holder.getView().setOnClickListener(v -> {
            holder.toggle(selected, item);
            this.check.setVisible(selected.size() > 0);
        });
        holder.getCheckBox().setOnClickListener(v -> {
            holder.toggle(selected, item);
            this.check.setVisible(selected.size() > 0);
        });
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    @Override
    public Filter getFilter() {
        return this.filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;
        private final CheckBox checkBox;
        private boolean selected;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.item_name);
            this.picture = view.findViewById(R.id.item_image);
            this.checkBox = view.findViewById(R.id.check_box);
            this.checkBox.setChecked(false);
            this.selected = false;
        }

        public void toggle(ArrayList<String> selected, String model) {
            if (this.selected) {
                selected.remove(model);
                this.visualDeselect();

            } else {
                selected.add(model);
                this.visualSelect();
            }
            this.selected = !this.selected;
        }

        public void visualSelect() {
            this.checkBox.setChecked(true);
            this.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_selected_state));
        }

        public void visualDeselect() {
            this.checkBox.setChecked(false);
            this.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_normal_state));
        }

        public TextView getName() {
            return name;
        }

        public ImageView getPicture() {
            return picture;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public View getView() {
            return this.itemView;
        }
    }
}
