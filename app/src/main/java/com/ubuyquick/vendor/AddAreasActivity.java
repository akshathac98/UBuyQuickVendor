package com.ubuyquick.vendor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.adapter.AreaAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddAreasActivity extends AppCompatActivity {

    private static final String TAG = "AddAreasActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id, vendor_number, shop_name;

    private List<String> areas;
    private AreaAdapter areaAdapter;
    private RecyclerView rv_areas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_areas);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        shop_id = getIntent().getStringExtra("shop_id");
        shop_name = getIntent().getStringExtra("shop_name");
        vendor_number = getIntent().getStringExtra("vendor_id");

        rv_areas = (RecyclerView) findViewById(R.id.rv_areas);
        areaAdapter = new AreaAdapter(this);
        areas = new ArrayList<>();
        areas.add("Area 1");
        areas.add("Area 2");
        areas.add("Area 3");
        areas.add("Area 4");
        areas.add("Area 5");

        rv_areas.setAdapter(areaAdapter);
        areaAdapter.setAreas(areas);

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
