package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.utils.MultiChoiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditNoteAdapter extends RecyclerView.Adapter<CreditNoteAdapter.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback,
        Filterable{

    private static final String TAG = "CreditNoteAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<Credit> credits;
    private List<Credit> creditsFiltered;

    private String shop_id;

    public CreditNoteAdapter(Context context, String shop_id) {
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

        private Button btn_plus;
        private Button btn_minus;
        private Button btn_clear;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_mobile = (TextView) itemView.findViewById(R.id.tv_number);
            this.tv_credit = (TextView) itemView.findViewById(R.id.tv_credit);
            this.btn_clear = (Button) itemView.findViewById(R.id.btn_clear);
            this.btn_minus = (Button) itemView.findViewById(R.id.btn_minus);
            this.btn_plus = (Button) itemView.findViewById(R.id.btn_plus);

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

            btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_credit, null, false);
                    final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                    final TextInputEditText number = (TextInputEditText) viewInflated.findViewById(R.id.et_number);
                    final TextInputEditText balance = (TextInputEditText) viewInflated.findViewById(R.id.et_balance);

                    Credit credit1 = credits.get(getAdapterPosition());
                    name.setText(credit1.getCustomerName());
                    number.setText(credit1.getCustomerMobile());
                    balance.setText(credit1.getCredit() + "");

                    builder.setTitle("Enter credit holder info:");
                    builder.setView(viewInflated)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(number.getText().toString()) || TextUtils.isEmpty(balance.getText().toString()))
                                        Toast.makeText(context, "Please check your info", Toast.LENGTH_SHORT).show();
                                    else {
                                        Map<String, Object> creditInfo = new HashMap<>();
                                        creditInfo.put("name", name.getText().toString());
                                        creditInfo.put("number", number.getText().toString());
                                        creditInfo.put("balance", Double.parseDouble(balance.getText().toString()));

                                        Credit credit2 = credits.get(getAdapterPosition());
                                        credit2.setCustomerName(name.getText().toString());
                                        credit2.setCustomerMobile(number.getText().toString());
                                        credit2.setCredit(Double.parseDouble(balance.getText().toString()));
                                        notifyItemChanged(getAdapterPosition());

                                        db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops")
                                                .document(shop_id).collection("credits").document(credits.get(getAdapterPosition()).getCustomerId())
                                                .set(creditInfo);
                                        Toast.makeText(context, "Saved credit holder info.", Toast.LENGTH_SHORT).show();
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
