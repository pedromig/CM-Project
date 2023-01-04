package com.example.app.ui.home.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.app.R;

import org.w3c.dom.Text;

public class ItemAddFragment extends Fragment {

    private ImageButton morebtn;
    private ImageButton lessbtn;
    private EditText quantitytxt;
    private Integer quantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_add, container, false);

        this.quantitytxt = view.findViewById(R.id.editTextNumber);
        this.morebtn = view.findViewById(R.id.more_btn);
        this.lessbtn = view.findViewById(R.id.less_btn);

        morebtn.setOnClickListener(v -> {
            String value= quantitytxt.getText().toString();
            quantity=Integer.parseInt(value);
            quantity++;
            quantitytxt.setText(quantity.toString());
        });
        lessbtn.setOnClickListener(v -> {
            String value= quantitytxt.getText().toString();
            quantity=Integer.parseInt(value);
            if (quantity >1)
                quantity--;
            quantitytxt.setText(quantity.toString());
        });
    return view;
    }
}

