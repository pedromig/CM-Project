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
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.app.MainActivity;
import com.example.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EditProfileFragment extends Fragment {

    private FloatingActionButton editProfPhotoBtn;
    private ImageView profilePicture;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        this.editProfPhotoBtn = view.findViewById(R.id.editProfilePictureBtn);
        this.profilePicture = view.findViewById(R.id.profilePicture);

        editProfPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), editProfPhotoBtn);
                popupMenu.getMenuInflater().inflate(R.menu.photo_edit_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_camera) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            ImagePickerCamera.launch(intent);
                        }
                        if (item.getItemId() == R.id.action_gallery) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            ImagePickerGallery.launch(intent);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }

        });
        return view;
    }

    ActivityResultLauncher<Intent> ImagePickerCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        profilePicture.setImageBitmap(bitmap);
                    }
                }
            });

    ActivityResultLauncher<Intent> ImagePickerGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri =  result.getData().getData();
                        profilePicture.setImageURI(uri);
                    }
                }
            });
}