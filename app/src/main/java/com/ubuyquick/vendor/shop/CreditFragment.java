package com.ubuyquick.vendor.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.CreditAdapter;
import com.ubuyquick.vendor.model.Credit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditFragment extends Fragment {

    private static final String TAG = "CreditFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_credits;
    private CreditAdapter creditAdapter;
    private List<Credit> credits;
    private EditText et_search;
    private Button btn_add, btn_message;

    private String shop_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        shop_id = getArguments().getString("shop_id");

        rv_credits = (RecyclerView) view.findViewById(R.id.rv_credits);
        credits = new ArrayList<>();
        et_search = (EditText) view.findViewById(R.id.et_search);
        creditAdapter = new CreditAdapter(view.getContext(), shop_id);
        rv_credits.setAdapter(creditAdapter);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_message = (Button) view.findViewById(R.id.btn_message);

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

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                btn_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_credit_message, null, false);
                        final TextInputEditText message = (TextInputEditText) viewInflated.findViewById(R.id.et_message);
                        message.setText(task.getResult().getData().get("credit_message").toString());

                        builder.setTitle("Enter credit remainder message:");
                        builder.setView(viewInflated)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (TextUtils.isEmpty(message.getText().toString()))
                                            Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                                        else {
                                            Map<String, Object> msg = new HashMap<>();
                                            msg.put("credit_message", message.getText().toString());
                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                    .collection("shops").document(shop_id).update(msg);
                                            Toast.makeText(getContext(), "Credit remainder message saved.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null);
                        builder.show();
                    }
                });
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_credit, null, false);
                final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);
                final TextInputEditText balance = (TextInputEditText) viewInflated.findViewById(R.id.et_balance);

                builder.setTitle("Enter credit holder info:");
                builder.setView(viewInflated)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(number.getText().toString()) || TextUtils.isEmpty(balance.getText().toString()))
                                    Toast.makeText(getContext(), "Incomplete info", Toast.LENGTH_SHORT).show();
                                else {
                                    Map<String, Object> credit = new HashMap<>();
                                    credit.put("name", name.getText().toString());
                                    credit.put("number", number.getText().toString());
                                    credit.put("balance", Double.parseDouble(balance.getText().toString()));

                                    credits.add(new Credit(number.getText().toString(), name.getText().toString(),
                                            number.getText().toString(), Double.parseDouble(balance.getText().toString())));
                                    creditAdapter.setCredits(credits);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                                            .document(shop_id).collection("credits").document(number.getText().toString())
                                    .set(credit);
                                    Toast.makeText(getContext(), "Saved credit holder info.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                .document(shop_id).collection("credits").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> credit = document.getData();
                    credits.add(new Credit(document.getId(), credit.get("name").toString(), credit.get("number").toString(),
                            Double.parseDouble(credit.get("balance").toString())));
                }
                creditAdapter.setCredits(credits);
            }
        });

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
