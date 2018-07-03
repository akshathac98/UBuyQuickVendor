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

    private String terms = "By logging in, you agree to the <a><u><font color='black'>Terms &amp; Conditions</font></u></a> of " +
            "using this account and to the <a><u><font color='black'>Privacy Policy</font></u></a> of <a><u><font color='#03A9F4'>UBuyQuick.com</font></u></a>";

    private TextView tv_terms;
    private TextInputEditText et_mobile_number;
    private Button btn_login, btn_signup, btn_login_mode;

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
        btn_signup = (Button) findViewById(R.id.btn_signup);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login_mode = (Button) findViewById(R.id.btn_login_mode);

    }

    private void initializeListeners() {

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile_number = et_mobile_number.getText().toString();
                if (mobile_number.length() == 10) {
                    Intent i = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                    i.putExtra(Intent.EXTRA_PHONE_NUMBER, mobile_number);
                    i.putExtra("VERIFICATION_TYPE", "REGISTER");
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile_number = et_mobile_number.getText().toString();
                if (mobile_number.length() == 10) {

                    if (LOGIN_MODE == 1) {

                    }

                    Intent i = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                    i.putExtra("VERIFICATION_TYPE", "LOGIN");
                    i.putExtra(Intent.EXTRA_PHONE_NUMBER, mobile_number);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_login_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Login As: ");
                builder.setSingleChoiceItems(login_modes, LOGIN_MODE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
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
        });
    }
}
