package com.ubuyquick.vendor.shop;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
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
    private EditText otp;

    private String mobile_number, mVerificationId, verification_type;

    private Button btn_delivery_agent;
    private Button btn_manager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, container, false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                token = forceResendingToken;
                Toast.makeText(getContext(), "OTP sent to phone: " + input.getText().toString(), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter OTP from Agent's phone:");
                otp = new EditText(getContext());
                otp.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(otp);
                builder.setNegativeButton("Cancel", null);

                builder.setPositiveButton("Verify Agent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString());
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> agent = new HashMap<>();
                                            agent.put("agent_id", mobile_number);
                                            agent.put("user_role", "DELIVERY");
                                            agent.put("shop_id", "BHYRAVA_PROVISIONS");

                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                    .collection("shops").document("BHYRAVA_PROVISIONS")
                                                    .collection("delivery_agents").document(mobile_number)
                                                    .set(agent);

                                            Toast.makeText(getContext(), "Agent added successfully", Toast.LENGTH_SHORT).show();
                                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(getContext(), "Invalid OTP. Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });
                builder.show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }
        };

        initializeViews();
        initialize();

        return view;
    }

    private void initializeViews() {
        img_shop = (ImageView) view.findViewById(R.id.img_shop);


        btn_delivery_agent = (Button) view.findViewById(R.id.btn_delivery_agent);
        btn_manager = (Button) view.findViewById(R.id.btn_manager);
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
                        mobile_number = input.getText().toString();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mobile_number, 60,
                                TimeUnit.SECONDS, getActivity(), mCallbacks);
                    }
                });
                builder.show();

            }
        });
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ImageLoader.getInstance().init(new UniversalImageLoader(getContext()).getConfig());
        UniversalImageLoader.setImage("https://firebasestorage.googleapis.com/v0/b/ubuyquick-d4121.appspot.com/o/9008003968%2FBhyrava%20Provisions%2Fshop_image.jpg?alt=media&token=8592f84e-42c3-4e6a-a522-54bddc907ec6", img_shop);
    }
}
