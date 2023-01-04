package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.ui.home.fragments.HomeFragmentDirections;
import com.example.app.ui.home.models.Home;
import com.example.app.ui.home.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private final ArrayList<Item> items;
    private Button lessbtn;
    private Button morebtn;
    private Button editbtn;
    private TextView quantitytxt;
    private NavController navController;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item model = items.get(position);
        holder.getName().setText(model.getName());
        holder.getView().setOnClickListener(v -> {
        });

        this.lessbtn = holder.getView().findViewById(R.id.less_btn);
        this.morebtn = holder.getView().findViewById(R.id.more_btn);
        this.editbtn = holder.getView().findViewById(R.id.edit_btn);
        this.quantitytxt = holder.getView().findViewById(R.id.quantityValue);

        morebtn.setOnClickListener(v -> {
            String value= quantitytxt.getText().toString();
            Integer quantity=Integer.parseInt(value);
            quantity++;
            quantitytxt.setText(quantity.toString());
        });
        lessbtn.setOnClickListener(v -> {
            String value= quantitytxt.getText().toString();
            Integer quantity=Integer.parseInt(value);
            if (quantity >1)
                quantity--;
            quantitytxt.setText(quantity.toString());
        });
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
