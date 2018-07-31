package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.AcceptedOrder;
import com.ubuyquick.vendor.model.CancelledOrder;
import com.ubuyquick.vendor.orders.AcceptedOrderActivity;
import com.ubuyquick.vendor.orders.CancelledOrderActivity;

import java.util.ArrayList;
import java.util.List;

public class CancelledOrderAdapter extends RecyclerView.Adapter<CancelledOrderAdapter.ViewHolder> {

    private static final String TAG = "CancelledOrderAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<CancelledOrder> cancelledOrders;
    private String shop_id, vendor_id;

    public CancelledOrderAdapter(Context context, String shop_id, String vendor_id) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.vendor_id = vendor_id;
        this.shop_id = shop_id;
        cancelledOrders = new ArrayList<>();
    }

    public void setCancelledOrders(List<CancelledOrder> cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer;
        private TextView tv_address;
        private TextView tv_order_id;
        private TextView tv_ordered_at;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer = (TextView) itemView.findViewById(R.id.tv_customer);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            this.tv_ordered_at = (TextView) itemView.findViewById(R.id.tv_ordered_at);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        CancelledOrder clickedCancelledOrder = cancelledOrders.get(getAdapterPosition());
                        Intent i = new Intent(v.getContext(), CancelledOrderActivity.class);
                        i.putExtra("ORDER_ID", clickedCancelledOrder.getOrderId());
                        i.putExtra("shop_id", shop_id);
                        i.putExtra("vendor_id", vendor_id);
                        v.getContext().startActivity(i);
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }
            });
        }

        public void bind(final CancelledOrder cancelledOrder) {
            this.tv_customer.setText(cancelledOrder.getCustomerName());
            this.tv_address.setText(cancelledOrder.getAddress());
            this.tv_order_id.setText("Order ID: " + cancelledOrder.getOrderId());
            this.tv_ordered_at.setText(cancelledOrder.getOrderedAt());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_cancelledorder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.cancelledOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return cancelledOrders.size();
    }
}
