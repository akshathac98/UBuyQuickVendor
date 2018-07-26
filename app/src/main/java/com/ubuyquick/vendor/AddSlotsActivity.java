package com.ubuyquick.vendor;

import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.adapter.TimeSlotAdapter;
import com.ubuyquick.vendor.model.TimeSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSlotsActivity extends AppCompatActivity {

    private static final String TAG = "AddSlotsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id, from, to;
    private ImageButton btn_add;
    private Button btn_from, btn_to;
    private EditText et_deliveries;

    private RecyclerView rv_slots;
    private List<TimeSlot> timeSlots;
    private TimeSlotAdapter timeSlotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slots);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        shop_id = getIntent().getStringExtra("shop_id");

        rv_slots = (RecyclerView) findViewById(R.id.rv_slots);
        timeSlots = new ArrayList<>();
        timeSlotAdapter = new TimeSlotAdapter(this, shop_id);
        rv_slots.setAdapter(timeSlotAdapter);
        btn_add = (ImageButton) findViewById(R.id.btn_add);
        btn_from = (Button) findViewById(R.id.btn_from);
        btn_to = (Button) findViewById(R.id.btn_to);
        et_deliveries = (EditText) findViewById(R.id.et_deliveries);

        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddSlotsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12) {
                                    from = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " AM";
                                    btn_from.setText(from);
                                } else if (hourOfDay > 12) {
                                    from = String.format("%02d", hourOfDay % 12) + ":" + String.format("%02d", minute) + " PM";
                                    btn_from.setText(from);
                                } else {
                                    from = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " PM";
                                    btn_from.setText(from);
                                }
                            }
                        }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddSlotsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12) {
                                    to = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " AM";
                                    btn_to.setText(to);
                                } else if (hourOfDay > 12) {
                                    to = String.format("%02d", hourOfDay % 12) + ":" + String.format("%02d", minute) + " PM";
                                    btn_to.setText(to);
                                } else {
                                    to = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " PM";
                                    btn_to.setText(to);
                                }
                            }
                        }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_deliveries.getText().toString().isEmpty()) {
                    Toast.makeText(AddSlotsActivity.this, "Number of deliveries can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    final int d = Integer.parseInt(et_deliveries.getText().toString());
                    Map<String, Object> slot = new HashMap<>();
                    slot.put("from", from);
                    slot.put("to", to);
                    slot.put("deliveries", d);

                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("shops").document(shop_id).collection("time_slots").add(slot)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            timeSlots.add(new TimeSlot(from + " to " + to, d, documentReference.getId()));
                            timeSlotAdapter.setTimeSlots(timeSlots);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddSlotsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    btn_from.setText("FROM");
                    btn_to.setText("TO");
                    et_deliveries.setText("");
                }
            }
        });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).collection("time_slots").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Map<String, Object> slot = document.getData();
                            timeSlots.add(new TimeSlot(slot.get("from").toString() + " to " + slot.get("to").toString(),
                                    Integer.parseInt(slot.get("deliveries").toString()), document.getId()));
                        }
                        timeSlotAdapter.setTimeSlots(timeSlots);
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
