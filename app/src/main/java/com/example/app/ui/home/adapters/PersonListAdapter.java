package com.example.app.ui.home.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Person;

import java.util.ArrayList;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder> {
    private NavController navController;
    private final ArrayList<Person> people;

    public PersonListAdapter(ArrayList<Person> people, NavController navController) {
        this.people = people;
        this.navController = navController;
    }

    @NonNull
    @Override
    public PersonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person model = people.get(position);

        // Card Properties and Listeners
        holder.getName().setText(model.getName());
        holder.getEmail().setText(model.getEmail());
    }

    @Override
    public int getItemCount() {
        return this.people.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;
        private final TextView email;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.person_name);
            this.email = view.findViewById(R.id.person_email);
            this.picture = view.findViewById(R.id.profile_picture);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getPicture() {
            return picture;
        }

        public TextView getEmail() {
            return email;
        }
    }
}
