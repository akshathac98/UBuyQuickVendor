package com.ubuyquick.vendor.shop;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.ubuyquick.vendor.AddAreasActivity;
import com.ubuyquick.vendor.AddShopActivity;
import com.ubuyquick.vendor.DeliveryAgentsActivity;
import com.ubuyquick.vendor.HomeActivity;
import com.ubuyquick.vendor.ManagersActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.auth.LoginActivity;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private View view;
    private com.makeramen.roundedimageview.RoundedImageView img_shop;
    private EditText input;
    private TextView tv_shop_name, tv_shop_location, tv_shop_timings, tv_specialization, tv_gstin;
    private TextView tv_status, tv_quick;

    private String vendor_number;
    private String shop_id;
    private String shop_name;
    private String vendor_id;
    private String image_url;
    private String timings_from;
    private String timings_to;

    private int LOGIN_MODE;
    private int manager_count = 0, deliveryagent_count = 0;

    private Button btn_delivery_agent;
    private Button btn_manager;
    private Button btn_edit_profile;
    private Button btn_delete_shop;
    private Button btn_add_area;
    private Button btn_add_slot;
    private Button btn_to;
    private Button btn_from;
    private Switch btn_shop_status;
    private Switch btn_quick_delivery;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);

        shop_id = getArguments().getString("shop_id");
        shop_name = getArguments().getString("shop_name");
        vendor_id = getArguments().getString("vendor_id");
        image_url = getArguments().getString("image_url");

        mAuth = FirebaseAuth.getInstance();

        vendor_number = vendor_id;
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(getContext()).getConfig());

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        manager_count = Integer.parseInt(task.getResult().getData().get("manager_count").toString());
                        deliveryagent_count = Integer.parseInt(task.getResult().getData().get("deliveryagent_count").toString());
                    }
                });

        initializeViews();
        initialize();

        return view;
    }

    private void initializeViews() {
        img_shop = (com.makeramen.roundedimageview.RoundedImageView) view.findViewById(R.id.img_shop);
        tv_shop_location = (TextView) view.findViewById(R.id.tv_shop_address);
        tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
        tv_specialization = (TextView) view.findViewById(R.id.tv_shop_spec);
        tv_gstin = (TextView) view.findViewById(R.id.tv_gst);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_quick = (TextView) view.findViewById(R.id.tv_quick);

//        btn_delivery_agent = (Button) view.findViewById(R.id.btn_delivery_agent);
//        btn_manager = (Button) view.findViewById(R.id.btn_manager);
        btn_edit_profile = (Button) view.findViewById(R.id.btn_edit_profile);
        btn_to = (Button) view.findViewById(R.id.btn_to);
        btn_quick_delivery = (Switch) view.findViewById(R.id.btn_quick_delivery);
        btn_shop_status = (Switch) view.findViewById(R.id.btn_status);
        btn_from = (Button) view.findViewById(R.id.btn_from);
        btn_delete_shop = (Button) view.findViewById(R.id.btn_delete_shop);
        btn_delivery_agent = (Button) view.findViewById(R.id.btn_add_delivery);
        btn_manager = (Button) view.findViewById(R.id.btn_add_manager);
        btn_add_area = (Button) view.findViewById(R.id.btn_add_area);
        btn_add_slot = (Button) view.findViewById(R.id.btn_add_slot);

        btn_add_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddAreasActivity.class);
                i.putExtra("shop_id", shop_id);
                i.putExtra("vendor_id", vendor_number);
                i.putExtra("shop_name", shop_name);
                startActivity(i);
            }
        });

        SharedPreferences preferences = getContext().getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);

        if (LOGIN_MODE == 1) {
            btn_manager.setVisibility(View.GONE);
        } else if (LOGIN_MODE == 2) {
            btn_manager.setVisibility(View.GONE);
            btn_delivery_agent.setVisibility(View.GONE);
        }

        btn_delete_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setMessage("Confirm Delete Shop?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("shops").document(shop_id).delete();
                                Toast.makeText(getContext(), "Deleted shop successfully.", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });

        btn_delivery_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (deliveryagent_count == 0) {
                    View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_name_phone, null, false);

                    final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                    final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);


                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Delivery Agent mobile number:");
                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null);

                    builder.setPositiveButton("Add Agent", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Map<String, Object> agent = new HashMap<>();
                            agent.put("user_id", number.getText().toString());
                            agent.put("user_role", "DELIVERY_AGENT");
                            agent.put("name", name.getText().toString());

                            db.collection("delivery_agents").document(number.getText().toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()) {
                                                Map<String, Object> shop = new HashMap<>();
                                                shop.put("shop_name", shop_name);
                                                shop.put("vendor_id", vendor_number);
                                                shop.put("shop_id", shop_id);
                                                shop.put("image_url", image_url);

                                                Map<String, Object> agentInfo = new HashMap<>();
                                                agentInfo.put("deliveryagent_count", 1);
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).update(agentInfo);
                                                deliveryagent_count++;
                                                db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                        .set(shop);
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).collection("delivery_agents")
                                                        .document(number.getText().toString()).set(agent);
                                            } else {
                                                db.collection("delivery_agents").document(number.getText().toString()).set(agent)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Map<String, Object> shop = new HashMap<>();
                                                                shop.put("shop_name", shop_name);
                                                                shop.put("image_url", image_url);
                                                                shop.put("vendor_id", vendor_number);
                                                                shop.put("shop_id", shop_id);
                                                                db.collection("delivery_agents").document(number.getText().toString()).collection("shops").document(shop_id)
                                                                        .set(shop);
                                                            }
                                                        });
                                            }
                                        }
                                    });


                        }
                    });
                    builder.show();
                } else {
                    Intent i = new Intent(getContext(), DeliveryAgentsActivity.class);
                    i.putExtra("shop_id", shop_id);
                    i.putExtra("vendor_id", vendor_number);
                    i.putExtra("image_url", image_url);
                    i.putExtra("shop_name", shop_name);
                    startActivity(i);
                }

            }
        });

        btn_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (manager_count == 0) {
                    View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_name_phone, null, false);

                    final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                    final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Manager mobile number:");
                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null);

                    builder.setPositiveButton("Add Manager", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Map<String, Object> agent = new HashMap<>();
                            agent.put("user_id", number.getText().toString());
                            agent.put("user_role", "MANAGER");
                            agent.put("name", name.getText().toString());

                            db.collection("managers").document(number.getText().toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()) {
                                                Map<String, Object> shop = new HashMap<>();
                                                shop.put("shop_name", shop_name);
                                                shop.put("image_url", image_url);
                                                shop.put("vendor_id", vendor_number);
                                                shop.put("shop_id", shop_id);

                                                Map<String, Object> managerInfo = new HashMap<>();
                                                managerInfo.put("manager_count", 1);
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).update(managerInfo);
                                                manager_count++;

                                                db.collection("managers").document(number.getText().toString()).collection("shops").document(shop_id)
                                                        .set(shop);
                                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                        .collection("shops").document(shop_id).collection("managers")
                                                        .document(number.getText().toString()).set(agent);
                                            } else {
                                                db.collection("managers").document(number.getText().toString()).set(agent)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Map<String, Object> shop = new HashMap<>();
                                                                shop.put("shop_name", shop_name);
                                                                shop.put("vendor_id", vendor_number);
                                                                shop.put("image_url", image_url);
                                                                shop.put("shop_id", shop_id);
                                                                db.collection("managers").document(number.getText().toString()).collection("shops").document(shop_id)
                                                                        .set(shop);
                                                            }
                                                        });
                                            }
                                        }
                                    });


                        }
                    });
                    builder.show();
                } else {
                    Intent i = new Intent(getContext(), ManagersActivity.class);
                    i.putExtra("shop_id", shop_id);
                    i.putExtra("image_url", image_url);
                    i.putExtra("vendor_id", vendor_number);
                    i.putExtra("shop_name", shop_name);
                    startActivity(i);
                }
            }
        });


        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EditShopProfileActivity.class);
                i.putExtra("shop_id", shop_id);
                startActivity(i);
            }
        });

        btn_quick_delivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> shop = new HashMap<>();
                if (isChecked) {
                    tv_quick.setText("On");
                    shop.put("quick_delivery", true);
                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("shops").document(shop_id).update(shop);
                } else {
                    tv_quick.setText("Off");
                    shop.put("quick_delivery", false);
                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("shops").document(shop_id).update(shop);
                }
            }
        });

        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12) {
                                    timings_from = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " AM";
                                    btn_from.setText(timings_from);
                                    Map<String, Object> shop = new HashMap<>();
                                    shop.put("shop_timings_from", timings_from);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).update(shop);
                                } else if (hourOfDay > 12) {
                                    timings_from = String.format("%02d", hourOfDay % 12) + ":" + String.format("%02d", minute) + " PM";
                                    btn_from.setText(timings_from);
                                    Map<String, Object> shop = new HashMap<>();
                                    shop.put("shop_timings_from", timings_from);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).update(shop);
                                } else {
                                    timings_from = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " PM";
                                    btn_from.setText(timings_from);
                                    Map<String, Object> shop = new HashMap<>();
                                    shop.put("shop_timings_from", timings_from);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).update(shop);
                                }
                            }
                        }, 0, 0, false);
                timePickerDialog.show();

            }
        });

        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (hourOfDay < 12) {
                                    timings_to = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " AM";
                                    btn_to.setText(timings_to);
                                    Map<String, Object> shop = new HashMap<>();
                                    shop.put("shop_timings_to", timings_to);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).update(shop);
                                } else if (hourOfDay > 12) {
                                    timings_to = String.format("%02d", hourOfDay % 12) + ":" + String.format("%02d", minute) + " PM";
                                    btn_to.setText(timings_to);
                                    Map<String, Object> shop = new HashMap<>();
                                    shop.put("shop_timings_to", timings_to);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).update(shop);
                                } else {
                                    timings_to = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + " PM";
                                    btn_to.setText(timings_to);
                                    Map<String, Object> shop = new HashMap<>();
                                    shop.put("shop_timings_to", timings_to);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).update(shop);
                                }
                            }
                        }, 0, 0, false);
                timePickerDialog.show();

            }
        });

        btn_shop_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String, Object> shop = new HashMap<>();
                if (isChecked) {
                    tv_status.setText("Online");
                    shop.put("shop_status", true);
                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("shops").document(shop_id).update(shop);
                } else {
                    tv_status.setText("Offline");
                    shop.put("shop_status", false);
                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("shops").document(shop_id).update(shop);
                }
            }
        });
    }

    private void initialize() {

        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                .collection("shops").document(shop_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> shop = task.getResult().getData();
                        UniversalImageLoader.setImage(image_url, img_shop);
                        tv_shop_location.setText(shop.get("shop_address").toString() + ", " + shop.get("shop_address2").toString());
                        tv_shop_name.setText(shop.get("shop_name").toString());
                        tv_gstin.setText(shop.get("shop_gstin").toString());
                        tv_specialization.setText(shop.get("shop_specialization").toString());
                        btn_from.setText(shop.get("shop_timings_from").toString());
                        btn_to.setText(shop.get("shop_timings_to").toString());
//                        tv_shop_timings.setText(shop.get("shop_timings").toString());

                        btn_quick_delivery.setChecked(Boolean.parseBoolean(shop.get("quick_delivery").toString()));

                        if (Boolean.parseBoolean(shop.get("shop_status").toString())) {
                            btn_shop_status.setChecked(true);
                        } else {
                            btn_shop_status.setChecked(false);
                        }


                    }
                });

    }
}
