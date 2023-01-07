package com.example.app.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

;import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.model.Person;
import com.example.app.ui.home.models.HomeViewModel;
import com.example.app.ui.home.models.ItemViewModel;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    private PersonViewModel viewModel;
    private ImageView picture;
    private TextView name;
    private TextView email;

    private Person user;
    private int selectedPerson;

    private Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.viewModel = new ViewModelProvider(requireActivity()).get(PersonViewModel.class);

        // Setup Action Bar
        setHasOptionsMenu(true);

        FirebaseUser user = this.auth.getCurrentUser();

        this.selectedPerson = 0;
        for (Person p : this.viewModel.getPeople()) {
            assert user != null;
            if (p.getKey().equals(user.getUid())){
                this.user = p;
                break;
            }
            ++this.selectedPerson;
        }

        // Setup Elements
        this.name = view.findViewById(R.id.name_text);
        this.email = view.findViewById(R.id.email_text);
        this.button = view.findViewById(R.id.logout_button);
        this.picture = view.findViewById(R.id.profile_picture);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.name.setText(this.user.getName());
        this.email.setText(this.user.getEmail());

        if (this.user.getPicture() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference().child(this.user.getPicture());
            ref.getDownloadUrl().addOnCompleteListener(task -> Picasso.get().load(task.getResult().toString()).into(this.picture));
        }

        this.button.setOnClickListener(v -> {
            // Sign Out Current User
            this.auth.signOut();
            this.auth.getCurrentUser();

            // Flush Data View Model Data
            HomeViewModel hvm = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
            ItemViewModel ivm = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
            hvm.getMembers().clear();
            hvm.getResidences().clear();
            ivm.getItems().clear();
            ivm.getOwners().clear();

            MainActivity activity = (MainActivity) requireActivity();
            activity.getMqttService().unsubscribeTopic("/cm/fridge-mates/" + auth.getUid());

            // Navigate to login fragment
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = ProfileFragmentDirections.actionNavigationProfileToLoginFragment();
            navController.navigate(action);
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment(this.selectedPerson);
            navController.navigate(action);
        }
        return true;
    }
}