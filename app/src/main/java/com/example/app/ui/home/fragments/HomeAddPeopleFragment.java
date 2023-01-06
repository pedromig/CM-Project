package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;
import com.example.app.model.Home;
import com.example.app.model.Person;
import com.example.app.ui.home.adapters.PersonListAdapter;
import com.example.app.ui.home.dialogs.DialogAddPersonToHome;
import com.example.app.ui.home.models.HomeViewModel;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeAddPeopleFragment extends Fragment {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private ChildEventListener membersEventListener;

    private Home home;
    private HomeViewModel viewModel;
    private PersonViewModel personViewModel;

    private RecyclerView recyclerView;
    private FloatingActionButton button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_add_people, container, false);

        this.personViewModel = new ViewModelProvider(requireActivity()).get(PersonViewModel.class);
        this.viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        if (this.viewModel.getMembers() != null) {
            this.viewModel.getMembers().clear();
        }

        // Fetch arguments passed to the fragment
        Bundle bundle = this.getArguments();
        assert bundle != null;
        this.home = viewModel.getResidences().get(bundle.getInt("selectedHome"));

        // Recycler View Settings
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        PersonListAdapter personListAdapter = new PersonListAdapter(this.viewModel.getMembers(), navController);
        this.recyclerView = view.findViewById(R.id.people_list);
        this.recyclerView.setAdapter(personListAdapter);
        this.setupRecyclerViewFirebaseListeners();

        // Components
        this.button = view.findViewById(R.id.add_person);
        this.recyclerView = view.findViewById(R.id.people_list);

        return view;
    }

    private void setupRecyclerViewFirebaseListeners() {
        // Setup Firebase Actions on Incoming Changes
        this.membersEventListener = db.getReference("homes")
                .child(this.home.getKey())
                .child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, String s) {
                        String member = snapshot.getValue(String.class);
                        for (Person person : personViewModel.getPeople()) {
                            if (person.getKey().equals(member) && !viewModel.getMembers().contains(person)) {
                                viewModel.addMember(person);

                                PersonListAdapter adapter = (PersonListAdapter) recyclerView.getAdapter();
                                assert adapter != null;
                                adapter.notifyItemInserted(viewModel.getMembers().size() - 1);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        String member = snapshot.getValue(String.class);
                        Person person = null;
                        for (Person p : personViewModel.getPeople()) {
                            if (p.getKey().equals(member)) {
                                person = p;
                            }
                        }
                        if (person != null) {
                            for (int i = 0; i < viewModel.getMembers().size(); ++i) {
                                Person m = viewModel.getMembers().get(i);
                                if (m.getKey().equals(person.getKey())) {
                                    viewModel.getMembers().remove(i);
                                    PersonListAdapter adapter = (PersonListAdapter) recyclerView.getAdapter();
                                    assert adapter != null;
                                    adapter.notifyItemRemoved(i);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.button.setOnClickListener(v -> {
            DialogAddPersonToHome fragment = new DialogAddPersonToHome(home);
            fragment.show(getChildFragmentManager(), "AddPersonDialog");
        });
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onStop() {
        super.onStop();
        this.db.getReference().removeEventListener(this.membersEventListener);
    }
}