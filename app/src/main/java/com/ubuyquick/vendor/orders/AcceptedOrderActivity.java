package com.ubuyquick.vendor.orders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.OrderProductAdapter;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptedOrderActivity extends AppCompatActivity {

    private static final String TAG = "AcceptedOrderActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String order_id, shop_id, number, vendor_id;

    private TextView tv_customer, tv_agent, tv_address, tv_order_id, tv_ordered_at, tv_order_total, tv_product_count;
    private RecyclerView rv_order_products;
    private OrderProductAdapter orderProductAdapter;
    private List<OrderProduct> orderProducts;
    private Button btn_delivered, btn_note;
    private int LOGIN_MODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_order);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        initializeViews();
        initialize();
    }

    private void initializeViews() {
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_agent = (TextView) findViewById(R.id.tv_agent);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_order_id = (TextView) findViewById(R.id.tv_order_id);
        tv_ordered_at = (TextView) findViewById(R.id.tv_ordered_at);
        tv_order_total = (TextView) findViewById(R.id.tv_total);
        tv_product_count = (TextView) findViewById(R.id.tv_product_count2);

        btn_delivered = (Button) findViewById(R.id.btn_delivered);
        btn_note = (Button) findViewById(R.id.btn_credit);
        rv_order_products = (RecyclerView) findViewById(R.id.rv_order_products);
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        order_id = getIntent().getStringExtra("ORDER_ID");
        shop_id = getIntent().getStringExtra("shop_id");
        vendor_id = getIntent().getStringExtra("vendor_id");

        if (LOGIN_MODE == 1) {
            number = vendor_id;
        } else {
            number = mAuth.getCurrentUser().getPhoneNumber().substring(3);
        }

        orderProductAdapter = new OrderProductAdapter(this, shop_id, order_id, "ACCEPTED", new Utils.OnItemClick() {
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

        db.collection("vendors").document(number).collection("shops")
                .document(shop_id)
                .collection("accepted_orders").document(order_id).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            tv_product_count.setText(task.getResult().getDocuments().size() + "");
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

        db.collection("vendors").document(number).collection("shops")
                .document(shop_id)
                .collection("accepted_orders").document(order_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final Map<String, Object> order = task.getResult().getData();


                            btn_note.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AcceptedOrderActivity.this);

                                    View viewInflated = LayoutInflater.from(AcceptedOrderActivity.this).inflate(R.layout.dialog_note, null, false);
                                    final TextInputEditText balance = (TextInputEditText) viewInflated.findViewById(R.id.et_balance);

                                    builder.setTitle("Credit Balance");
                                    builder.setView(viewInflated);
                                    builder.setNegativeButton("Cancel", null);
                                    builder.setPositiveButton("Add Note", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (TextUtils.isEmpty(balance.getText().toString()))
                                                Toast.makeText(AcceptedOrderActivity.this, "Empty balance", Toast.LENGTH_SHORT).show();
                                            else {
                                                Map<String, Object> credit = new HashMap<>();
                                                credit.put("balance", Double.parseDouble(balance.getText().toString()));
                                                credit.put("cleared", false);
                                                credit.put("name", order.get("customer_name").toString());
                                                credit.put("number", order.get("customer_id").toString());
                                                credit.put("order_id", order.get("order_id").toString());
                                                db.collection("vendors").document(number).collection("shops")
                                                        .document(shop_id).collection("credit_notes").add(credit);
                                                Toast.makeText(AcceptedOrderActivity.this, "Saved credit note", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });

                            btn_delivered.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setMessage("Mark as delivered?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Map<String, Object> deliveredOrder = new HashMap<>();
                                                    deliveredOrder.put("customer_name", order.get("customer_name").toString());
                                                    deliveredOrder.put("order_id", order.get("order_id").toString());
                                                    deliveredOrder.put("ordered_at", order.get("ordered_at").toString());
                                                    deliveredOrder.put("customer_id", order.get("customer_id").toString());
                                                    deliveredOrder.put("delivery_address", order.get("delivery_address").toString());

                                                    db.collection("vendors").document(number)
                                                            .collection("shops").document(shop_id)
                                                            .collection("accepted_orders").document(order.get("order_id").toString()).collection("products")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                                        for (DocumentSnapshot document : documents) {
                                                                            db.collection("vendors").document(number)
                                                                                    .collection("shops").document(shop_id)
                                                                                    .collection("delivered_orders").document(order.get("order_id").toString())
                                                                                    .collection("products").document(document.getId()).set(document.getData());
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(AcceptedOrderActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                    db.collection("vendors").document(number)
                                                            .collection("shops").document(shop_id)
                                                            .collection("delivered_orders")
                                                            .document(order.get("order_id").toString())
                                                            .set(deliveredOrder)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "onComplete: " + order.get("order_id").toString());
                                                                        db.collection("vendors").document(number)
                                                                                .collection("shops").document(shop_id).collection("accepted_orders")
                                                                                .document(order.get("order_id").toString()).delete()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(AcceptedOrderActivity.this, "Delivery successful", Toast.LENGTH_SHORT).show();
                                                                                            finish();
                                                                                        } else {
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        Toast.makeText(AcceptedOrderActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });

                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();
                                }
                            });

                            tv_customer.setText(order.get("customer_name").toString());
                            tv_agent.setText(order.get("delivery_agent_name").toString());
                            tv_address.setText(order.get("delivery_address").toString());
                            tv_order_id.setText(order_id);
                            tv_ordered_at.setText(order.get("ordered_at").toString());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delivery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            super.onBackPressed();
        } else if (item.getItemId() == R.id.action_deliver) {
            Uri mapsUri = Uri.parse("google.navigation:q=Peenya+2nd+Stage,+Bangalore");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        return false;
    }
}
