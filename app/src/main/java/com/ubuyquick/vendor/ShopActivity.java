package com.ubuyquick.vendor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.shop.AnalysisFragment;
import com.ubuyquick.vendor.shop.CreditFragment;
import com.ubuyquick.vendor.shop.OrderFragment;
import com.ubuyquick.vendor.shop.ProfileFragment;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("shop_name"));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ProfileFragment();
                case 1:
                    return new OrderFragment();
                case 2:
                    return new CreditFragment();
                default:
                    return new AnalysisFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
