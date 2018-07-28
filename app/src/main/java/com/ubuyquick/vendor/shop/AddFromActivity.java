package com.ubuyquick.vendor.shop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.CategoryActivity;
import com.ubuyquick.vendor.NewProductActivity;
import com.ubuyquick.vendor.R;

public class AddFromActivity extends AppCompatActivity {

    private static final String TAG = "AddFromActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private String category;
    private String sub_category;

    private Button btn_new, btn_master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_from);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shop_id = getIntent().getStringExtra("shop_id");
        category = getIntent().getStringExtra("category");
        sub_category = getIntent().getStringExtra("sub_category");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_master = (Button) findViewById(R.id.btn_master);
        btn_new = (Button) findViewById(R.id.btn_new);

        btn_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFromActivity.this, CategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                startActivity(i);
            }
        });

        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFromActivity.this, NewProductActivity.class);
                i.putExtra("shop_id", shop_id);
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
