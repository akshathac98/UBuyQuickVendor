package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.Manifest;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.AcceptedOrder;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.orders.AcceptedOrderActivity;

import java.util.ArrayList;
import java.util.List;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "CreditAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<Credit> credits;

    public CreditAdapter(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        credits = new ArrayList<>();
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_mobile;
        private TextView tv_credit;

        private Button btn_send;
        private Button btn_edit;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_mobile = (TextView) itemView.findViewById(R.id.tv_number);
            this.tv_credit = (TextView) itemView.findViewById(R.id.tv_credit);
            btn_send = (Button) itemView.findViewById(R.id.btn_send);
            btn_edit = (Button) itemView.findViewById(R.id.btn_edit);

        }

        public void bind(Credit credit) {
            this.tv_customer_name.setText(credit.getCustomerName());
            this.tv_mobile.setText(credit.getCustomerMobile());
            this.tv_credit.setText("Credit balance: " + credit.getCredit() + "/-");

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Cancel order?")
                            .setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                            ((Activity) context).requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 1);
                                        } else {
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage("+919008003968", null, "Credit balance pending for Bhyrava provisions", null, null);
                                        }
                                    }

                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission required to send sms", Toast.LENGTH_SHORT).show();
                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+919008003968", null, "Credit balance pending for Bhyrava provisions", null, null);
                }
                break;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_credit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.credits.get(position));
    }

    @Override
    public int getItemCount() {
        return credits.size();
    }
}
