package com.example.app.ui.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.model.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Pattern;

public class DialogAddPersonToHome extends DialogFragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private Home home;

    public DialogAddPersonToHome(Home home) {
        this.home = home;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_person_dialog, null);
        EditText personEmail = view.findViewById(R.id.dialog_home_name);

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
            String email = personEmail.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(requireActivity(), "Email must not be empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "." + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
            if (!emailPattern.matcher(email).matches()) {
                Toast.makeText(requireActivity(), "Invalid Email Format!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            db.getReference("profiles").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                String key = snap.getKey();
                                Person profile = snap.getValue(Person.class);
                                assert profile != null;
                                if (profile.getEmail().equals(email)) {
                                    // Add Member Locally
                                    home.getMembers().add(key);

                                    // Update Info Upstream
                                    HashMap<String, Object> homeUpdates = new HashMap<>();
                                    homeUpdates.put("members", home.getMembers());
                                    db.getReference("homes")
                                            .child(home.getKey())
                                            .updateChildren(homeUpdates);

                                    Toast.makeText(requireActivity(), "Resident added successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    return;
                                }
                            }
                            Toast.makeText(requireActivity(), "User not found!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println(error);
                        }
                    }
            );
        });
        return dialog;
    }
}
