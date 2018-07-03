package com.ubuyquick.vendor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.adapter.AddProductAdapter;
import com.ubuyquick.vendor.model.AddProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private String category;
    private String sub_category;
    private List<AddProduct> addProducts;
    private RecyclerView rv_products;
    private AddProductAdapter addProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shop_id = getIntent().getStringExtra("shop_id");
        category = getIntent().getStringExtra("category");
        sub_category = getIntent().getStringExtra("sub_category");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        initialize();
    }

    private void initializeViews() {
        rv_products = (RecyclerView) findViewById(R.id.rv_products);
    }

    private void initialize() {
        addProductAdapter = new AddProductAdapter(this, shop_id, category, sub_category);
        addProducts = new ArrayList<>();
        rv_products.setAdapter(addProductAdapter);

        db.collection("product_categories").document(category).collection(sub_category).get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents) {
                        Map<String, Object> product = document.getData();
                        addProducts.add(new AddProduct(product.get("product_name").toString(), Double.parseDouble(product.get("product_mrp").toString()), product.get("product_id").toString(), product.get("image_url").toString()
                        , category, sub_category));
                    }
                    addProductAdapter.setAddProducts(addProducts);
                } else {
                    Toast.makeText(AddProductActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
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

