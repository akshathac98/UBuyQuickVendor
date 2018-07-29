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
import com.ubuyquick.vendor.HomeActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.auth.LoginActivity;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.model.CreditNote;
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
    private List<CreditNote> creditNotes;
    private List<CreditNote> creditNotesFiltered;

    private String shop_id;

    public CreditNoteAdapter(Context context, String shop_id) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.shop_id = shop_id;
        creditNotes = new ArrayList<>();
        creditNotesFiltered = new ArrayList<>();
    }

    public void setCreditNotes(List<CreditNote> creditNotes) {
        this.creditNotes = creditNotes;
        notifyDataSetChanged();
    }

    class ViewHolder extends MultiChoiceHelper.ViewHolder {
        private TextView tv_customer_name;
        private TextView tv_mobile;
        private TextView tv_credit;

        private ImageButton btn_clear;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_mobile = (TextView) itemView.findViewById(R.id.tv_number);
            this.tv_credit = (TextView) itemView.findViewById(R.id.tv_credit);
            this.btn_clear = (ImageButton) itemView.findViewById(R.id.btn_clear);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            this.btn_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete Credit Note?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Map<String, Object> info = new HashMap<>();
                                    info.put("cleared", true);
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).collection("credit_notes")
                                    .document(creditNotes.get(getAdapterPosition()).getId()).update(info);
                                    creditNotes.remove(getAdapterPosition());
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

        }

        public void bind(final CreditNote creditNote) {
            this.tv_customer_name.setText(creditNote.getCustomerName());
            this.tv_mobile.setText(creditNote.getCustomerMobile());
            this.tv_credit.setText("\u20B9" + creditNote.getCredit());

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    creditNotesFiltered = creditNotes;
                } else {
                    List<CreditNote> filteredCredits = new ArrayList<>();
                    for (CreditNote creditNote : creditNotes) {
                        if (creditNote.getCustomerName().toLowerCase().contains(charString.toLowerCase()) || creditNote.getCustomerMobile().contains(constraint)) {
                            filteredCredits.add(creditNote);
                        }
                    }

                    creditNotesFiltered = filteredCredits;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = creditNotesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                creditNotesFiltered = (ArrayList<CreditNote>) results.values;
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
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.creditNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return creditNotes.size();
    }
}
