package com.ubuyquick.vendor.orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.NewOrderAdapter;
import com.ubuyquick.vendor.model.NewOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewOrdersFragment extends Fragment {

    private static final String TAG = "NewOrdersFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams layoutParams;
    private RecyclerView orderList;
    private NewOrderAdapter newOrderAdapter;
    private List<NewOrder> newOrders;
    private ProgressBar progressBar;

    private int LOGIN_MODE = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_orders_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relLayout1);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        orderList = (RecyclerView) view.findViewById(R.id.rv_orders);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        newOrderAdapter = new NewOrderAdapter(view.getContext(), getArguments().getString("shop_id"));
        orderList.setAdapter(newOrderAdapter);
        newOrders = new ArrayList<>();

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        if (LOGIN_MODE == 1) {

            db.collection("vendors").document(getArguments().getString("vendor_id")).collection("shops").document(getArguments().getString("shop_id")).collection("new_orders")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() > 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Map<String, Object> order = document.getData();
                                        newOrders.add(new NewOrder(order.get("order_id").toString(), order.get("customer_name").toString()
                                                , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString()));
                                    }
                                    newOrderAdapter.setNewOrders(newOrders);
                                } else {
                                    TextView no_orders = new TextView(getContext());
                                    no_orders.setLayoutParams(layoutParams);
                                    no_orders.setText("You have no new orders yet.");
                                    relativeLayout.addView(no_orders);
                                }
                            } else {
                                Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                            }
                        }
                    });
        } else {


            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document(getArguments().getString("shop_id")).collection("new_orders")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() > 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Map<String, Object> order = document.getData();
                                        newOrders.add(new NewOrder(order.get("order_id").toString(), order.get("customer_name").toString()
                                                , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString()));
                                    }
                                    newOrderAdapter.setNewOrders(newOrders);
                                } else {
                                    TextView no_orders = new TextView(getContext());
                                    no_orders.setLayoutParams(layoutParams);
                                    no_orders.setText("You have no new orders yet.");
                                    relativeLayout.addView(no_orders);
                                }
                            } else {
                                Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                            }
                        }
                    });
        }

        progressBar.setVisibility(View.GONE);
        orderList.setVisibility(View.VISIBLE);
        return view;
    }

}
