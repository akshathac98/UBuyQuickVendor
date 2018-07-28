package com.ubuyquick.vendor.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.CategoryActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.InventoryProductAdapter;
import com.ubuyquick.vendor.model.InventoryProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private static final String TAG = "InventoryFragment";

    private Button btn_add_product;
    private Button btn_search;
    private Button btn_change_view;

    private String shop_id;
    private RecyclerView rv_products;
    private List<InventoryProduct> inventoryProducts;
    private List<InventoryProduct> inventorySearchProducts;
    private InventoryProductAdapter inventoryProductAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText et_search;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shop_id = getArguments().getString("shop_id");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inventory_fragment, container, false);

        shop_id = getArguments().getString("shop_id");
        et_search = (view.findViewById(R.id.et_search));
        btn_search = (view.findViewById(R.id.btn_search));
        btn_add_product = view.findViewById(R.id.btn_add_product);
        btn_change_view = view.findViewById(R.id.btn_change_view);
        rv_products = (RecyclerView) view.findViewById(R.id.rv_products) ;
        inventoryProductAdapter = new InventoryProductAdapter(getContext(), shop_id);
        inventoryProducts = new ArrayList<>();
        inventorySearchProducts = new ArrayList<>();
        rv_products.setAdapter(inventoryProductAdapter);

        btn_change_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), InventoryCategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                startActivity(i);
            }
        });

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
                                    Toast.makeText(getContext(), "No item found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddFromActivity.class);
                i.putExtra("shop_id", shop_id);
                startActivity(i);
            }
        });

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).collection("inventory").get()
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

        return view;
    }
}
