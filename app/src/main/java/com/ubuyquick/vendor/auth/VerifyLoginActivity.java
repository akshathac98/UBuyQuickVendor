package com.ubuyquick.vendor.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ubuyquick.vendor.HomeActivity;
import com.ubuyquick.vendor.R;

import java.util.concurrent.TimeUnit;

public class VerifyLoginActivity extends AppCompatActivity {

    private static final String TAG = "VerifyLoginActivity";

    private String mobile_number, mVerificationId;

    private TextView tv_code;
    private Button btn_verify;
    private PinView pinView;

    private FirebaseAuth mAuth;

    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_login);

        mAuth = FirebaseAuth.getInstance();

        mobile_number = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        tv_code = (TextView) findViewById(R.id.tv_code);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        pinView = (PinView) findViewById(R.id.pinView);

        tv_code.setText(getString(R.string.verification_code_sent) + mobile_number);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, pinView.getText().toString());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: sign in success");
                                    Intent i = new Intent(VerifyLoginActivity.this, HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Log.d(TAG, "onComplete: failure: " + task.getException());
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(VerifyLoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                                Toast.makeText(VerifyLoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                token = forceResendingToken;
                Toast.makeText(VerifyLoginActivity.this, "OTP verification code sent", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCodeSent: " + s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential.getSmsCode());
                pinView.setText(phoneAuthCredential.getSmsCode());
                Toast.makeText(VerifyLoginActivity.this, "Verified successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
                Toast.makeText(VerifyLoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobile_number,
                60, TimeUnit.SECONDS, VerifyLoginActivity.this, mCallbacks);
    }
}
