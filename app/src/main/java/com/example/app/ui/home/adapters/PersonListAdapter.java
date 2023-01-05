package com.example.app.ui.home.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Person;

import java.util.ArrayList;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.ViewHolder>{
    private final ArrayList<Person> people;

    public PersonListAdapter(ArrayList<Person> people) {
        this.people = people;
    }

    @NonNull
    @Override
    public PersonListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonListAdapter.ViewHolder holder, int position) {
        Person model = people.get(position);
        holder.getName().setText(model.getName());
        holder.getPhoneNumber().setText(model.getPhoneNumber());
        holder.getPicture().setImageDrawable(Drawable.createFromPath(model.getPicture()));
        // holder.getView().setOnClickListener(v -> {

        // });
        // holder.getView()
        //         .setOnLongClickListener(new NoteLongClickListener(position, fragmentManager));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView picture;
        private final TextView name;
        private final TextView phoneNumber;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.home_name);
            this.phoneNumber = view.findViewById(R.id.person_phone_number);
            this.picture = view.findViewById(R.id.profile_picture);
        }

        public TextView getName() {
            return name;
        }

        public ImageView getPicture() {
            return picture;
        }

        public TextView getPhoneNumber() {
            return phoneNumber;
        }
    }
}
