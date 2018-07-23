package com.ubuyquick.vendor;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
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

public class ManagersActivity extends AppCompatActivity {

    private static final String TAG = "ManagersActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText input;
    private ListView list_managers;

    private String shop_id, vendor_number, shop_name;
    private List<String> managers;
    private RelativeLayout btn_add_manager;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managers);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        shop_id = getIntent().getStringExtra("shop_id");
        shop_name = getIntent().getStringExtra("shop_name");
        vendor_number = getIntent().getStringExtra("vendor_id");

        btn_add_manager = (RelativeLayout) findViewById(R.id.btn_add_manager);

        list_managers = (ListView) findViewById(R.id.lv_managers);
        managers = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, managers);
        list_managers.setAdapter(arrayAdapter);
        registerForContextMenu(list_managers);
        list_managers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).collection("managers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        Log.d(TAG, "onComplete: size: " + documents.size());
                        for (DocumentSnapshot document : documents) {
                            Log.d(TAG, "onComplete: document: " + document.toString());
                            Map<String, Object> manager = document.getData();
                            managers.add(manager.get("user_id").toString());
                        }
                        arrayAdapter.notifyDataSetChanged();

                        btn_add_manager.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle("Manager mobile number:");
                                input = new EditText(v.getContext());
                                input.setInputType(InputType.TYPE_CLASS_PHONE);
                                builder.setView(input);
                                builder.setNegativeButton("Cancel", null);

                                builder.setPositiveButton("Add Manager", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Map<String, Object> agent = new HashMap<>();
                                        agent.put("user_id", input.getText().toString());
                                        agent.put("user_role", "MANAGER");

                                        db.collection("managers").document(input.getText().toString()).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.getResult().exists()) {
                                                            Map<String, Object> shop = new HashMap<>();
                                                            shop.put("shop_name", shop_name);
                                                            shop.put("vendor_id", vendor_number);
                                                            shop.put("shop_id", shop_id);

                                                            managers.add(input.getText().toString());
                                                            arrayAdapter.notifyDataSetChanged();

                                                            Map<String, Object> managerInfo = new HashMap<>();
                                                            managerInfo.put("manager_count", managers.size());
                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                    .collection("shops").document(shop_id).update(managerInfo);

                                                            db.collection("managers").document(input.getText().toString()).collection("shops").document(shop_id)
                                                                    .set(shop);
                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                    .collection("shops").document(shop_id).collection("managers")
                                                                    .document(input.getText().toString()).set(agent);
                                                        } else {
                                                            db.collection("managers").document(input.getText().toString()).set(agent)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Map<String, Object> shop = new HashMap<>();
                                                                            shop.put("shop_name", shop_name);
                                                                            shop.put("vendor_id", vendor_number);
                                                                            shop.put("shop_id", shop_id);
                                                                            db.collection("managers").document(input.getText().toString()).collection("shops").document(shop_id)
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
        if (v.getId() == R.id.lv_managers) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.assign_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit:
                AlertDialog.Builder builder = new AlertDialog.Builder(ManagersActivity.this);
                builder.setTitle("Manager mobile number:");
                input = new EditText(ManagersActivity.this);
                input.setText(managers.get(info.position));
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Map<String, Object> agent = new HashMap<>();
                        agent.put("user_id", input.getText().toString());
                        agent.put("user_role", "MANAGER");

                        db.collection("managers").document(managers.get(info.position)).delete();
                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                .collection("shops").document(shop_id).collection("managers")
                                .document(managers.get(info.position)).delete();

                        db.collection("managers").document(input.getText().toString()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Map<String, Object> shop = new HashMap<>();
                                            shop.put("shop_name", shop_name);
                                            shop.put("vendor_id", vendor_number);
                                            shop.put("shop_id", shop_id);

                                            db.collection("managers").document(input.getText().toString()).collection("shops").document(shop_id)
                                                    .set(shop);
                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                    .collection("shops").document(shop_id).collection("managers")
                                                    .document(input.getText().toString()).set(agent);
                                        } else {
                                            db.collection("managers").document(input.getText().toString()).set(agent)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Map<String, Object> shop = new HashMap<>();
                                                            shop.put("shop_name", shop_name);
                                                            shop.put("vendor_id", vendor_number);
                                                            shop.put("shop_id", shop_id);
                                                            db.collection("managers").document(input.getText().toString()).collection("shops").document(shop_id)
                                                                    .set(shop);
                                                        }
                                                    });
                                        }
                                        managers.set(info.position, input.getText().toString());
                                        arrayAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });

                builder.show();
                return super.onContextItemSelected(item);


            case R.id.action_delete:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(ManagersActivity.this);
                builder2.setTitle("Delete manager?");
                builder2.setNegativeButton("Cancel", null);

                builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("managers").document(managers.get(info.position)).delete();
                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                .collection("shops").document(shop_id).collection("managers")
                                .document(managers.get(info.position)).delete();
                        managers.remove(info.position);
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
