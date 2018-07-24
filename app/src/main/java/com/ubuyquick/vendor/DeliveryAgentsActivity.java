package com.ubuyquick.vendor;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAgentsActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryAgentsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText input;
    private ListView list_agents;

    private String shop_id, vendor_number, shop_name, image_url;
    private List<String> agents;
    private RelativeLayout btn_add_delivery;
    private ArrayAdapter<String> arrayAdapter;

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

        btn_add_delivery = (RelativeLayout) findViewById(R.id.btn_add_delivery);

        list_agents = (ListView) findViewById(R.id.lv_agents);
        agents = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, agents);
        list_agents.setAdapter(arrayAdapter);

        registerForContextMenu(list_agents);
        list_agents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

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
                            agents.add(manager.get("user_id").toString());
                        }
                        arrayAdapter.notifyDataSetChanged();

                        btn_add_delivery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                View viewInflated = LayoutInflater.from(DeliveryAgentsActivity.this).inflate(R.layout.dialog_name_phone, null, false);

                                final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                                final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);

                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle("Agent mobile number:");
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

                                                            agents.add(number.getText().toString());
                                                            arrayAdapter.notifyDataSetChanged();

                                                            Map<String, Object> managerInfo = new HashMap<>();
                                                            managerInfo.put("deliveryagent_count", agents.size());
                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                    .collection("shops").document(shop_id).update(managerInfo);

                                                            db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                                    .set(shop);
                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                    .collection("shops").document(shop_id).collection("delivery_agents")
                                                                    .document(number.getText().toString()).set(agent);
                                                        } else {
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
                                                        }
                                                    }
                                                });


                                    }
                                });
                                builder.show();
                            }
                        });
                    }
                });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lv_agents) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.assign_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit:
                View viewInflated = LayoutInflater.from(DeliveryAgentsActivity.this).inflate(R.layout.dialog_name_phone, null, false);

                final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);


                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryAgentsActivity.this);
                builder.setTitle("Agent mobile number:");
                number.setText(agents.get(info.position));
                builder.setView(viewInflated);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Map<String, Object> agent = new HashMap<>();
                        agent.put("user_id", input.getText().toString());
                        agent.put("user_role", "DELIVERY_AGENT");
                        agent.put("name", name.getText().toString());

                        db.collection("delivery_agents").document(agents.get(info.position)).delete();
                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                .collection("shops").document(shop_id).collection("delivery_agents")
                                .document(agents.get(info.position)).delete();

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

                                            db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                    .set(shop);
                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                    .collection("shops").document(shop_id).collection("delivery_agents")
                                                    .document(number.getText().toString()).set(agent);
                                        } else {
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
                                        }
                                        agents.set(info.position, number.getText().toString());
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });

                builder.show();
                return super.onContextItemSelected(item);


            case R.id.action_delete:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(DeliveryAgentsActivity.this);
                builder2.setTitle("Delete agent?");
                builder2.setNegativeButton("Cancel", null);

                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("delivery_agents").document(agents.get(info.position)).delete();
                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                .collection("shops").document(shop_id).collection("delivery_agents")
                                .document(agents.get(info.position)).delete();
                        agents.remove(info.position);
                        arrayAdapter.notifyDataSetChanged();
                    }
                });

                builder2.show();
                return super.onContextItemSelected(item);


            default:
                return super.onContextItemSelected(item);

        }
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
