package com.example.app.ui.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.ui.home.models.Item;

import java.util.ArrayList;

public class ItemShoppingListAdapter extends RecyclerView.Adapter<ItemShoppingListAdapter.ViewHolder>{

    private final ArrayList<Item> items;
    private Button lessbtn;
    private Button morebtn;
    private Button editbtn;
    private TextView quantitytxt;
    private Integer quantity;

    public ItemShoppingListAdapter(ArrayList<Item> items) {this.items = items;}

    @NonNull
    @Override
    public ItemShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_item_card, parent, false);
        return new ItemShoppingListAdapter.ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull ItemShoppingListAdapter.ViewHolder holder, int position) {
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

    public ItemShoppingListAdapter(@NonNull View view, ArrayList<Item> items, ArrayList<Item> items1) {
        this.items = items1;
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
