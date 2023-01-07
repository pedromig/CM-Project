package com.example.app.ui.home.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DialogAddShoppingListItem extends DialogFragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final ArrayList<String> shoppingItems;
    public DialogAddShoppingListItem(ArrayList<String> items) {
        this.shoppingItems = items;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_item_dialog, null);
        EditText itemName = view.findViewById(R.id.dialog_item_name);

        builder.setView(view)
                .setPositiveButton("Add", (dialog, id) -> {
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize Alert Dialog Button Behaviour
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            if (name.isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Item name must not be empty!",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            DatabaseReference ref = db.getReference().child("profiles")
                    .child(Objects.requireNonNull(auth.getUid())).child("shopping");
            shoppingItems.add(name);
            ref.setValue(shoppingItems);

            Toast toast = Toast.makeText(this.getContext(), "Item added successfully!",
                    Toast.LENGTH_SHORT);
            toast.show();
            dialog.dismiss();
        });
        return dialog;
    }
}
