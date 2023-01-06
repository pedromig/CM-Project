package com.example.app.ui.home.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    private int selectedHome;

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
        this.selectedHome = bundle.getInt("selectedHome");
        this.home = viewModel.getResidences().get(this.selectedHome);

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.homeName.setText(this.home.getName());
        this.homeLocation.setText(this.home.getLocation());

        if (this.home.getMembers().size() > 1) {
            this.deleteButton.setText("Leave Home");
        }

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

        this.updateImage.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), updateImage);
            popupMenu.getMenuInflater().inflate(R.menu.photo_edit_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_camera) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ImagePickerCamera.launch(intent);
                }
                if (item.getItemId() == R.id.action_gallery) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    ImagePickerGallery.launch(intent);
                }
                return true;
            });
            popupMenu.show();
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
        if (item.getItemId() == R.id.action_home_people) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = HomeEditFragmentDirections.actionHomeEditFragmentToHomeAddPeopleFragment(this.selectedHome);
            navController.navigate(action);
        }
        return true;
    }

    ActivityResultLauncher<Intent> ImagePickerCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        picture.setImageBitmap(bitmap);
                    }
                }
            });

    ActivityResultLauncher<Intent> ImagePickerGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        picture.setImageURI(uri);
                    }
                }
            });
}