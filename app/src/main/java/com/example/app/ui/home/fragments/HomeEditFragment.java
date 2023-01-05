package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.ui.home.dialogs.DialogDeleteHome;
import com.example.app.ui.home.models.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HomeEditFragment extends Fragment {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private HomeViewModel viewModel;
    private Home home;

    private Button saveButton;
    private Button deleteButton;
    private EditText homeName;
    private EditText homeLocation;
    private ImageView picture;
    private FloatingActionButton updateImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_home, container, false);

        // Fetch ViewModel and Build Adapter
        this.viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        this.home = viewModel.getResidences().get(bundle.getInt("selectedHome"));

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Buttons
        this.deleteButton = view.findViewById(R.id.delete_house);
        this.saveButton = view.findViewById(R.id.save_house);

        // Image
        this.picture = view.findViewById(R.id.house_image);
        this.updateImage = view.findViewById(R.id.upload_image);

        // EditText Fields
        this.homeName = view.findViewById(R.id.house_name);
        this.homeLocation = view.findViewById(R.id.location_name);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.homeName.setText(this.home.getName());
        this.homeLocation.setText(this.home.getLocation());

        this.deleteButton.setOnClickListener(v -> {
            DialogDeleteHome fragment = new DialogDeleteHome(this.home, this.viewModel);
            fragment.show(getChildFragmentManager(), "DeleteHomeDialog");
        });

        this.saveButton.setOnClickListener(v -> {
            String name = this.homeName.getText().toString();
            String location = this.homeLocation.getText().toString();
            if (name.isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Home name must not be empty!",
                        Toast.LENGTH_SHORT
                );
                toast.show();
                return;
            }

            // Update Values Locally
            this.home.setName(name);
            this.home.setLocation(location);

            // Update Database
            HashMap<String, Object> homeUpdates = new HashMap<>();
            homeUpdates.put("name", name);
            homeUpdates.put("location", location);
            db.getReference("homes").child(home.getKey()).updateChildren(homeUpdates);

            Toast toast = Toast.makeText(this.getContext(), "Home updated successfully!",
                    Toast.LENGTH_SHORT
            );
            toast.show();
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_home_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_person) {

        }
        return true;
    }
}