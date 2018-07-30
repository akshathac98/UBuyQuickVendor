package com.ubuyquick.vendor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ListView list_categories;

    private String shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shop_id = getIntent().getStringExtra("shop_id");

        list_categories = (ListView) findViewById(R.id.list_categories);

        list_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(CategoryActivity.this, SubCategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                i.putExtra("category", "dry_fruits");
                i.putExtra("category_name", list_categories.getItemAtPosition(position).toString());
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
