package com.ubuyquick.vendor.orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.DeliveredOrderAdapter;
import com.ubuyquick.vendor.model.DeliveredOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveredOrdersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DeliveredOrdersFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RelativeLayout relativeLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout.LayoutParams layoutParams;
    private RecyclerView orderList;
    private DeliveredOrderAdapter deliveredOrderAdapter;
    private List<DeliveredOrder> deliveredOrders;

    private int LOGIN_MODE = 0;
    private String number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delivered_orders_fragment, container, false);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relLayout1);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        orderList = (RecyclerView) view.findViewById(R.id.rv_orders);
        deliveredOrderAdapter = new DeliveredOrderAdapter(view.getContext(), getArguments().getString("shop_id"),
                getArguments().getString("vendor_id"));
        orderList.setAdapter(deliveredOrderAdapter);
        deliveredOrders = new ArrayList<>();

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        if (LOGIN_MODE == 1) {
            number = getArguments().getString("vendor_id");
        } else {
            number = mAuth.getCurrentUser().getPhoneNumber().substring(3);
        }

        db.collection("vendors").document(number)
                .collection("shops").document(getArguments().getString("shop_id")).collection("delivered_orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Map<String, Object> order = document.getData();
                                    deliveredOrders.add(new DeliveredOrder(order.get("order_id").toString(), order.get("customer_name").toString()
                                            , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString(),
                                            Integer.parseInt(order.get("count").toString())));
                                }
                                deliveredOrderAdapter.setDeliveredOrders(deliveredOrders);
                            } else {
                                TextView no_orders = new TextView(getContext());
                                no_orders.setLayoutParams(layoutParams);
                                no_orders.setText("You haven't delivered any orders yet.");
                                relativeLayout.addView(no_orders);
                            }
                        } else {
                            Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                        }
                    }
                });

        return view;
    }

    @Override
    public void onRefresh() {

        deliveredOrders.clear();
        deliveredOrderAdapter.notifyDataSetChanged();

        db.collection("vendors").document(number)
                .collection("shops").document(getArguments().getString("shop_id")).collection("delivered_orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Map<String, Object> order = document.getData();
                                    deliveredOrders.add(new DeliveredOrder(order.get("order_id").toString(), order.get("customer_name").toString()
                                            , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString(),
                                            Integer.parseInt(order.get("count").toString())));
                                }
                                deliveredOrderAdapter.setDeliveredOrders(deliveredOrders);
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                TextView no_orders = new TextView(getContext());
                                no_orders.setLayoutParams(layoutParams);
                                no_orders.setText("You haven't delivered any orders yet.");
                                relativeLayout.addView(no_orders);
                            }
                        } else {
                            Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                        }
                    }
                });
    }
}
