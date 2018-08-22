package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.AcceptedOrder;
import com.ubuyquick.vendor.model.NewOrder;
import com.ubuyquick.vendor.orders.AcceptedOrderActivity;
import com.ubuyquick.vendor.orders.NewOrderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcceptedOrderAdapter extends RecyclerView.Adapter<AcceptedOrderAdapter.ViewHolder> {

    private static final String TAG = "AcceptedOrderAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<AcceptedOrder> acceptedOrders;
    private String shop_id, vendor_id;

    public AcceptedOrderAdapter(Context context, String shop_id, String vendor_id) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.shop_id = shop_id;
        this.vendor_id = vendor_id;
        acceptedOrders = new ArrayList<>();
    }

    public void setAcceptedOrders(List<AcceptedOrder> acceptedOrders) {
        this.acceptedOrders = acceptedOrders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer;
        private TextView tv_address;
        private TextView tv_order_id;
        private TextView tv_ordered_at;
        private TextView tv_count;
        private TextView tv_date;
        private Button btn_deliver;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer = (TextView) itemView.findViewById(R.id.tv_customer);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_date = (TextView) itemView.findViewById(R.id.tv_order_date);
            this.tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            this.tv_ordered_at = (TextView) itemView.findViewById(R.id.tv_ordered_at);
            this.tv_count = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            this.btn_deliver = (Button) itemView.findViewById(R.id.btn_deliver);

            btn_deliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String location = acceptedOrders.get(getAdapterPosition()).getLatitude() +
                            "," + acceptedOrders.get(getAdapterPosition()).getLongitude();
                    Uri mapsUri = Uri.parse("google.navigation:q=" + location);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapsUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        AcceptedOrder clickedAcceptedOrder = acceptedOrders.get(getAdapterPosition());
                        Intent i = new Intent(v.getContext(), AcceptedOrderActivity.class);
                        i.putExtra("ORDER_ID", clickedAcceptedOrder.getOrderId());
                        i.putExtra("shop_id", shop_id);
                        i.putExtra("vendor_id", vendor_id);
                        v.getContext().startActivity(i);
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }
            });
        }

        public void bind(final AcceptedOrder acceptedOrder) {
            this.tv_customer.setText(acceptedOrder.getCustomerName());
            this.tv_address.setText(acceptedOrder.getAddress());
            this.tv_order_id.setText("Order ID: " + acceptedOrder.getOrderId());
            this.tv_ordered_at.setText(acceptedOrder.getOrderedAt().substring(11, 16));
            this.tv_count.setText(acceptedOrder.getCount() + "");
            this.tv_date.setText(acceptedOrder.getOrderedAt().substring(0, 11));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_acceptedorder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.acceptedOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return acceptedOrders.size();
    }
}
