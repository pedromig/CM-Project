package com.example.app.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.databinding.FragmentDashboardBinding;
import com.example.app.model.Person;
import com.example.app.ui.home.adapters.DashboardPersonListAdapter;
import com.example.app.ui.home.models.PersonViewModel;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;


    private ArrayList<Person> people;
    private ArrayList<Double> amounts;
    private TextView settled;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.people = new ArrayList<>();
        this.amounts = new ArrayList<>();


        this.recyclerView = view.findViewById(R.id.person_list);
        this.settled = view.findViewById(R.id.settled_up);

        FirebaseUser user = auth.getCurrentUser();
        db.getReference("profiles").child(user.getUid()).child("debts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        PersonViewModel personViewModel = new ViewModelProvider(requireActivity()).get(PersonViewModel.class);
                        people = new ArrayList<>();
                        amounts = new ArrayList<>();
                        GenericTypeIndicator<HashMap<String, Double>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Double>>() {
                        };
                        HashMap<String, Double> values = snapshot.getValue(genericTypeIndicator);
                        if (values != null) {
                            for (String k : values.keySet()) {
                                if (k.equals(user.getUid()))
                                    continue;
                                for (Person p : personViewModel.getPeople()) {
                                    if (k.equals(p.getKey())) {
                                        people.add(p);
                                        amounts.add(values.get(k));
                                        break;
                                    }
                                }
                            }

                            for (Person p : personViewModel.getPeople()) {
                                if (p.getKey().equals(user.getUid()))
                                    continue;
                                if (p.getDebts() != null) {
                                    if (p.getDebts().containsKey(user.getUid())) {
                                        boolean found = false;
                                        for (int i = 0; i < people.size(); ++i) {
                                            Person tmp = people.get(i);
                                            if (tmp.getKey().equals(p.getKey())) {
                                                amounts.set(i, amounts.get(i) - p.getDebts().get(user.getUid()));
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            people.add(p);
                                            amounts.add(-p.getDebts().get(user.getUid()));
                                        }
                                    }
                                }
                            }
                            ArrayList<Integer> remove = new ArrayList<>();
                            for (int i = 0; i < people.size(); ++i) {
                                if (Math.signum(amounts.get(i)) == 0) {
                                    remove.add(i);
                                }
                            }
                            for (int i : remove) {
                                amounts.remove(i);
                                people.remove(i);
                            }

                            if (people.size() > 0) {
                                settled.setVisibility(View.GONE);
                            }
                            DashboardPersonListAdapter dashboardPersonListAdapter =
                                    new DashboardPersonListAdapter(people, amounts, getChildFragmentManager());
                            recyclerView = view.findViewById(R.id.person_list);
                            recyclerView.setAdapter(dashboardPersonListAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}