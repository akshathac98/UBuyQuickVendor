package com.ubuyquick.vendor.orders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.OrderProductAdapter;
import com.ubuyquick.vendor.model.NewOrder;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.Utils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOrderActivity extends AppCompatActivity {

    private static final String TAG = "NewOrderActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String order_id, shop_id;
    private int products_available = 0, total_products = 0;
    private Double order_total = 0.0;

    private TextView tv_customer, tv_address, tv_order_id, tv_ordered_at, tv_order_total;
    private TextView tv_total, tv_products, tv_available, tv_slot;
    private RecyclerView rv_order_products;
    private OrderProductAdapter orderProductAdapter;
    private List<OrderProduct> orderProducts;
    private TextView btn_accept, btn_cancel;
    private EditText et_discount, et_shipping, et_package;
    private CheckBox cb_all;
    private Map<String, String> agents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

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
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_products = (TextView) findViewById(R.id.tv_products);
        tv_slot = (TextView) findViewById(R.id.tv_slot);
        tv_available = (TextView) findViewById(R.id.tv_available);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_accept = (TextView) findViewById(R.id.btn_accept);
        cb_all = (CheckBox) findViewById(R.id.cb_product);

        et_discount = (EditText) findViewById(R.id.et_discount);
        et_package = (EditText) findViewById(R.id.et_package);
        et_shipping = (EditText) findViewById(R.id.et_delivery);

        EditText.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    et_shipping.requestFocus();
                }
                return true;
            }
        };

        EditText.OnEditorActionListener listener2 = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    et_package.requestFocus();
                }
                return true;
            }
        };

        EditText.OnEditorActionListener listener3 = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    et_package.clearFocus();
                }
                return true;
            }
        };

        et_discount.setOnEditorActionListener(listener);
        et_shipping.setOnEditorActionListener(listener2);
        et_package.setOnEditorActionListener(listener3);

        rv_order_products = (RecyclerView) findViewById(R.id.rv_order_products);

    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        order_id = getIntent().getStringExtra("ORDER_ID");
        shop_id = getIntent().getStringExtra("shop_id");
        agents = new HashMap<>();

        Log.d(TAG, "initialize: order id: " + order_id);

        orderProductAdapter = new OrderProductAdapter(this, order_id, shop_id, "NEW", new Utils.OnItemClick() {
            @Override
            public void onClick(int count) {
                tv_available.setText("" + count);
                if (count == orderProducts.size()) ;
            }
        }, new Utils.OnChange() {
            @Override
            public void onChange(double mrp) {
                tv_total.setText("" + (mrp + Double.parseDouble(tv_total.getText().toString())));
                order_total = (mrp + Double.parseDouble(tv_total.getText().toString()));
            }
        });
        orderProducts = new ArrayList<>();
        rv_order_products.setAdapter(orderProductAdapter);
        cb_all.setChecked(true);
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    orderProductAdapter.selectAll(true);
                else
                    orderProductAdapter.deselectAll();
            }
        });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document(shop_id)
                .collection("new_orders").document(order_id).collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> product = document.getData();
                                order_total += Double.parseDouble(product.get("mrp").toString()) *
                                        Double.parseDouble(product.get("quantity").toString());
                                orderProducts.add(new OrderProduct(document.getId(), product.get("name").toString(),
                                        Integer.parseInt(product.get("quantity").toString()), Double.parseDouble(product.get("mrp").toString())
                                        , "", true));
                            }
                            tv_total.setText("\u20B9" + order_total);
                            tv_products.setText("" + orderProducts.size());
                            orderProductAdapter.setOrderProducts(orderProducts);
                        }
                    }
                });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id)
                .collection("new_orders").document(order_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final Map<String, Object> order = task.getResult().getData();
                            tv_customer.setText(order.get("customer_name").toString());
                            tv_address.setText(order.get("delivery_address").toString());
                            tv_order_id.setText(order_id);

                            final String slot = order.get("slot").toString();
                            if (slot.equals("Quick Delivery"))
                                tv_slot.setText(slot);
                            else if (!slot.equals("Pick Up"))
                                tv_slot.setText("Delivery " + slot);
                            else if (slot.equals("Pick Up"))
                                tv_slot.setText("Pick Up");

                            tv_ordered_at.setText(new Date(new java.sql.Timestamp(Long.parseLong(order.get("order_id").toString())).getTime()).toString());

                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).collection("delivery_agents").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<QuerySnapshot> task) {

                                            if (slot.equals("Pick Up")) {
                                                btn_accept.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NewOrderActivity.this);
                                                        builder.setTitle("Accept Order?");
                                                        builder.setNegativeButton("Cancel", null);
                                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final Map<String, Object> acceptedOrder = new HashMap<>();
                                                                acceptedOrder.put("delivery_agent", "None");
                                                                acceptedOrder.put("delivery_agent_name", "None");
                                                                acceptedOrder.put("latitude", order.get("latitude").toString());
                                                                acceptedOrder.put("longitude", order.get("longitude").toString());
                                                                acceptedOrder.put("customer_name", order.get("customer_name").toString());
                                                                acceptedOrder.put("order_id", order.get("order_id").toString());
                                                                acceptedOrder.put("ordered_at", order.get("ordered_at").toString());
                                                                acceptedOrder.put("customer_id", order.get("customer_id").toString());
                                                                acceptedOrder.put("count", order.get("count").toString());
                                                                acceptedOrder.put("slot", order.get("slot").toString());
                                                                acceptedOrder.put("delivery_address", order.get("delivery_address").toString());

                                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document(shop_id)
                                                                        .collection("new_orders").document(order.get("order_id").toString()).collection("products")
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    int available_products = 0;
                                                                                    CollectionReference collectionReference =
                                                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                                                    .collection("shops").document(shop_id)
                                                                                                    .collection("accepted_orders").document(order.get("order_id").toString()).collection("products");
                                                                                    for (OrderProduct orderProduct : orderProducts) {
                                                                                        if (orderProduct.isAvailable()) {
                                                                                            available_products++;
                                                                                            Map<String, Object> product = new HashMap<>();
                                                                                            product.put("name", orderProduct.getProductName());
                                                                                            product.put("mrp", orderProduct.getProductMrp());
                                                                                            product.put("quantity", orderProduct.getProductQuantity());
                                                                                            product.put("image_url", orderProduct.getProductImageUrl());
                                                                                            collectionReference.add(product);
                                                                                        }
                                                                                    }
                                                                                    acceptedOrder.put("product_count", available_products);
                                                                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                                            .collection("shops").document(shop_id)
                                                                                            .collection("accepted_orders")
                                                                                            .document(order.get("order_id").toString())
                                                                                            .update(acceptedOrder);
                                                                                }
                                                                            }
                                                                        });

                                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                        .collection("shops").document(shop_id)
                                                                        .collection("accepted_orders")
                                                                        .document(order.get("order_id").toString())
                                                                        .set(acceptedOrder)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "onComplete: remove id: " + order.get("order_id").toString());
                                                                                db.collection("vendors").document(mAuth.getCurrentUser()
                                                                                        .getPhoneNumber().substring(3)).collection("shops")
                                                                                        .document(shop_id).collection("new_orders")
                                                                                        .document(order.get("order_id").toString()).delete()
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Toast.makeText(NewOrderActivity.this, "Accepted order.", Toast.LENGTH_SHORT).show();
                                                                                                finish();
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Toast.makeText(NewOrderActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(NewOrderActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            } else

                                                btn_accept.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(NewOrderActivity.this);
                                                        builder.setTitle("Select Delivery Agent:");
                                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(NewOrderActivity.this,
                                                                android.R.layout.select_dialog_singlechoice);
                                                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                                            Map<String, Object> agentInfo = document.getData();
                                                            agents.put(agentInfo.get("name").toString(), agentInfo.get("user_id").toString());
                                                            arrayAdapter.add(agentInfo.get("name").toString());
                                                        }

                                                        builder.setNegativeButton("Cancel", null);
                                                        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final String agent = arrayAdapter.getItem(which);
                                                                AlertDialog.Builder builderInner = new AlertDialog.Builder(NewOrderActivity.this);
                                                                builderInner.setMessage(agent);
                                                                builderInner.setTitle("Selected delivery agent:");
                                                                builderInner.setPositiveButton("Accept Order", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        final Map<String, Object> acceptedOrder = new HashMap<>();
                                                                        acceptedOrder.put("delivery_agent", agents.get(agent).toString());
                                                                        acceptedOrder.put("delivery_agent_name", agent);
                                                                        acceptedOrder.put("latitude", order.get("latitude").toString());
                                                                        acceptedOrder.put("longitude", order.get("longitude").toString());
                                                                        acceptedOrder.put("customer_name", order.get("customer_name").toString());
                                                                        acceptedOrder.put("order_id", order.get("order_id").toString());
                                                                        acceptedOrder.put("ordered_at", order.get("ordered_at").toString());
                                                                        acceptedOrder.put("customer_id", order.get("customer_id").toString());
                                                                        acceptedOrder.put("count", order.get("count").toString());
                                                                        acceptedOrder.put("slot", order.get("slot").toString());
                                                                        acceptedOrder.put("delivery_address", order.get("delivery_address").toString());

                                                                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document(shop_id)
                                                                                .collection("new_orders").document(order.get("order_id").toString()).collection("products")
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            int available_products = 0;
                                                                                            CollectionReference collectionReference =
                                                                                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                                                            .collection("shops").document(shop_id)
                                                                                                            .collection("accepted_orders").document(order.get("order_id").toString()).collection("products");
                                                                                            for (OrderProduct orderProduct : orderProducts) {
                                                                                                if (orderProduct.isAvailable()) {
                                                                                                    available_products++;
                                                                                                    Map<String, Object> product = new HashMap<>();
                                                                                                    product.put("name", orderProduct.getProductName());
                                                                                                    product.put("mrp", orderProduct.getProductMrp());
                                                                                                    product.put("quantity", orderProduct.getProductQuantity());
                                                                                                    product.put("image_url", orderProduct.getProductImageUrl());
                                                                                                    collectionReference.add(product);
                                                                                                }
                                                                                            }
                                                                                            acceptedOrder.put("product_count", available_products);
                                                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                                                    .collection("shops").document(shop_id)
                                                                                                    .collection("accepted_orders")
                                                                                                    .document(order.get("order_id").toString())
                                                                                                    .update(acceptedOrder);
                                                                                        }
                                                                                    }
                                                                                });

                                                                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                                .collection("shops").document(shop_id)
                                                                                .collection("accepted_orders")
                                                                                .document(order.get("order_id").toString())
                                                                                .set(acceptedOrder)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Log.d(TAG, "onComplete: remove id: " + order.get("order_id").toString());
                                                                                        db.collection("vendors").document(mAuth.getCurrentUser()
                                                                                                .getPhoneNumber().substring(3)).collection("shops")
                                                                                                .document(shop_id).collection("new_orders")
                                                                                                .document(order.get("order_id").toString()).delete()
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        Toast.makeText(NewOrderActivity.this, "Accepted order.", Toast.LENGTH_SHORT).show();
                                                                                                        finish();
                                                                                                    }
                                                                                                })
                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                        Toast.makeText(NewOrderActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(NewOrderActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                                builderInner.show();
                                                            }
                                                        });
                                                        builder.show();

                                                    }
                                                });


                                        }
                                    });

                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setMessage("Cancel order?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Map<String, Object> cancelledOrder = new HashMap<>();
                                                    cancelledOrder.put("customer_name", order.get("customer_name").toString());
                                                    cancelledOrder.put("order_id", order.get("order_id").toString());
                                                    cancelledOrder.put("ordered_at", order.get("ordered_at").toString());
                                                    cancelledOrder.put("customer_id", order.get("customer_id").toString());
                                                    cancelledOrder.put("slot", order.get("slot").toString());
                                                    cancelledOrder.put("count", order.get("count").toString());
                                                    cancelledOrder.put("delivery_address", order.get("delivery_address").toString());

                                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                            .collection("shops").document(shop_id)
                                                            .collection("new_orders").document(order.get("order_id").toString()).collection("products")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                                        for (DocumentSnapshot document : documents) {
                                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                                    .collection("shops").document(shop_id)
                                                                                    .collection("cancelled_orders").document(order.get("order_id").toString())
                                                                                    .collection("products").document(document.getId()).set(document.getData());
                                                                        }
                                                                    } else {
                                                                        Toast.makeText(NewOrderActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                            .collection("shops").document(shop_id)
                                                            .collection("cancelled_orders")
                                                            .document(order.get("order_id").toString())
                                                            .set(cancelledOrder)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "onComplete: " + order.get("order_id").toString());
                                                                        db.collection("vendors").document(mAuth.getCurrentUser()
                                                                                .getPhoneNumber().substring(3))
                                                                                .collection("shops").document(shop_id).collection("new_orders")
                                                                                .document(order.get("order_id").toString()).delete()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(NewOrderActivity.this, "Cancelled order", Toast.LENGTH_SHORT).show();
                                                                                            finish();
                                                                                        } else {
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        Toast.makeText(NewOrderActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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
