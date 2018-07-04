package com.ubuyquick.vendor.shop;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private View view;
    private ImageView img_shop;
    private EditText input;
    private TextView tv_shop_name, tv_shop_location, tv_shop_timings;

    private String vendor_number;
    private String shop_id;
    private String shop_name;
    private String vendor_id;
    private String image_url;

    private int LOGIN_MODE;

    private Button btn_delivery_agent;
    private Button btn_manager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);

        shop_id = getArguments().getString("shop_id");
        shop_name = getArguments().getString("shop_name");
        vendor_id = getArguments().getString("vendor_id");

        initializeViews();
        initialize();

        return view;
    }

    private void initializeViews() {
        img_shop = (ImageView) view.findViewById(R.id.img_shop);
        tv_shop_location = (TextView) view.findViewById(R.id.tv_shop_address);
        tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
        tv_shop_timings = (TextView) view.findViewById(R.id.tv_shop_timings);

        btn_delivery_agent = (Button) view.findViewById(R.id.btn_delivery_agent);
        btn_manager = (Button) view.findViewById(R.id.btn_manager);

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        if (LOGIN_MODE == 1) {
            btn_manager.setVisibility(View.GONE);
        } else if (LOGIN_MODE == 2) {
            btn_manager.setVisibility(View.GONE);
            btn_delivery_agent.setVisibility(View.GONE);
        }

        btn_delivery_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delivery Agent mobile number:");
                input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Add Agent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Map<String, Object> agent = new HashMap<>();
                        agent.put("user_id", input.getText().toString());
                        agent.put("user_role", "DELIVERY_AGENT");

                        db.collection("delivery_agents").document(input.getText().toString()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Map<String, Object> shop = new HashMap<>();
                                            shop.put("shop_name", shop_name);
                                            shop.put("vendor_id", vendor_number);
                                            shop.put("shop_id", shop_id);
                                            db.collection("delivery_agents").document(input.getText().toString()).collection("shops").document(shop_id)
                                                    .set(shop);
                                        } else {
                                            db.collection("delivery_agents").document(input.getText().toString()).set(agent)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Map<String, Object> shop = new HashMap<>();
                                                            shop.put("shop_name", shop_name);
                                                            shop.put("vendor_id", vendor_number);
                                                            shop.put("shop_id", shop_id);
                                                            db.collection("delivery_agents").document(input.getText().toString()).collection("shops").document(shop_id)
                                                                    .set(shop);
                                                        }
                                                    });
                                        }
                                    }
                                });


                    }
                });
                builder.show();

            }
        });

        btn_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Manager mobile number:");
                input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Add Manager", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Map<String, Object> agent = new HashMap<>();
                        agent.put("user_id", input.getText().toString());
                        agent.put("user_role", "MANAGER");

                        db.collection("managers").document(input.getText().toString()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            Map<String, Object> shop = new HashMap<>();
                                            shop.put("shop_name", shop_name);
                                            shop.put("vendor_id", vendor_number);
                                            shop.put("shop_id", shop_id);
                                            db.collection("managers").document(input.getText().toString()).collection("shops").document(shop_id)
                                                    .set(shop);
                                        } else {
                                            db.collection("managers").document(input.getText().toString()).set(agent)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Map<String, Object> shop = new HashMap<>();
                                                            shop.put("shop_name", shop_name);
                                                            shop.put("vendor_id", vendor_number);
                                                            shop.put("shop_id", shop_id);
                                                            db.collection("managers").document(input.getText().toString()).collection("shops").document(shop_id)
                                                                    .set(shop);
                                                        }
                                                    });
                                        }
                                    }
                                });


                    }
                });
                builder.show();

            }
        });
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();

        vendor_number = vendor_id;
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(getContext()).getConfig());

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
        .collection("shops").document(shop_id).get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> shop = task.getResult().getData();
                UniversalImageLoader.setImage(shop.get("shop_image_url").toString(), img_shop);
                tv_shop_location.setText(shop.get("shop_address").toString());
                tv_shop_name.setText(shop.get("shop_name").toString());
                tv_shop_timings.setText(shop.get("shop_timings").toString());

            }
        });

    }
}
