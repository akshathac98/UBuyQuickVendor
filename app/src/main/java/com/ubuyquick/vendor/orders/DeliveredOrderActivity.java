package com.ubuyquick.vendor.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.OrderProductAdapter;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveredOrderActivity extends AppCompatActivity {

    private static final String TAG = "DeliveredOrderActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String order_id, shop_id;

    private TextView tv_customer, tv_address, tv_order_id, tv_ordered_at, tv_order_total;
    private RecyclerView rv_order_products;
    private OrderProductAdapter orderProductAdapter;
    private List<OrderProduct> orderProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_order);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
        initialize();
    }

    private void initializeViews() {
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_order_id = (TextView) findViewById(R.id.tv_order_id);
        tv_ordered_at = (TextView) findViewById(R.id.tv_ordered_at);
        tv_order_total = (TextView) findViewById(R.id.tv_order_total);

        rv_order_products = (RecyclerView) findViewById(R.id.rv_order_products);
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        order_id = getIntent().getStringExtra("ORDER_ID");
        shop_id = getIntent().getStringExtra("shop_id");

        orderProductAdapter = new OrderProductAdapter(this, shop_id, order_id, "DELIVERED", new Utils.OnItemClick() {
            @Override
            public void onClick(int count) {

            }
        }, new Utils.OnChange() {
            @Override
            public void onChange(double mrp) {

            }
        });
        orderProducts = new ArrayList<>();
        rv_order_products.setAdapter(orderProductAdapter);

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id)
                .collection("delivered_orders").document(order_id).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> product = document.getData();
                                orderProducts.add(new OrderProduct(document.getId(), product.get("name").toString(),
                                        Integer.parseInt(product.get("quantity").toString()), Double.parseDouble(product.get("mrp").toString())
                                        , product.get("image_url").toString(), true));
                            }
                            orderProductAdapter.setOrderProducts(orderProducts);
                        }
                    }
                });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id)
                .collection("delivered_orders").document(order_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> product = task.getResult().getData();
                            tv_customer.setText(product.get("customer_name").toString());
                            tv_address.setText(product.get("delivery_address").toString());
                            tv_order_id.setText(order_id);
                            tv_ordered_at.setText(product.get("ordered_at").toString());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            super.onBackPressed();
        }
        return false;
    }
}
