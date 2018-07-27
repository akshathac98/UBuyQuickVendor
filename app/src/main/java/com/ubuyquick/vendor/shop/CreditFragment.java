package com.ubuyquick.vendor.shop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.adapter.CreditAdapter;
import com.ubuyquick.vendor.credits.CreditNotesFragment;
import com.ubuyquick.vendor.credits.ShopCreditFragment;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.orders.AcceptedOrdersFragment;
import com.ubuyquick.vendor.orders.CancelledOrdersFragment;
import com.ubuyquick.vendor.orders.DeliveredOrdersFragment;
import com.ubuyquick.vendor.orders.NewOrdersFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditFragment extends Fragment {

    private static final String TAG = "CreditFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView rv_credits;
    private CreditAdapter creditAdapter;
    private List<Credit> credits;
    private EditText et_search;
    private Button btn_add, btn_message;

    private String shop_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        shop_id = getArguments().getString("shop_id");

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);


        return view;
    }

    private void setupViewPager(ViewPager viewPager) {

        OrderFragment.Adapter adapter = new OrderFragment.Adapter(getChildFragmentManager());
        Fragment creditNotesFragment = new CreditNotesFragment();
        Fragment shopCreditsFragment = new ShopCreditFragment();

        Bundle orderArgs = new Bundle();
        orderArgs.putString("shop_id", getArguments().getString("shop_id"));
        orderArgs.putString("shop_name", getArguments().getString("shop_name"));
        orderArgs.putString("vendor_id", getArguments().getString("vendor_id"));
        creditNotesFragment.setArguments(orderArgs);
        shopCreditsFragment.setArguments(orderArgs);

        adapter.addFragment(shopCreditsFragment, "Shop Credits");
        adapter.addFragment(creditNotesFragment, "Credit Notes");
        viewPager.setAdapter(adapter);

    }
}
