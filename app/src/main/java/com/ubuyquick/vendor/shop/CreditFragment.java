package com.ubuyquick.vendor.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rv_credits = (RecyclerView) view.findViewById(R.id.rv_credits);
        credits = new ArrayList<>();
        creditAdapter = new CreditAdapter(view.getContext());
        rv_credits.setAdapter(creditAdapter);

        credits.add(new Credit("123123123", "Ajay Srinivas", "9008003968", 329.0));
        creditAdapter.setCredits(credits);

        return view;
    }
}
