package com.example.app.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.model.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    // Firebase Auth
    private FirebaseAuth auth;

    private EditText email;
    private EditText password;
    private EditText confirm;

    private Button signUpButton;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Edit Text Fields
        this.email = view.findViewById(R.id.email);
        this.password = view.findViewById(R.id.password);
        this.confirm = view.findViewById(R.id.confirm_password);

        // Buttons
        this.signUpButton = view.findViewById(R.id.signup);
        this.loginButton = view.findViewById(R.id.login);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons
        this.loginButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
            navController.navigate(action);
        });

        this.signUpButton.setOnClickListener(v -> {
            String email = this.email.getText().toString();
            String password = this.password.getText().toString();
            String confirm = this.confirm.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(requireActivity(), "Email and Password/Confirm fields must not be empty!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "." + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
            if (!emailPattern.matcher(email).matches()) {
                Toast.makeText(requireActivity(), "Invalid Email Format!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(requireActivity(), "Password length must greater than 6 characters",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(requireActivity(), "Password Mismatch",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            createAccount(email, password);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) requireActivity()).getSupportActionBar().show();
    }

    private void createAccount(String email, String password) {
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        // Create Profile
                        Person person = new Person(email);
                        db.getReference("profiles").child(userId).setValue(person);

                        // Navigate Back
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                        NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
                        navController.navigate(action);
                    } else {
                        Toast.makeText(requireActivity(), "Sign Up Failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}