package com.ubuyquick.vendor.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.OrderAdapter;
import com.ubuyquick.vendor.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewOrdersFragment extends Fragment {

    private static final String TAG = "NewOrdersFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView orderList;
    private OrderAdapter orderAdapter;
    private List<Order> orders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_orders_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        orderList = (RecyclerView) view.findViewById(R.id.rv_orders);
        orderAdapter = new OrderAdapter(view.getContext());
        orderList.setAdapter(orderAdapter);
        orders = new ArrayList<>();

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("new_orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Map<String, Object> order = document.getData();
                                orders.add(new Order(order.get("order_id").toString(), order.get("customer_name").toString()
                                , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString()));
                            }
                            orderAdapter.setOrders(orders);
                        } else {
                            Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                        }
                    }
                });

        return view;
    }
}
