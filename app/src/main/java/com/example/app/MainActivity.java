package com.example.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.app.model.Person;
import com.example.app.ui.dashboard.DashboardFragment;
import com.example.app.ui.dashboard.MQTT;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase db;

    private String CHANNEL_ID = "FridgeMates";
    private MQTT mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Firebase
        FirebaseApp.initializeApp(this);
        this.db = FirebaseDatabase.getInstance();
        this.db.setPersistenceEnabled(true);
        this.setupFirebaseProfilesListener();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.mqttService = new MQTT(getApplicationContext(), "FridgeMates", "lol");
        this.setupMqttService();

        this.createNotificationsChannel();

        // Setup App Bar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();

        // Setup Nav Controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navController.addOnDestinationChangedListener((controller, destination, bundle) -> {
            int layout = destination.getId();
            if (layout == R.id.login_fragment || layout == R.id.register_fragment) {
                navigationView.setVisibility(View.GONE);
            } else {
                navigationView.setVisibility(View.VISIBLE);
            }
        });

        // Add listener for controller back button
        toolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // ActionBar App Bar Settings
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setOnItemSelectedListener(item -> {
            navController.navigate(item.getItemId());
            return true;
        });
    }

    public MQTT getMqttService() {
        return mqttService;
    }

    private void setupFirebaseProfilesListener() {
        // Setup Firebase Actions on Incoming Changes To Profiles
        PersonViewModel viewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        db.getReference("profiles").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                Person person = snapshot.getValue(Person.class);
                if (person != null) {
                    for (Person p : viewModel.getPeople()) {
                        if (p.getKey().equals(person.getKey())) {
                            return;
                        }
                    }
                    person.setKey(snapshot.getKey());
                    viewModel.addPerson(person);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {
                Person person = snapshot.getValue(Person.class);
                if (person != null) {
                    for (int i = 0; i < viewModel.getPeople().size(); ++i) {
                        Person p = viewModel.getPeople().get(i);
                        if (p.getKey().equals(snapshot.getKey())) {
                            person.setKey(snapshot.getKey());
                            viewModel.getPeople().set(i, person);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < viewModel.getPeople().size(); ++i) {
                    Person p = viewModel.getPeople().get(i);
                    if (p.getKey().equals(snapshot.getKey())) {
                        viewModel.getPeople().remove(i);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setupMqttService() {
        this.getMqttService().setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.err.println("CONNECTED: " + serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.err.println("CONNECTION LOST!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println(message.toString());
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                        CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Payment Received!")
                        .setContentText(message.toString())
                        .setPriority(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(1, builder.build());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("DELIVERED!");
            }
        });
        mqttService.connect();
    }

    private void createNotificationsChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ThirdChallenge", importance);
        channel.setDescription("Fridge Mates Notifications");
        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}