package com.example.app.ui.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PersonMultiSelectListAdapter extends RecyclerView.Adapter<PersonMultiSelectListAdapter.ViewHolder> {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<Person> people;
    private ArrayList<Person> selected;
    private Context context;

    public PersonMultiSelectListAdapter(Context context, ArrayList<Person> people, ArrayList<Person> selected) {
        this.context = context;
        this.people = people;
        this.selected = selected;
    }

    @NonNull
    @Override
    public PersonMultiSelectListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_share_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonMultiSelectListAdapter.ViewHolder holder, int position) {
        Person model = people.get(position);

        FirebaseUser user = this.auth.getCurrentUser();
        holder.getName().setText(model.getName());
        holder.getEmail().setText(model.getEmail());

        for (Person p : this.selected) {
            if (model.getKey().equals(p.getKey())) {
                holder.setSelected(true);
                holder.visualSelect();
                break;
            }
        }

        if (!user.getUid().equals(model.getKey()))  {
            // Set listeners
            holder.getView().setOnClickListener(v -> {
                holder.toggle(selected, model);
            });
            holder.getCheckBox().setOnClickListener(v -> {
                holder.toggle(selected, model);
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.people.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private final TextView name;
        private final TextView email;
        private boolean selected;


        public ViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.person_name);
            this.email = view.findViewById(R.id.person_email);
            this.checkBox = view.findViewById(R.id.check_box);
            this.checkBox.setChecked(false);
            this.selected = false;
        }

        public void toggle(ArrayList<Person> selected, Person model) {
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

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public TextView getName() {
            return name;
        }

        public TextView getEmail() {
            return email;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public View getView() {
            return this.itemView;
        }
    }

}
