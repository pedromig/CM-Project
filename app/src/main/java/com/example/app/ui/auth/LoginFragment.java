package com.example.app.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.model.Person;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {
    private FirebaseAuth auth;

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private PersonViewModel viewModel;

    private ImageView loginImage;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button loginButton;
    private Button registerButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        // Image View
        this.loginImage = view.findViewById(R.id.app_image);

        // Edit Text
        this.editTextEmail = view.findViewById(R.id.email);
        this.editTextPassword = view.findViewById(R.id.password);

        // Buttons
        this.loginButton = view.findViewById(R.id.login);
        this.registerButton = view.findViewById(R.id.register);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = this.auth.getCurrentUser();
        if (user != null) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = LoginFragmentDirections.actionLoginFragmentToNavigationDashboard();
            navController.navigate(action);
        }
        ((MainActivity) requireActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) requireActivity()).getSupportActionBar().show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireActivity(), "Email and Password fields must not be empty!",
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
            signIn(email, password);
        });

        this.registerButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
            navController.navigate(action);
        });
    }

    private void signIn(String email, String password) {
        this.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                        NavDirections action = LoginFragmentDirections.actionLoginFragmentToNavigationDashboard();
                        navController.navigate(action);
                    } else {
                        Toast.makeText(requireActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}