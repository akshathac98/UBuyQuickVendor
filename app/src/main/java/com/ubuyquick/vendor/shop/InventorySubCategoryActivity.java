package com.ubuyquick.vendor.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.AddProductActivity;
import com.ubuyquick.vendor.R;

import java.util.ArrayList;
import java.util.HashMap;

public class InventorySubCategoryActivity extends AppCompatActivity {

    private static final String TAG = "InventorySubCategoryActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private ListView lv_cats;
    private ArrayList<String> cats;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("sub_category"));

        shop_id = getIntent().getStringExtra("shop_id");

        lv_cats = (ListView) findViewById(R.id.lv_list);
        cats = getIntent().getStringArrayListExtra("list");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cats);
        lv_cats.setAdapter(arrayAdapter);

        lv_cats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(InventorySubCategoryActivity.this, InventoryProductsActivity.class);
                i.putExtra("category", cats.get(position));
                i.putExtra("shop_id", getIntent().getStringExtra("shop_id"));
                startActivity(i);
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
