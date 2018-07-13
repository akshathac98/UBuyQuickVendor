package com.ubuyquick.vendor.auth;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    private static Pattern pattern;
    private Matcher matcher;

    private Button btn_cancel, btn_signup;
    private TextInputLayout til_vendor_name, til_email, til_mobile_number, til_aadhar_number;
    private TextInputEditText et_vendor_name, et_email, et_mobile_number, et_aadhar_number;
    String vendor_name, email, mobile_number, aadhar_card_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_up);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        til_vendor_name = (TextInputLayout) findViewById(R.id.til_vendor_name);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_mobile_number = (TextInputLayout) findViewById(R.id.til_mobile_number);
        til_aadhar_number = (TextInputLayout) findViewById(R.id.til_aadhar_number);

        et_vendor_name = (TextInputEditText) findViewById(R.id.et_vendor_name);
        et_email = (TextInputEditText) findViewById(R.id.et_email);
        et_mobile_number = (TextInputEditText) findViewById(R.id.et_mobile_number);
        et_aadhar_number = (TextInputEditText) findViewById(R.id.et_aadhar_number);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_vendor_name.setErrorEnabled(false);
                til_mobile_number.setErrorEnabled(false);
                til_aadhar_number.setErrorEnabled(false);
                til_email.setErrorEnabled(false);

                if (validateForm()) {
                    Intent i = new Intent(SignupActivity.this, VerifyOTPActivity.class);
                    i.putExtra("name", vendor_name);
                    i.putExtra("phone", mobile_number);
                    i.putExtra("VERIFICATION_TYPE", "REGISTER");
                    i.putExtra("email", email);
                    i.putExtra("aadhar", aadhar_card_number);
                    startActivity(i);
                }

            }
        });

    }

    private boolean validateForm() {
        vendor_name = et_vendor_name.getText().toString();
        email = et_email.getText().toString();
        mobile_number = et_mobile_number.getText().toString();
        aadhar_card_number = et_aadhar_number.getText().toString();

        if (TextUtils.isEmpty(vendor_name)) {
            til_vendor_name.setError("Vendor name cannot empty.");
            et_vendor_name.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(mobile_number)) {
            til_mobile_number.setError("Mobile number required.");
            et_mobile_number.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            email = "NA";
        }  else if (TextUtils.isEmpty(aadhar_card_number)) {
            til_aadhar_number.setError("Aadhar number required.");
            et_aadhar_number.requestFocus();
            return false;
        }

        if (mobile_number.length() != 10) {
            til_mobile_number.setError("Mobile number should have 10 digits.");
            return false;
        }

        if (!email.equals("NA")) {
            pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                til_email.setError("Invalid email address format.");
                return false;
            }
        }

        if (aadhar_card_number.length() != 12) {
            til_aadhar_number.setError("Invalid Aadhar number. Please enter 12 digits.");
            return false;
        }

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
}
