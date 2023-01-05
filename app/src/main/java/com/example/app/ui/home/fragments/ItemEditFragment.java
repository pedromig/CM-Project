package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.model.Item;
import com.example.app.ui.home.models.ItemViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

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

    private MaterialDatePicker datePicker;
    private Button datePickerButton;
    private Date expirationDate;
    private int home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Setup View Model
        assert getParentFragment() != null;
        this.viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        this.homeKey = bundle.getString("selectedHome");
        int i = bundle.getInt("selectedItem");
        this.item = i != Item.NEW_ITEM ? this.viewModel.getItems().get(i) : null;

        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(i != Item.NEW_ITEM ? "Edit " + item.getName() : "New Item");

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


        CalendarConstraints.Builder  constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setStart(Date.from(Instant.now()).getTime());
        materialDateBuilder.setCalendarConstraints(constraintsBuilder.build());

        if (this.item != null) {
            this.itemName.setText(this.item.getName());
            this.itemPrice.setText(String.valueOf(this.item.getPrice()));
            this.itemQuantity.setText(String.valueOf(this.item.getQuantity()));
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date date = format.parse(this.item.getExpirationDate());
                this.expirationDate = new Date(date.getTime());
                materialDateBuilder.setSelection(this.expirationDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        this.datePicker = materialDateBuilder.build();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Date Picker
        this.datePicker.addOnPositiveButtonClickListener(
                selection -> {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.setTimeInMillis((Long) selection);
                    expirationDate = calendar.getTime();
                });

        this.datePickerButton.setOnClickListener(v -> {
            datePicker.show(requireActivity().getSupportFragmentManager(), "DatePicker");
        });

        // Setup Button Actions
        this.moreButton.setOnClickListener(v -> {
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
            int quantity = Integer.parseInt(itemQuantity.getText().toString());
            if (expirationDate == null) {
                Toast toast = Toast.makeText(this.getContext(), "Fields must not be empty.",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            if (expirationDate.getTime() < Date.from(Instant.now()).getTime()) {
                Toast toast = Toast.makeText(this.getContext(), "Expiration date must be in the future.",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String date = format.format(this.expirationDate);

            if (name.isEmpty() || price.isEmpty() || date.isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Fields must not be empty.",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            if (item == null) {
                Item item = new Item(this.homeKey, name, Double.parseDouble(price), quantity, date);
                DatabaseReference ref = db.getReference("items").push();
                item.setKey(ref.getKey());
                ref.setValue(item);

                Toast toast = Toast.makeText(this.getContext(), "Item created successfully",
                        Toast.LENGTH_SHORT);
                toast.show();

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.popBackStack();
            } else {

                // Update Values Locally
                this.item.setName(name);
                this.item.setPrice(Double.parseDouble(price));
                this.item.setQuantity(quantity);
                this.item.setExpirationDate(date);

                // Update Values Remote
                HashMap<String, Object> itemUpdates = new HashMap<>();
                itemUpdates.put("name", name);
                itemUpdates.put("price", price);
                itemUpdates.put("quantity", quantity);
                itemUpdates.put("price", Double.parseDouble(price));
                db.getReference("items").child(item.getKey()).updateChildren(itemUpdates);

                Toast toast = Toast.makeText(this.getContext(), "Item updated successfully",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}

