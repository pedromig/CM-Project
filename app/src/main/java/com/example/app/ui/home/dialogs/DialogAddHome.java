package com.example.app.ui.home.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.ui.home.models.HomeViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogAddHome extends DialogFragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_home_dialog, null);
        EditText homeName = view.findViewById(R.id.dialog_home_name);

        builder.setView(view).setMessage("Note Title")
                .setPositiveButton("Add", (dialog, id) -> {
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize Alert Dialog Button Behaviour
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            String name = homeName.getText().toString();
            if (name.isEmpty()) {

                Toast toast = Toast.makeText(this.getContext(), "Home name must not be empty!",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            Home home = new Home(name);
            DatabaseReference ref = db.getReference("homes").push();
            home.setKey(ref.getKey());
            ref.setValue(home);

            Toast toast = Toast.makeText(this.getContext(), "Home added successfully!",
                    Toast.LENGTH_SHORT);
            toast.show();
            dialog.dismiss();
        });
        return dialog;
    }
}
