package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.ui.home.adapters.ItemListAdapter;
import com.example.app.ui.home.models.Item;
import com.example.app.ui.home.models.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class ItemsFragment extends Fragment {
    private ItemViewModel viewModel;

    private RecyclerView itemRecyclerView;
    private FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_items, container, false);

        // Fetch Items
        this.viewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        ItemListAdapter itemListAdapter = new ItemListAdapter(this.viewModel.getItems());

        // Recycler View Layout Manager
        LinearLayoutManager itemLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        // Setup Action Bar
        setHasOptionsMenu(true);

        // Floating Action Button
        this.floatingActionButton = view.findViewById(R.id.add_item);

        this.itemRecyclerView = view.findViewById(R.id.items_list);
        this.itemRecyclerView.setLayoutManager(itemLayoutManager);
        this.itemRecyclerView.setAdapter(itemListAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        this.floatingActionButton.setOnClickListener(v -> {
            // Build Information Bundle for sharing state with Edit Item Fragment
            Bundle bundle = new Bundle();
            bundle.putInt("selectedItem", Item.NEW_ITEM);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            NavDirections action = ItemsFragmentDirections.actionItemsFragmentToItemAddFragment();
            navController.navigate(action);
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.item_bar_menu, menu);

        //Setup ShoppingList Button to Change to Shopping List Fragment
        MenuItem shoppinglist = menu.findItem(R.id.action_shopping_list);

        // Setup Filter Button Properties
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();

        // Setup Text Field and Hints
        EditText searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHint("Search Item");
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Setup Filter Button Listener
        // searchView.setOnQueryTextListener(new ResidencesFilterListener((HomeListAdapter) this.homeRecyclerView.getAdapter()));
        super.onCreateOptionsMenu(menu, inflater);
    }
}