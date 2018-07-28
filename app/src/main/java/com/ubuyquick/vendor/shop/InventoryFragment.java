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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.CategoryActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.SubCategoryActivity;
import com.ubuyquick.vendor.adapter.InventoryProductAdapter;
import com.ubuyquick.vendor.model.InventoryProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private static final String TAG = "InventoryFragment";

    private Button btn_add_product;
    private Button btn_change_view;

    private String shop_id;
    private ListView list_categories;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
        btn_add_product = view.findViewById(R.id.btn_add_product);
        btn_change_view = view.findViewById(R.id.btn_change_view);

        list_categories = (ListView) view.findViewById(R.id.list_categories);

        list_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), InventorySubCategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                i.putExtra("category", "dry_fruits");
                startActivity(i);
            }
        });

        btn_change_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), InventoryCategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                startActivity(i);
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
        return view;
    }
}
