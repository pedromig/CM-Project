package com.example.app.ui.home.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.model.Person;
import com.example.app.ui.home.models.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DialogDeleteHome extends DialogFragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private final Home home;
    private final HomeViewModel viewModel;

    public DialogDeleteHome(Home home, HomeViewModel viewModel) {
        this.home = home;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String action;
        if (this.home.getMembers().size() > 1) {
            action = "leave ";
        } else {
            action = "delete";
        }
        builder.setMessage("Do you want to " + action + " this home?")
                .setPositiveButton(action, (dialog, id) -> {
                    if (home.getMembers().size() > 1) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        home.getMembers().remove(user.getUid());
                        db.getReference("homes").child(home.getKey()).
                                child("members").setValue(home.getMembers());
                    } else {
                        db.getReference("homes").child(home.getKey()).removeValue();
                    }
                    this.viewModel.getResidences().remove(home);

                    NavController navController = Navigation.findNavController(requireActivity(),
                            R.id.nav_host_fragment_activity_main);
                    navController.popBackStack();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });
        return builder.create();
    }
}
