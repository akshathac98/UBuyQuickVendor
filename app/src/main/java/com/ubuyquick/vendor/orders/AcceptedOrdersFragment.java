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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.AcceptedOrderAdapter;
import com.ubuyquick.vendor.adapter.NewOrderAdapter;
import com.ubuyquick.vendor.model.AcceptedOrder;
import com.ubuyquick.vendor.model.NewOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcceptedOrdersFragment extends Fragment {

    private static final String TAG = "AcceptedOrdersFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams layoutParams;
    private RecyclerView orderList;
    private AcceptedOrderAdapter acceptedOrderAdapter;
    private List<AcceptedOrder> acceptedOrders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accepted_orders_fragment, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relLayout1);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        orderList = (RecyclerView) view.findViewById(R.id.rv_orders);
        acceptedOrderAdapter = new AcceptedOrderAdapter(view.getContext(), getArguments().getString("shop_id"));
        orderList.setAdapter(acceptedOrderAdapter);
        acceptedOrders = new ArrayList<>();

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                .document(getArguments().getString("shop_id")).collection("accepted_orders").orderBy("order_id", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Map<String, Object> order = document.getData();
                                    acceptedOrders.add(new AcceptedOrder(order.get("order_id").toString(), order.get("customer_name").toString()
                                            , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString()));
                                }
                                acceptedOrderAdapter.setAcceptedOrders(acceptedOrders);
                            } else {
                                TextView no_orders = new TextView(getContext());
                                no_orders.setLayoutParams(layoutParams);
                                no_orders.setText("You haven't accepted any orders.");
                                relativeLayout.addView(no_orders);
                            }
                        } else {
                            Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                        }
                    }
                });

        return view;
    }
}
