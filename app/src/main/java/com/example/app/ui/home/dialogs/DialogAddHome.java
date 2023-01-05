package com.example.app.ui.home.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

        builder.setView(view)
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    Home home = new Home(homeName.getText().toString());
                    DatabaseReference ref = db.getReference("homes").push();
                    home.setKey(ref.getKey());
                    ref.setValue(home);

                    Toast toast = Toast.makeText(this.getContext(), "Home added successfully!",
                            Toast.LENGTH_SHORT);
                    toast.show();

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});
        return builder.create();
    }
}
