package com.ubuyquick.vendor.shop;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.InventoryProductAdapter;
import com.ubuyquick.vendor.model.InventoryProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryProductsActivity extends AppCompatActivity {

    private static final String TAG = "InventoryProductsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Button btn_search;
    private EditText et_search;

    private String shop_id, category, sub_category;
    private RecyclerView rv_products;
    private List<InventoryProduct> inventoryProducts;
    private List<InventoryProduct> inventorySearchProducts;
    private InventoryProductAdapter inventoryProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_products);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("sub_category_name"));

        shop_id = getIntent().getStringExtra("shop_id");
        category = getIntent().getStringExtra("category");
        sub_category = getIntent().getStringExtra("sub_category");

        et_search = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        rv_products = (RecyclerView) findViewById(R.id.rv_products) ;
        inventoryProductAdapter = new InventoryProductAdapter(this, shop_id);
        inventoryProducts = new ArrayList<>();
        inventorySearchProducts = new ArrayList<>();
        rv_products.setAdapter(inventoryProductAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = et_search.getText().toString();
                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                        .collection("shops").document(shop_id).collection("inventory")
                        .whereEqualTo("product_name", search).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                if (documents.size() > 0) {
                                    for (DocumentSnapshot document : documents) {
                                        Map<String, Object> product = document.getData();
                                        inventorySearchProducts.add(new InventoryProduct(product.get("product_name").toString(),
                                                Integer.parseInt(product.get("product_quantity").toString()),
                                                Double.parseDouble(product.get("product_mrp").toString()),
                                                product.get("image_url").toString(), true, document.getId(),
                                                product.get("category").toString(), product.get("sub_category").toString()));
                                    }
                                    inventoryProductAdapter.setInventoryProducts(inventorySearchProducts);
                                    et_search.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if (TextUtils.isEmpty(et_search.getText())) {
                                                inventoryProductAdapter.setInventoryProducts(inventoryProducts);
                                                inventorySearchProducts.clear();
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(InventoryProductsActivity.this, "No item found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });



        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).collection("inventory").whereEqualTo("sub_category", sub_category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Map<String, Object> product = document.getData();
                            inventoryProducts.add(new InventoryProduct(product.get("product_name").toString(),
                                    Integer.parseInt(product.get("product_quantity").toString()),
                                    Double.parseDouble(product.get("product_mrp").toString()),
                                    product.get("image_url").toString(),
                                    true, document.getId(), product.get("category").toString(),
                                    product.get("sub_category").toString()));
                        }
                        inventoryProductAdapter.setInventoryProducts(inventoryProducts);
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
