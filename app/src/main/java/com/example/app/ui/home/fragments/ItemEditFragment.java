package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.app.R;
import com.example.app.model.Item;
import com.example.app.ui.home.models.ItemViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.FirebaseDatabase;

public class ItemEditFragment extends Fragment {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private ItemViewModel viewModel;
    private Item item;
    private String homeKey;

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;

    private ImageButton moreButton;
    private ImageButton lessButton;

    private Button saveButton;

    MaterialDatePicker datePicker;
    private Button datePickerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;

        this.homeKey = bundle.getString("selectedHome");
        int i = bundle.getInt("selectedItem");
        this.item = i != Item.NEW_ITEM ? viewModel.getItems().get(i) : null;

        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(i != Item.NEW_ITEM ? item.getName() : "New Item");

        // Setup View Model
        this.viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        // Setup Edit Text
        this.itemName = view.findViewById(R.id.item_name);
        this.itemPrice = view.findViewById(R.id.item_price);
        this.itemQuantity = view.findViewById(R.id.item_quantity);

        // Setup Buttons
        this.moreButton = view.findViewById(R.id.more_btn);
        this.lessButton = view.findViewById(R.id.less_btn);
        this.saveButton = view.findViewById(R.id.save_item);
        this.datePickerButton = view.findViewById(R.id.pick_date_button);

        // Date Picker
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Expiration Date");
        datePicker = materialDateBuilder.build();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Date Picker
        datePickerButton.setOnClickListener(v -> {
            datePicker.show(getChildFragmentManager(), "DatePicker");
        });

        // Setup Button Actions
        moreButton.setOnClickListener(v -> {
            String value = itemQuantity.getText().toString();
            int quantity = Integer.parseInt(value);
            quantity++;
            itemQuantity.setText(Integer.toString(quantity));
        });

        this.lessButton.setOnClickListener(v -> {
            String value = itemQuantity.getText().toString();
            int quantity = Integer.parseInt(value);
            if (quantity > 1)
                quantity--;
            this.itemQuantity.setText(Integer.toString(quantity));
        });

        this.saveButton.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            String price = itemPrice.getText().toString();
            String quantity = itemQuantity.getText().toString();
            String date = datePicker.getHeaderText();
            System.out.println(date);

            if (item == null) {
                // Item item = new Item(this.home, name, price, quantity, );
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}

