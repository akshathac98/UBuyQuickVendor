package com.ubuyquick.vendor.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.orders.AcceptedOrdersFragment;
import com.ubuyquick.vendor.orders.CancelledOrdersFragment;
import com.ubuyquick.vendor.orders.DeliveredOrdersFragment;
import com.ubuyquick.vendor.orders.NewOrdersFragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFragment extends Fragment {

    private static final String TAG = "OrderFragment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Map<String, Object> newOrder = new HashMap<>();
                final String timestamp = new Timestamp(System.currentTimeMillis()).getTime() + "";
                newOrder.put("customer_name", "Ajay Srinivas");
                newOrder.put("customer_id", "124124124");
                newOrder.put("delivery_address", "Hegganahalli, Peenya");
                newOrder.put("order_id", timestamp);
                newOrder.put("ordered_at", timestamp);
                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document("BHYRAVA_PROVISIONS")
                        .collection("new_orders").document(timestamp)
                        .set(newOrder)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });

                String[] product_names = {"India Gate Basmati Rice", "Colgate Active Salt", "Dairy Milk Silk", "Eggs - Farm Fresh", "Tata Salt (Crystal)", "Goldwinner Oil 5L", "Colgate Active Salt"};
                String[] product_images = {"https://www.bigbasket.com/media/uploads/p/l/220612_2-india-gate-basmati-rice-dubar.jpg",
                        "http://www.wilko.com/content/ebiz/wilkinsonplus/invt/0274546/0274546_l.jpg",
                        "http://www.avdeal.in/media/catalog/product/cache/1/thumbnail/960x/17f82f742ffe127f42dca9de82fb58b1/8/9/8901233021430.jpg",
                        "https://img1.etsystatic.com/190/1/11623135/il_570xN.1490551059_ghnx.jpg",
                        "https://www.hi5mart.com/image/cache/catalog/Grocery%20Staples/sugarandsalt/Tata%20Salt%20-%20Iodized,%201%20kg%20Pouch-750x750.jpg",
                        "https://5.imimg.com/data5/MP/RX/MY-9290782/goldwinner-oil-wholesale-in-chennai-500x500.jpg",
                        "http://www.wilko.com/content/ebiz/wilkinsonplus/invt/0274546/0274546_l.jpg"};

                for (int i = 0; i < product_images.length; i++) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("name", product_names[i]);
                    product.put("image_url", product_images[i]);
                    product.put("quantity", 15);
                    product.put("mrp", 27.0);
                    product.put("available", false);
                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document("BHYRAVA_PROVISIONS")
                            .collection("new_orders").document(timestamp).collection("products")
                            .add(product);
                }

            }
        });
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        Fragment newOrderFragment = new NewOrdersFragment();
        Fragment acceptedOrderFragment = new AcceptedOrdersFragment();
        Fragment cancelledOrderFragment = new CancelledOrdersFragment();
        Fragment deliveredOrderFragment = new DeliveredOrdersFragment();

        Bundle orderArgs = new Bundle();
        orderArgs.putString("shop_id", getArguments().getString("shop_id"));
        orderArgs.putString("shop_name", getArguments().getString("shop_name"));
        orderArgs.putString("vendor_id", getArguments().getString("vendor_id"));
        newOrderFragment.setArguments(orderArgs);
        acceptedOrderFragment.setArguments(orderArgs);
        cancelledOrderFragment.setArguments(orderArgs);
        deliveredOrderFragment.setArguments(orderArgs);

        adapter.addFragment(newOrderFragment, "New");
        adapter.addFragment(acceptedOrderFragment, "Accepted");
        adapter.addFragment(cancelledOrderFragment, "Cancelled");
        adapter.addFragment(deliveredOrderFragment, "Delivered");
        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
