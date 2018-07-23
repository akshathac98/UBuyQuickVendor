package com.ubuyquick.vendor.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.HomeActivity;
import com.ubuyquick.vendor.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private String terms = "New to UBuyQuick? Click here to <a><font color='#03A9F4'>Sign Up</font></a>";

    private TextView tv_terms;
    private TextInputEditText et_mobile_number;
    private Button btn_login, btn_signup;
    private RadioGroup login_group;
    private RadioButton mode_vendor, mode_manager, mode_delivery;

    private CharSequence[] login_modes = {"Vendor", "Manager", "Delivery Agent"};
    private int LOGIN_MODE = 0;
    private CharSequence login_mode = "Vendor";
    private AlertDialog alertDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        initializeViews();
        initializeListeners();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            Log.d(TAG, "initialize: user: " + mUser.getPhoneNumber());
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    private void initializeViews() {
        tv_terms = (TextView) findViewById(R.id.tv_terms);
        tv_terms.setText(Html.fromHtml(terms));
        et_mobile_number = (TextInputEditText) findViewById(R.id.et_mobile_number);
        btn_login = (Button) findViewById(R.id.btn_login);
        login_group = (RadioGroup) findViewById(R.id.radioGroup);
        mode_vendor = (RadioButton) findViewById(R.id.radioButton);
        mode_manager = (RadioButton) findViewById(R.id.radioButton2);
        mode_delivery = (RadioButton) findViewById(R.id.radioButton3);
    }

    private void initializeListeners() {

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        mode_vendor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    login_mode = login_modes[0];
                    LOGIN_MODE = 0;
                }

            }
        });

        mode_manager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    login_mode = login_modes[1];
                    LOGIN_MODE = 1;
                }
            }
        });

        mode_delivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    login_mode = login_modes[2];
                    LOGIN_MODE = 2;
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile_number = et_mobile_number.getText().toString();
                if (mobile_number.length() == 10) {
                    Intent i = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                    i.putExtra("VERIFICATION_TYPE", "LOGIN");
                    i.putExtra(Intent.EXTRA_PHONE_NUMBER, mobile_number);
                    i.putExtra("LOGIN_MODE", LOGIN_MODE);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
/*
        btn_login_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Login As: ");
                builder.setSingleChoiceItems(login_modes, LOGIN_MODE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                login_mode = login_modes[0];
                                LOGIN_MODE = 0;
                                btn_login_mode.setText("LOGIN AS: VENDOR (CLICK TO CHANGE)");
                                break;
                            case 1:
                                login_mode = login_modes[1];
                                LOGIN_MODE = 1;
                                btn_login_mode.setText("LOGIN AS: MANAGER (CLICK TO CHANGE)");
                                break;
                            case 2:
                                login_mode = login_modes[2];
                                LOGIN_MODE = 2;
                                btn_login_mode.setText("LOGIN AS: DELIVERY AGENT (CLICK TO CHANGE)");
                                break;
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();

            }
        });*/
    }
}
