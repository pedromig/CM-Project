package com.example.app.ui.home.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Person;
import com.example.app.ui.home.dialogs.DialogPayDebt;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DashboardPersonListAdapter extends RecyclerView.Adapter<DashboardPersonListAdapter.ViewHolder> {
    private final ArrayList<Person> people;
    private final ArrayList<Double> amount;
    private FragmentManager fragmentManager;

    public DashboardPersonListAdapter(ArrayList<Person> people, ArrayList<Double> amount,
                                      FragmentManager fragmentManager) {
        this.people = people;
        this.amount = amount;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public DashboardPersonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.debt_list_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person model = people.get(position);
        Double money = amount.get(position);
        if (model.getPicture() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference().child(model.getPicture());
            ref.getDownloadUrl().addOnCompleteListener(task -> Picasso.get().load(task.getResult().toString()).into(holder.getPicture()));
        }
        // Card Properties and Listeners
        holder.getName().setText(model.getName());
        if (money > 0) {
            holder.getAmount().setText(" - " + money + "€");
            holder.getAmount().setTextColor(Color.RED);
            holder.getView().setOnClickListener(v -> {
                DialogPayDebt fragment = new DialogPayDebt(model, money, position);
                fragment.show(this.fragmentManager, "RemoveItemDialog");
            });
        } else {
            holder.getAmount().setText(" + " + Math.abs(money) + "€");
            holder.getAmount().setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return this.people.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;
        private final TextView amount;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.person_name);
            this.amount = view.findViewById(R.id.amount);
            this.picture = view.findViewById(R.id.profile_picture);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getPicture() {
            return picture;
        }

        public TextView getAmount() {
            return amount;
        }

        public View getView() {
            return this.itemView;
        }
    }
}
