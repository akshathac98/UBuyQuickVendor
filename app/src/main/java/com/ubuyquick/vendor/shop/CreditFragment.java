package com.ubuyquick.vendor.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.CreditAdapter;
import com.ubuyquick.vendor.model.Credit;

import java.util.ArrayList;
import java.util.List;

public class CreditFragment extends Fragment {

    private static final String TAG = "CreditFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_credits;
    private CreditAdapter creditAdapter;
    private List<Credit> credits;
    private EditText et_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rv_credits = (RecyclerView) view.findViewById(R.id.rv_credits);
        credits = new ArrayList<>();
        et_search = (EditText) view.findViewById(R.id.et_search);
        creditAdapter = new CreditAdapter(view.getContext());
        rv_credits.setAdapter(creditAdapter);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                creditAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        credits.add(new Credit("123123123", "Ajay Srinivas", "9008003968", 329.0));
        credits.add(new Credit("124124124", "Vijay Srinivas", "7204131524", 329.0));
        creditAdapter.setCredits(credits);

        return view;
    }

    private void filter(String text) {
        List<Credit> temp = new ArrayList<>();
        for (Credit credit : credits) {
            if (credit.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(credit);
            }
        }
        creditAdapter.setCredits(temp);
    }

}
