package com.example.app.ui.home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.model.Item;
import com.example.app.model.Person;
import com.example.app.ui.home.models.ItemViewModel;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

import kotlinx.coroutines.ObsoleteCoroutinesApi;

public class ItemEditFragment extends Fragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private ItemViewModel viewModel;
    private PersonViewModel personViewModel;
    private Item item;
    private String homeKey;

    private Uri imageUri;
    private ImageView itemImage;
    private FloatingActionButton changeItemImage;

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;

    private ImageButton moreButton;
    private ImageButton lessButton;
    private Button saveButton;

    private MaterialDatePicker datePicker;
    private Button datePickerButton;
    private Date expirationDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Setup View Models
        this.personViewModel = new ViewModelProvider(requireActivity()).get(PersonViewModel.class);
        this.viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        this.homeKey = bundle.getString("selectedHome");
        int i = bundle.getInt("selectedItem");
        this.item = i != Item.NEW_ITEM ? this.viewModel.getItems().get(i) : null;

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

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
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

        // Setup change item image
        this.itemImage = view.findViewById(R.id.item_image);
        this.changeItemImage = view.findViewById(R.id.edit_item_image_btn);
        this.imagePicker();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (this.item != null && this.item.getImage() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference().child(this.item.getImage());
            ref.getDownloadUrl().addOnCompleteListener(task -> Picasso.get().load(task.getResult().toString()).into(this.itemImage));
        }
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
            // Basic Fields
            String name = itemName.getText().toString();
            String price = itemPrice.getText().toString();
            int quantity = Integer.parseInt(itemQuantity.getText().toString());

            // Dates
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            if (expirationDate == null) {
                expirationDate = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
            }
            String date = format.format(this.expirationDate);

            if (name.isEmpty() || price.isEmpty() || date.isEmpty()) {
                Toast toast = Toast.makeText(this.getContext(), "Fields must not be empty.",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            // Owners
            ArrayList<String> owners = new ArrayList<>();
            if (!this.viewModel.getOwners().isEmpty()) {
                for (Person p : this.viewModel.getOwners()) {
                    if (!owners.contains(p.getKey())) {
                        owners.add(p.getKey());
                    }
                }
            } else {
                if (this.item != null) {
                    for (String p : this.item.getOwners()) {
                        if (!owners.contains(p)) {
                            owners.add(p);
                        }
                    }
                }
            }

            HashMap<String, Double> balance = new HashMap<>();
            if (item == null) {
                FirebaseUser user = auth.getCurrentUser();
                assert user != null;
                if (!owners.contains(user.getUid())) {
                    owners.add(user.getUid());
                }
                Item item = new Item(this.homeKey, name, Double.parseDouble(price), quantity, date, owners);
                if (this.imageUri != null) {
                    // Save Data Locally
                    String uid = UUID.randomUUID().toString();
                    item.setImage(uid);

                    // Save Date Upstream
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference ref = storage.getReference();
                    ref.child(uid).putFile(imageUri);
                }

                DatabaseReference ref = db.getReference("items").push();
                item.setKey(ref.getKey());
                ref.setValue(item);

                // Money
                double split = (Double.parseDouble(price) * quantity) / owners.size();
                for (String o : owners) {
                    for (Person s : this.personViewModel.getPeople()) {
                        if (s.getKey().equals(o)) {
                            balance.put(s.getKey(), split);
                            break;
                        }
                    }
                }

                System.out.println("----");
                for (String x : balance.keySet()) {
                    System.out.println(x + " " + balance.get(x));
                }
                System.out.println("----");

                // Message
                Toast toast = Toast.makeText(this.getContext(), "Item created successfully",
                        Toast.LENGTH_SHORT);
                toast.show();

                // PopBack Stack
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.popBackStack();
            } else {
                // Update Values Locally
                this.item.setName(name);
                this.item.setPrice(Double.parseDouble(price));
                this.item.setQuantity(quantity);
                this.item.setExpirationDate(date);
                this.item.setOwners(owners);

                if (this.imageUri != null) {
                    // Save Data Locally
                    String uid = UUID.randomUUID().toString();
                    item.setImage(uid);

                    // Save Date Upstream
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference ref = storage.getReference();
                    ref.child(uid).putFile(imageUri);
                }

                // Update Values Remote
                HashMap<String, Object> itemUpdates = new HashMap<>();
                itemUpdates.put("name", name);
                itemUpdates.put("price", Double.parseDouble(price));
                itemUpdates.put("quantity", quantity);
                DatabaseReference ref = db.getReference("items").child(item.getKey());
                ref.updateChildren(itemUpdates);
                ref.child("owners").setValue(owners);

                // Money
                double split = viewModel.getSavedAmount() / viewModel.getSaved().size();
                for (Person s : this.viewModel.getSaved()) {
                    balance.put(s.getKey(), -split);
                }

                double newAmount = Double.parseDouble(price) * quantity;
                viewModel.setSavedAmount(newAmount);
                double newSplit = (newAmount / this.viewModel.getOwners().size());
                for (Person o : this.viewModel.getOwners()) {
                    Double old = balance.getOrDefault(o.getKey(), 0.0);
                    assert old != null;
                    balance.put(o.getKey(), old + newSplit);
                }

                System.out.println("----");
                for (String x : balance.keySet()) {
                    System.out.println(x + " " + balance.get(x));
                }
                System.out.println("----");

                // Toast Message
                Toast toast = Toast.makeText(this.getContext(), "Item updated successfully",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

            FirebaseUser user = auth.getCurrentUser();
            db.getReference("profiles").child(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Person person = snapshot.getValue(Person.class);
                            DatabaseReference ref = db.getReference("profiles")
                                    .child(user.getUid()).child("debts");
                            assert person != null;
                            if (person.getDebts() == null) {
                                ref.push();
                                person.setDebts(new HashMap<>());
                            }
                            HashMap<String, Double> debts = person.getDebts();
                            for (String p : balance.keySet()) {
                                if (debts.containsKey(p)) {
                                    debts.put(p, debts.get(p) + balance.get(p));
                                } else {
                                    debts.put(p, balance.get(p));
                                }
                            }
                            ref.setValue(debts);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println(error);
                        }
                    });

            this.viewModel.setSet(false);
            if (this.viewModel.getSaved() != null) {
                this.viewModel.getSaved().clear();
            }
            // remove item -> delete or add to shopping list.
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.item_edit_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share_item) {
            // If item exists load owners
            if (this.item != null) {
                for (String owner : this.item.getOwners()) {
                    for (Person p : this.personViewModel.getPeople()) {
                        if (p.getKey().equals(owner) &&
                                !this.viewModel.getOwners().contains(p)) {
                            this.viewModel.addOwner(p);
                        }
                    }
                }
            } else {
                FirebaseUser current = auth.getCurrentUser();
                for (Person p : this.personViewModel.getPeople()) {
                    if (p.getKey().equals(current.getUid()) &&
                            !this.viewModel.getOwners().contains(p)) {
                        this.viewModel.addOwner(p);
                    }
                }
            }
            if (!this.viewModel.isSet()) {
                this.viewModel.setSaved(new ArrayList<>(this.viewModel.getOwners()));
                this.viewModel.setSet(true);
            }

            // System.out.println("Owners: " + this.viewModel.getOwners().size());
            // System.out.println("Saved: " + this.viewModel.getSaved().size());
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = ItemEditFragmentDirections.actionItemEditFragmentToItemShareFragment(this.homeKey);
            navController.navigate(action);
        }
        return true;
    }

    public void imagePicker() {
        this.changeItemImage.setOnClickListener(new View.OnClickListener() {
            final ActivityResultLauncher<Intent> ImagePickerCamera = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                Bundle bundle = result.getData().getExtras();
                                Bitmap bitmap = (Bitmap) bundle.get("data");
                                itemImage.setImageBitmap(bitmap);

                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                                        bitmap, "image", null);

                                imageUri = Uri.parse(path);
                                System.out.println(imageUri.toString());
                                itemImage.setImageURI(imageUri);
                            }
                        }
                    });

            final ActivityResultLauncher<Intent> ImagePickerGallery = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                imageUri = result.getData().getData();
                                itemImage.setImageURI(imageUri);
                            }
                        }
                    });

            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), changeItemImage);
                popupMenu.getMenuInflater().inflate(R.menu.photo_edit_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_camera) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ImagePickerCamera.launch(intent);
                    }
                    if (item.getItemId() == R.id.action_gallery) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ImagePickerGallery.launch(intent);
                    }
                    return true;
                });
                popupMenu.show();
            }
        });
    }
}

