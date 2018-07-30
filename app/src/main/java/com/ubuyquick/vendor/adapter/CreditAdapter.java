package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.Manifest;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.AcceptedOrder;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.model.CreditNote;
import com.ubuyquick.vendor.orders.AcceptedOrderActivity;
import com.ubuyquick.vendor.utils.MultiChoiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback,
        Filterable{

    private static final String TAG = "CreditAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<Credit> credits;
    private List<Credit> creditsFiltered;

    private String shop_id;

    public CreditAdapter(Context context, String shop_id) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.shop_id = shop_id;
        credits = new ArrayList<>();
        creditsFiltered = new ArrayList<>();
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
        notifyDataSetChanged();
    }

    class ViewHolder extends MultiChoiceHelper.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_mobile;
        private TextView tv_credit;

        private ImageButton btn_send;
        private ImageButton btn_edit;
        private ImageButton btn_plus;
        private ImageButton btn_minus;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_mobile = (TextView) itemView.findViewById(R.id.tv_number);
            this.tv_credit = (TextView) itemView.findViewById(R.id.tv_credit);
            btn_send = (ImageButton) itemView.findViewById(R.id.btn_send);
            btn_edit = (ImageButton) itemView.findViewById(R.id.btn_edit);
            this.btn_minus = (ImageButton) itemView.findViewById(R.id.btn_minus);
            this.btn_plus = (ImageButton) itemView.findViewById(R.id.btn_plus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public void bind(final Credit credit) {
            this.tv_customer_name.setText(credit.getCustomerName());
            this.tv_mobile.setText(credit.getCustomerMobile());
            this.tv_credit.setText("\u20B9" + credit.getCredit());

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Send message from your phone?")
                            .setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                            ((Activity) context).requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, 1);
                                        } else {
                                            final SmsManager smsManager = SmsManager.getDefault();
                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                    .collection("shops").document(shop_id).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Map<String, Object> vendor = task.getResult().getData();
                                                    smsManager.sendTextMessage("+91" + credits.get(getAdapterPosition()).getCustomerMobile(), null, vendor.get("credit_message").toString(), null, null);
                                                }
                                            });
                                        }
                                    }

                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });


            btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_credit, null, false);
                    final EditText balance = (EditText) viewInflated.findViewById(R.id.et_balance);
                    final TextView tv_balance = (TextView) viewInflated.findViewById(R.id.tv_balance);

                    final Credit credit = credits.get(getAdapterPosition());
                    tv_balance.setText(credit.getCredit() + " + ");
                    balance.requestFocus();

                    builder.setTitle("Enter amount to add:");
                    builder.setView(viewInflated)
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(balance.getText().toString()))
                                        Toast.makeText(context, "Number can't be empty", Toast.LENGTH_SHORT).show();
                                    else {
                                        Map<String, Object> creditInfo = new HashMap<>();
                                        creditInfo.put("balance", Double.parseDouble(balance.getText().toString()) + credit.getCredit());

                                        Credit credit1 = credits.get(getAdapterPosition());
                                        credit1.setCredit(Double.parseDouble(balance.getText().toString()) + credit.getCredit());
                                        notifyItemChanged(getAdapterPosition());

                                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                                                .document(shop_id).collection("credits").document(credits.get(getAdapterPosition()).getCustomerId())
                                                .update(creditInfo);
                                        Toast.makeText(context, "Saved credit holder info.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    builder.show();
                }
            });

            btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_credit, null, false);
                    final EditText balance = (EditText) viewInflated.findViewById(R.id.et_balance);
                    final TextView tv_balance = (TextView) viewInflated.findViewById(R.id.tv_balance);

                    final Credit credit = credits.get(getAdapterPosition());
                    tv_balance.setText(credit.getCredit() + " - ");
                    balance.requestFocus();

                    builder.setTitle("Enter amount to subtract:");
                    builder.setView(viewInflated)
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(balance.getText().toString()))
                                        Toast.makeText(context, "Number can't be empty", Toast.LENGTH_SHORT).show();
                                    else {
                                        Map<String, Object> creditInfo = new HashMap<>();
                                        creditInfo.put("balance", credit.getCredit() - Double.parseDouble(balance.getText().toString()));

                                        Credit credit1 = credits.get(getAdapterPosition());
                                        credit1.setCredit(credit.getCredit() - Double.parseDouble(balance.getText().toString()));
                                        notifyItemChanged(getAdapterPosition());

                                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                                                .document(shop_id).collection("credits").document(credits.get(getAdapterPosition()).getCustomerId())
                                                .update(creditInfo);
                                        Toast.makeText(context, "Saved credit holder info.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    builder.show();
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete Credit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document(shop_id).collection("credits")
                                            .document(credits.get(getAdapterPosition()).getCustomerMobile()).delete();
                                    credits.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
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

            tv_credit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_credit, null, false);
                    final TextInputEditText balance = (TextInputEditText) viewInflated.findViewById(R.id.et_balance);

                    Credit credit1 = credits.get(getAdapterPosition());
                    balance.setText(credit1.getCredit() + "");

                    builder.setTitle("Credit Balance:");
                    builder.setView(viewInflated)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(balance.getText().toString()))
                                        Toast.makeText(context, "Amount can't be empty", Toast.LENGTH_SHORT).show();
                                    else {
                                        Map<String, Object> creditInfo = new HashMap<>();
                                        creditInfo.put("balance", Double.parseDouble(balance.getText().toString()));

                                        Credit credit2 = credits.get(getAdapterPosition());
                                        credit2.setCredit(Double.parseDouble(balance.getText().toString()));
                                        notifyItemChanged(getAdapterPosition());

                                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                                                .document(shop_id).collection("credits").document(credits.get(getAdapterPosition()).getCustomerId())
                                                .set(creditInfo);
                                        Toast.makeText(context, "Saved credit balance", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    builder.show();
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    creditsFiltered = credits;
                } else {
                    List<Credit> filteredCredits = new ArrayList<>();
                    for (Credit credit : credits) {
                        if (credit.getCustomerName().toLowerCase().contains(charString.toLowerCase()) || credit.getCustomerMobile().contains(constraint)) {
                            filteredCredits.add(credit);
                        }
                    }

                    creditsFiltered = filteredCredits;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = creditsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                creditsFiltered = (ArrayList<Credit>) results.values;
                notifyDataSetChanged();
            }
        };
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
