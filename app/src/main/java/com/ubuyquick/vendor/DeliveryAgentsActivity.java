package com.ubuyquick.vendor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.adapter.AgentAdapter;
import com.ubuyquick.vendor.model.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAgentsActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryAgentsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText input;

    private String shop_id, vendor_number, shop_name, image_url;
    private List<Agent> agents;
    private RecyclerView rv_agents;
    private AgentAdapter agentAdapter;
    private Button btn_add;
    private int LOGIN_MODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_agents);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        shop_id = getIntent().getStringExtra("shop_id");
        shop_name = getIntent().getStringExtra("shop_name");
        vendor_number = getIntent().getStringExtra("vendor_id");
        image_url = getIntent().getStringExtra("image_url");

        btn_add = (Button) findViewById(R.id.btn_add);

        SharedPreferences preferences = getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        rv_agents = (RecyclerView) findViewById(R.id.rv_agents);
        agents = new ArrayList<>();
        agentAdapter = new AgentAdapter(this, shop_id, vendor_number, shop_name, image_url, "DELIVERY_AGENT");
        rv_agents.setAdapter(agentAdapter);

        if (LOGIN_MODE == 1) {
            db.collection("vendors").document(vendor_number)
                    .collection("shops").document(shop_id).collection("delivery_agents").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            Log.d(TAG, "onComplete: size: " + documents.size());
                            for (DocumentSnapshot document : documents) {
                                Log.d(TAG, "onComplete: document: " + document.toString());
                                Map<String, Object> manager = document.getData();
                                agents.add(new Agent(manager.get("name").toString(), manager.get("user_id").toString()));
                            }
                            agentAdapter.setAgents(agents);

                        }
                    });
        } else {
            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                    .collection("shops").document(shop_id).collection("delivery_agents").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            Log.d(TAG, "onComplete: size: " + documents.size());
                            for (DocumentSnapshot document : documents) {
                                Log.d(TAG, "onComplete: document: " + document.toString());
                                Map<String, Object> manager = document.getData();
                                agents.add(new Agent(manager.get("name").toString(), manager.get("user_id").toString()));
                            }
                            agentAdapter.setAgents(agents);

                        }
                    });


        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View viewInflated = LayoutInflater.from(DeliveryAgentsActivity.this).inflate(R.layout.dialog_name_phone, null, false);

                final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delivery Agent details:");
                builder.setView(viewInflated);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Add Delivery Agent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Map<String, Object> agent = new HashMap<>();
                        agent.put("user_id", number.getText().toString());
                        agent.put("name", name.getText().toString());
                        agent.put("user_role", "DELIVERY_AGENT");

                        db.collection("delivery_agents").document(number.getText().toString()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Map<String, Object> shop = new HashMap<>();
                                            shop.put("shop_name", shop_name);
                                            shop.put("image_url", image_url);
                                            shop.put("vendor_id", vendor_number);
                                            shop.put("shop_id", shop_id);

                                            agents.add(new Agent(name.getText().toString(), number.getText().toString()));
                                            agentAdapter.setAgents(agents);

                                            Map<String, Object> managerInfo = new HashMap<>();
                                            managerInfo.put("deliveryagent_count", agents.size());

                                            if (LOGIN_MODE == 1) {
                                                db.collection("vendors").document(vendor_number)
                                                        .collection("shops").document(shop_id).update(managerInfo);

                                                db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                        .set(shop);
                                                db.collection("vendors").document(vendor_number)
                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                        .document(number.getText().toString()).set(agent);
                                            } else {
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).update(managerInfo);

                                                db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                        .set(shop);
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                        .document(number.getText().toString()).set(agent);
                                            }

                                        } else {
                                            agents.add(new Agent(name.getText().toString(), number.getText().toString()));
                                            agentAdapter.setAgents(agents);
                                            db.collection("delivery_agents").document(number.getText().toString()).set(agent)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Map<String, Object> shop = new HashMap<>();
                                                            shop.put("shop_name", shop_name);
                                                            shop.put("image_url", image_url);
                                                            shop.put("vendor_id", vendor_number);
                                                            shop.put("shop_id", shop_id);
                                                            db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                                    .set(shop);
                                                        }
                                                    });
                                            Map<String, Object> managerInfo = new HashMap<>();
                                            managerInfo.put("deliveryagent_count", agents.size());

                                            if (LOGIN_MODE == 1) {
                                                db.collection("vendors").document(vendor_number)
                                                        .collection("shops").document(shop_id).update(managerInfo);

                                                db.collection("vendors").document(vendor_number)
                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                        .document(number.getText().toString()).set(agent);
                                            } else {
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).update(managerInfo);

                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                        .document(number.getText().toString()).set(agent);
                                            }

                                        }
                                    }
                                });


                    }
                });
                builder.show();
            }
        });
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
