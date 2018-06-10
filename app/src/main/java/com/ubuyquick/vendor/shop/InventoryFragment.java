package com.ubuyquick.vendor.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ubuyquick.vendor.CategoryActivity;
import com.ubuyquick.vendor.R;

public class InventoryFragment extends Fragment {

    private static final String TAG = "InventoryFragment";

    private Button btn_add_product;

    private String shop_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shop_id = getArguments().getString("shop_id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inventory_fragment, container, false);

        shop_id = getArguments().getString("shop_id");

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CategoryActivity.class);
                i.putExtra("shop_id", shop_id);
                startActivity(i);
            }
        });

        return view;
    }
}
