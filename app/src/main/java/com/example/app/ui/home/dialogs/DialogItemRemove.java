package com.example.app.ui.home.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app.R;
import com.example.app.model.Item;
import com.example.app.model.Person;
import com.example.app.ui.home.adapters.ItemListAdapter;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DialogItemRemove extends DialogFragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private ItemListAdapter adapter;
    private int position;

    private final Item item;

    public DialogItemRemove(Item item, ItemListAdapter adapter, int position) {
        this.item = item;
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_remove_item_dialog, null);

        builder.setView(view).setMessage("How do you want to proceed with this item?")
                .setPositiveButton("Add To Shopping List", (dialog, id) -> {
                })
                .setNegativeButton("Delete", (dialog, id) -> {
                });

        FirebaseUser user = auth.getCurrentUser();
        AlertDialog dialog = builder.create();
        dialog.show();

        // Customize Alert Dialog Button Behaviour
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setOnClickListener(v -> {
            System.out.println("Add to Shopping List");

            PersonViewModel viewModel = new ViewModelProvider(requireActivity())
                    .get(PersonViewModel.class);
            DatabaseReference ref = db.getReference("profiles").child(user.getUid())
                    .child("shopping");
            for (Person p : viewModel.getPeople()) {
                if (p.getKey().equals(user.getUid())) {
                    if (p.getShopping() == null) {
                        ref.push();
                        p.setShopping(new ArrayList<>());
                    }
                    p.getShopping().add(this.item.getName());
                    ref.setValue(p.getShopping());
                    break;
                }
            }
            Toast.makeText(requireActivity(), "Item added to shopping list",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        Button pos = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pos.setOnClickListener(v -> {
            DatabaseReference ref = db.getReference("items")
                    .child(item.getKey()).child("owners");
            for (String owner : this.item.getOwners()) {
                if (owner.equals(user.getUid())) {
                    this.item.getOwners().remove(owner);
                    this.adapter.notifyItemRemoved(position);
                    break;
                }
            }
            ref.setValue(this.item.getOwners());
            Toast.makeText(requireActivity(), "Item removed successfully!",
                    Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        return dialog;
    }
}
