package com.ubuyquick.vendor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.adapter.AcceptedOrderAdapter;
import com.ubuyquick.vendor.adapter.OrderProductAdapter;
import com.ubuyquick.vendor.model.AcceptedOrder;
import com.ubuyquick.vendor.model.OrderProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveryOrdersActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryOrdersActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private String order_id;
    private String shop_name;
    private String vendor_id;
    private String image_url;

    private RecyclerView rv_order_products;
    private OrderProductAdapter orderProductAdapter;
    private List<OrderProduct> orderProducts;
    private Button btn_delivered, btn_note;

    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams layoutParams;
    private RecyclerView orderList;
    private AcceptedOrderAdapter acceptedOrderAdapter;
    private List<AcceptedOrder> acceptedOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_orders);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("shop_name"));

        shop_id = getIntent().getStringExtra("shop_id");
        shop_name = getIntent().getStringExtra("shop_name");
        vendor_id = getIntent().getStringExtra("vendor_id");
        image_url = getIntent().getStringExtra("image_url");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        order_id = getIntent().getStringExtra("ORDER_ID");
        shop_id = getIntent().getStringExtra("shop_id");
        vendor_id = getIntent().getStringExtra("vendor_id");

        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        acceptedOrderAdapter = new AcceptedOrderAdapter(DeliveryOrdersActivity.this, shop_id, vendor_id);
        orderList = (RecyclerView) findViewById(R.id.rv_orders);
        orderList.setAdapter(acceptedOrderAdapter);
        acceptedOrders = new ArrayList<>();

        db.collection("vendors").document(vendor_id).collection("shops")
                .document(shop_id).collection("accepted_orders").orderBy("order_id", Query.Direction.DESCENDING)
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
                                            , order.get("customer_id").toString(), order.get("delivery_address").toString(), order.get("ordered_at").toString(),
                                            order.get("latitude").toString(), order.get("longitude").toString(),
                                            Integer.parseInt(order.get("count").toString())));
                                }
                                acceptedOrderAdapter.setAcceptedOrders(acceptedOrders);
                            } else {
                                TextView no_orders = new TextView(DeliveryOrdersActivity.this);
                                no_orders.setLayoutParams(layoutParams);
                                no_orders.setText("You have no assigned orders :(");
                                relativeLayout.addView(no_orders);
                            }
                        } else {
                            Log.d(TAG, "onComplete: error getting documents: " + task.getException());
                        }
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

}
