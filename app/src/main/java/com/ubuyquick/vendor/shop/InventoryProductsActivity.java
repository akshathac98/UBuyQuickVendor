package com.ubuyquick.vendor.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Credentials;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.InventoryProductAdapter;
import com.ubuyquick.vendor.model.InventoryProduct;
import com.ubuyquick.vendor.model.MainSearchProduct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryProductsActivity extends AppCompatActivity {

    private static final String TAG = "InventoryProductsActivi";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Button btn_search;
    private EditText et_search;

    private String shop_id, category, sub_category;

    private int LOGIN_MODE = 0;
    private String vendor_id, number;

    private RecyclerView rv_products;
    private List<MainSearchProduct> products;
    private InventoryProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_products);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("category"));

        shop_id = getIntent().getStringExtra("shop_id");
        vendor_id = getIntent().getStringExtra("vendor_id");
        category = getIntent().getStringExtra("category");
        sub_category = getIntent().getStringExtra("sub_category");

        rv_products = (RecyclerView) findViewById(R.id.rv_products);
        products = new ArrayList<>();
        productAdapter = new InventoryProductAdapter(this, getIntent().getStringExtra("shop_id"));
        rv_products.setAdapter(productAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);
        if (LOGIN_MODE == 1) {
            number = vendor_id;
        } else {
            number = mAuth.getCurrentUser().getPhoneNumber().substring(3);
        }
        final HashMap<String, Double> priceList = new HashMap<>();

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", Credentials.basic("elastic", "IcORsWAWIOYtaZLNpJgbUvw1"));

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("q", "message:*" + getIntent().getStringExtra("category") + "*");
        queryMap.put("from", "0");
        queryMap.put("size", "50");

        AndroidNetworking.get("https://08465455b9e04080ada3e4855fc4fc86.ap-southeast-1.aws.found.io:9243/ubq-has/_search")
                .addQueryParameter(queryMap)
                .addHeaders(headerMap)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "onResponse: " + response.toString());
                    JSONArray productsArray = response.getJSONObject("hits").getJSONArray("hits");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productObj = productsArray.getJSONObject(i);
                        String product_name = productObj.getJSONObject("_source").getString("Products");
                        String product_measure = productObj.getJSONObject("_source").getString("Measure");
                        double product_mrp = productObj.getJSONObject("_source").getDouble("Price");

                        priceList.put(product_name, productObj.getJSONObject("_source").getDouble("Price"));
                        products.add(new MainSearchProduct(product_name, product_mrp, product_measure, 1));
                    }
                    productAdapter.setProducts(products);
                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: exception: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d(TAG, "onError: " + anError.getErrorDetail());
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
