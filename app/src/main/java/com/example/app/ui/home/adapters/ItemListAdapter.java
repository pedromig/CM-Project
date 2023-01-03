package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.ui.home.models.Home;
import com.example.app.ui.home.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private final ArrayList<Item> items;

    public ItemListAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ViewHolder holder, int position) {
        Item model = items.get(position);
        holder.getName().setText(model.getName());
        holder.getView().setOnClickListener(v -> {
        });

        // holder.getView()
        //         .setOnLongClickListener(new NoteLongClickListener(position, fragmentManager));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.item_name);
        }

        public TextView getName() {
            return name;
        }

        public View getView() {
            return this.itemView;
        }
    }

}
