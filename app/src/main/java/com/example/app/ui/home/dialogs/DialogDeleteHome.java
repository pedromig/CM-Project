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
import com.example.app.ui.home.models.HomeViewModel;
import com.google.firebase.database.FirebaseDatabase;

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
        builder.setMessage("Do you want to delete this home?")
                .setPositiveButton(R.string.delete, (dialog, id) -> {
                    db.getReference("homes").child(home.getKey()).removeValue();
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
