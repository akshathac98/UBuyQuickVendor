package com.ubuyquick.vendor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.adapter.ShopAdapter;
import com.ubuyquick.vendor.auth.LoginActivity;
import com.ubuyquick.vendor.model.Shop;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private DocumentReference vendorRef;

    private ProgressBar progressBar;

    private int LOGIN_MODE;
    private String not_available = "NA";
    private TextView tv_email, tv_vendor_email, tv_vendor_aadhar, tv_name, tv_phone, tv_aadhar, tv_pan, tv_verified, tv_status;
    private Button btn_logout, btn_edit_profile, btn_add_shop;
    private View view;
    private CircleImageView img_vendor;
    private RecyclerView rv_shops;
    private ShopAdapter shopAdapter;
    private List<Shop> shops;
    private RelativeLayout relativeLayout;
    private RelativeLayout.LayoutParams layoutParams;

    private String vendor_id;
    private String shop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        initialize();

    }

    private void loadShopList() {
        shops.clear();

        if (LOGIN_MODE == 0) {
            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    shops.add(new Shop(document.get("shop_image_url").toString(),
                                            document.get("shop_name").toString(),
                                            Boolean.parseBoolean(document.get("shop_status").toString()),
                                            document.get("shop_id").toString(), mAuth.getCurrentUser().getPhoneNumber().substring(3),
                                            Boolean.parseBoolean(document.get("quick_delivery").toString())));
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                shopAdapter.setShops(shops);
                                rv_shops.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else if (LOGIN_MODE == 1) {
            db.collection("managers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    shop = document.get("vendor_id").toString();
                                }
                                try {
                                    db.collection("vendors").document(shop).collection("shops").whereEqualTo("shop_id", task.getResult().getDocuments().get(0).getData().get("shop_id"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            shops.add(new Shop(document.get("shop_image_url").toString(),
                                                                    document.get("shop_name").toString(),
                                                                    Boolean.parseBoolean(document.get("shop_status").toString()),
                                                                    document.get("shop_id").toString(), shop,
                                                                    Boolean.parseBoolean(document.get("quick_delivery").toString())));
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        shopAdapter.setShops(shops);
                                                        rv_shops.setVisibility(View.VISIBLE);
                                                    } else {
                                                        Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    e.getLocalizedMessage();
                                    TextView no_orders = new TextView(HomeActivity.this);
                                    no_orders.setLayoutParams(layoutParams);
                                    no_orders.setText("You have no shops to manage.");
                                    relativeLayout.addView(no_orders);
                                }
                                shopAdapter.setShops(shops);
                            } else {
                                Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            tv_vendor_email.setVisibility(View.GONE);
            tv_email.setVisibility(View.GONE);
            tv_vendor_aadhar.setVisibility(View.GONE);
            tv_aadhar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } else {
            db.collection("delivery_agents").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    shop = document.get("vendor_id").toString();
                                }
                                db.collection("vendors").document(shop).collection("shops").whereEqualTo("shop_id", task.getResult().getDocuments().get(0).getData().get("shop_id"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        shops.add(new Shop(document.get("shop_image_url").toString(),
                                                                document.get("shop_name").toString(),
                                                                Boolean.parseBoolean(document.get("shop_status").toString()),
                                                                document.get("shop_id").toString(), shop,
                                                                Boolean.parseBoolean(document.get("quick_delivery").toString())));
                                                    }
                                                    shopAdapter.setShops(shops);
                                                } else {
                                                    Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                shopAdapter.setShops(shops);
                            } else {
                                Toast.makeText(HomeActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            tv_vendor_email.setVisibility(View.GONE);
            tv_email.setVisibility(View.GONE);
            tv_vendor_aadhar.setVisibility(View.GONE);
            tv_aadhar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        rv_shops.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        loadShopList();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(this).getConfig());

        shopAdapter = new ShopAdapter(this);
        rv_shops.setAdapter(shopAdapter);
        shops = new ArrayList<>();

        if (LOGIN_MODE == 0) {
            vendorRef = db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3));
            vendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> vendor = document.getData();
                            UniversalImageLoader.setImage(vendor.get("photo_url").toString(), img_vendor);
                            tv_email.setText(vendor.get("email").toString());
                            tv_name.setText(vendor.get("name").toString());
                            tv_phone.setText(vendor.get("phone").toString());
                            tv_aadhar.setText(vendor.get("aadhar_number").toString());
                            if ((boolean) vendor.get("verified")) {
                                tv_verified.setText("Verified Vendor");
                            } else {
                                tv_verified.setText("Not Verified");
                            }
                        }
                    }
                }
            });
        } else if (LOGIN_MODE == 1) {
            vendorRef = db.collection("managers").document(mAuth.getCurrentUser().getPhoneNumber().substring(3));
            vendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            tv_name.setText(document.getData().get("name").toString());
                        }
                    }
                }
            });
            tv_email.setText(not_available);
            tv_phone.setText(mAuth.getCurrentUser().getPhoneNumber().substring(3));
            tv_aadhar.setText(not_available);
            tv_verified.setText("Manager");
        } else if (LOGIN_MODE == 2) {
            vendorRef = db.collection("delivery_agents").document(mAuth.getCurrentUser().getPhoneNumber().substring(3));
            vendorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            tv_name.setText(document.getData().get("name").toString());
                        }
                    }
                }
            });
            tv_verified.setText("Delivery Agent");
            tv_phone.setText(mAuth.getCurrentUser().getPhoneNumber().substring(3));
        } else {
            tv_email.setText(not_available);
            tv_phone.setText(mAuth.getCurrentUser().getPhoneNumber().substring(3));
            tv_aadhar.setText(not_available);
            tv_verified.setText("Delivery Agent");
        }


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Confirm Log Out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        });

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
            }
        });
    }

    private void initializeViews() {
        rv_shops = (RecyclerView) findViewById(R.id.rv_shops);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout);
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tv_aadhar = (TextView) header.findViewById(R.id.tv_aadhar);
        tv_vendor_aadhar = (TextView) header.findViewById(R.id.tv_vendor_aadhar);
        tv_name = (TextView) header.findViewById(R.id.tv_vendor_name);
        tv_email = (TextView) header.findViewById(R.id.tv_email);
        view = (View) header.findViewById(R.id.view);
        tv_vendor_email = (TextView) header.findViewById(R.id.tv_vendor_email);
        tv_phone = (TextView) header.findViewById(R.id.tv_phone);
        tv_verified = (TextView) header.findViewById(R.id.tv_verified);
        img_vendor = (CircleImageView) header.findViewById(R.id.img_vendor);
        btn_edit_profile = (Button) header.findViewById(R.id.btn_edit_profile);
        btn_logout = (Button) header.findViewById(R.id.btn_logout);
        btn_add_shop = (Button) findViewById(R.id.btn_add_shop);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        SharedPreferences preferences = getSharedPreferences("LOGIN_MODE", MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        if (LOGIN_MODE > 0) {
            btn_add_shop.setVisibility(View.GONE);
        } else {


            btn_add_shop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, AddShopActivity.class));
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });
            tv_status = (TextView) findViewById(R.id.tv_status);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
