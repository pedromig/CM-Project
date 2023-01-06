package com.example.app.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.model.Person;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;


public class EditProfileFragment extends Fragment {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private PersonViewModel viewModel;
    private Person user;

    private ImageView picture;

    private EditText password;
    private EditText email;
    private EditText name;

    private Button saveButton;
    private FloatingActionButton editProfileButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        this.viewModel = new ViewModelProvider(requireActivity()).get(PersonViewModel.class);

        Bundle bundle = this.getArguments();
        assert bundle != null;
        int i = bundle.getInt("selectedPerson");
        this.user = this.viewModel.getPeople().get(i);

        this.saveButton = view.findViewById(R.id.save_profile);
        this.editProfileButton = view.findViewById(R.id.edit_profile_picture_button);
        this.picture = view.findViewById(R.id.profile_picture);
        this.name = view.findViewById(R.id.edit_name);
        this.email = view.findViewById(R.id.edit_email);
        this.password = view.findViewById(R.id.edit_password);
        return view;
    }


    public void imagePicker() {
        this.editProfileButton.setOnClickListener(new View.OnClickListener() {
            final ActivityResultLauncher<Intent> ImagePickerCamera = registerForActivityResult(
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

            final ActivityResultLauncher<Intent> ImagePickerGallery = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                Uri uri = result.getData().getData();
                                picture.setImageURI(uri);
                            }
                        }
                    });

            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), editProfileButton);
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
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.imagePicker();

        this.name.setText(this.user.getName());
        this.email.setText(this.user.getEmail());

        this.saveButton.setOnClickListener(v -> {
            String name = this.name.getText().toString();
            String email = this.email.getText().toString();
            String password = this.password.getText().toString();

            if (email.isEmpty() || name.isEmpty()) {
                Toast.makeText(requireActivity(), "Name/Emails Fields must not be empty!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "." + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
            if (!emailPattern.matcher(email).matches()) {
                Toast.makeText(requireActivity(), "Invalid Email Format!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            /* Update Firebase Auth*/FirebaseUser current = this.auth.getCurrentUser();
            if (!password.isEmpty()) {
                if (password.length() < 6) {
                    Toast.makeText(requireActivity(), "Password length must be greater than 6 characters",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                current.updatePassword(password);
                return;
            }

            // Save Data Locally
            this.user.setName(name);
            this.user.setEmail(email);

            // Save Data Upstream
            DatabaseReference ref = this.db.getReference().child("profiles").child(this.user.getKey());
            ref.child("name").setValue(name);
            ref.child("email").setValue(email);
            assert current != null;
            current.updateEmail(email);
            Toast.makeText(requireActivity(), "Profile updated successfully!",
                    Toast.LENGTH_SHORT).show();
        });
    }
}