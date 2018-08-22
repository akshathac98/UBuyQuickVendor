package com.ubuyquick.vendor.credits;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.CreditAdapter;
import com.ubuyquick.vendor.adapter.CreditNoteAdapter;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.model.CreditNote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreditNotesFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_credits;
    private CreditNoteAdapter creditNoteAdapter;
    private List<CreditNote> creditNotes;
    private EditText et_search;

    private String shop_id;
    private int LOGIN_MODE = 0;
    private String number1;

    public CreditNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_credit_notes, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);
        if (LOGIN_MODE == 1) {
            number1 = getArguments().getString("vendor_id");
        } else {
            number1 = mAuth.getCurrentUser().getPhoneNumber().substring(3);
        }

        shop_id = getArguments().getString("shop_id");

        et_search = (EditText) view.findViewById(R.id.et_search);
        rv_credits = (RecyclerView) view.findViewById(R.id.rv_credits);
        creditNotes = new ArrayList<>();
        creditNoteAdapter = new CreditNoteAdapter(view.getContext(), shop_id, getArguments().getString("vendor_id"));
        rv_credits.setAdapter(creditNoteAdapter);

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

        db.collection("vendors").document(number1).collection("shops")
                .document(shop_id).collection("credit_notes").whereEqualTo("cleared", false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> credit = document.getData();
                    creditNotes.add(new CreditNote(document.getId(), credit.get("number").toString(), credit.get("name").toString(), credit.get("number").toString(),
                            Boolean.parseBoolean(credit.get("cleared").toString()),
                            credit.get("order_id").toString(),
                            Double.parseDouble(credit.get("balance").toString())));
                }
                creditNoteAdapter.setCreditNotes(creditNotes);
            }
        });

        return view;
    }

    private void filter(String text) {
        List<CreditNote> temp = new ArrayList<>();
        for (CreditNote creditNote : creditNotes) {
            if (creditNote.getCustomerName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(creditNote);
            }
        }
        creditNoteAdapter.setCreditNotes(temp);
    }
}



