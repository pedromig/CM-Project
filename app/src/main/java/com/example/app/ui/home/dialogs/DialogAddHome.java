package com.example.app.ui.home.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.example.app.ui.home.models.Home;
import com.example.app.ui.home.models.HomeViewModel;

public class DialogAddHome extends DialogFragment {

    private final HomeViewModel viewModel;

    public DialogAddHome(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_home_dialog, null);

        EditText homeName = view.findViewById(R.id.dialog_home_name);
        builder.setView(view)
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    this.viewModel.addResidence(new Home(homeName.getText().toString()));
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});
        return builder.create();
    }

}
