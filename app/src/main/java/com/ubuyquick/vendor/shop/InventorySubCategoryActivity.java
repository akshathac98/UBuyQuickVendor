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

import java.util.HashMap;

public class InventorySubCategoryActivity extends AppCompatActivity {

    private static final String TAG = "InventorySubCategoryActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ListView list_subcategories;

    private String shop_id;
    private String category;
    private String[] sub;
    private HashMap<String, String[]> subcategories;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shop_id = getIntent().getStringExtra("shop_id");
        category = getIntent().getStringExtra("category");

        subcategories = new HashMap<>();
        subcategories.put("dry_fruits", new String[]{"sub_category_1", "sub_category_2"});

        sub = subcategories.get(category);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sub);
        list_subcategories = (ListView) findViewById(R.id.list_subcategories);
        list_subcategories.setAdapter(arrayAdapter);
        list_subcategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(InventorySubCategoryActivity.this, InventoryProductsActivity.class);
                i.putExtra("shop_id", shop_id);
                i.putExtra("category", "dry_fruits");
                i.putExtra("sub_category", sub[position]);
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
