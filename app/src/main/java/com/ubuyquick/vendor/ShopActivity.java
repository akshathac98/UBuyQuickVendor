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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.shop.AnalysisFragment;
import com.ubuyquick.vendor.shop.CreditFragment;
import com.ubuyquick.vendor.shop.InventoryFragment;
import com.ubuyquick.vendor.shop.OrderFragment;
import com.ubuyquick.vendor.shop.ProfileFragment;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {

    private static final String TAG = "ShopActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private String shop_name;
    private String vendor_id;
    private String image_url;

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

    private int LOGIN_MODE = 0;


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

        shop_id = getIntent().getStringExtra("shop_id");
        shop_name = getIntent().getStringExtra("shop_name");
        vendor_id = getIntent().getStringExtra("vendor_id");
        image_url = getIntent().getStringExtra("image_url");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }*/

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
                    Fragment profileFragment = new ProfileFragment();
                    Bundle profileArgs = new Bundle();
                    profileArgs.putString("shop_id", shop_id);
                    profileArgs.putString("shop_name", shop_name);
                    profileArgs.putString("vendor_id", vendor_id);
                    profileArgs.putString("image_url", image_url);
                    profileFragment.setArguments(profileArgs);
                    return profileFragment;
                case 1:
                    Fragment orderFragment = new OrderFragment();
                    Bundle orderArgs = new Bundle();
                    orderArgs.putString("shop_id", shop_id);
                    orderArgs.putString("shop_name", shop_name);
                    orderArgs.putString("vendor_id", vendor_id);
                    orderFragment.setArguments(orderArgs);
                    return orderFragment;
                case 2:
                    Fragment creditFragment = new CreditFragment();
                    Bundle creditArgs = new Bundle();
                    creditArgs.putString("shop_id", shop_id);
                    creditArgs.putString("vendor_id", vendor_id);
                    creditArgs.putString("shop_name", shop_name);
                    creditFragment.setArguments(creditArgs);
                    return creditFragment;
                case 3:
                    Fragment fragment = new InventoryFragment();
                    Bundle args = new Bundle();
                    args.putString("shop_id", shop_id);
                    args.putString("vendor_id", vendor_id);
                    fragment.setArguments(args);
                    return fragment;
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
