package com.example.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

;import com.example.app.R;
import com.example.app.ui.home.models.HomeViewModel;
import com.example.app.ui.home.models.ItemViewModel;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private Button button;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.auth = FirebaseAuth.getInstance();
        this.button = view.findViewById(R.id.logout_button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            // Navigate to login fragment
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = ProfileFragmentDirections.actionNavigationProfileToLoginFragment();
            navController.navigate(action);
        });
    }
}