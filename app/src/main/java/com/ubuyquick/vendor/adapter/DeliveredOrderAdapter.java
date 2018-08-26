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
import com.ubuyquick.vendor.model.CancelledOrder;
import com.ubuyquick.vendor.model.DeliveredOrder;
import com.ubuyquick.vendor.orders.CancelledOrderActivity;
import com.ubuyquick.vendor.orders.DeliveredOrderActivity;

import java.util.ArrayList;
import java.util.List;

public class DeliveredOrderAdapter extends RecyclerView.Adapter<DeliveredOrderAdapter.ViewHolder> {

    private static final String TAG = "DeliveredOrderAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Context context;
    private List<DeliveredOrder> deliveredOrders;
    private String shop_id, vendor_id;

    public DeliveredOrderAdapter(Context context, String shop_id, String vendor_id) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.shop_id = shop_id;
        this.vendor_id = vendor_id;
        deliveredOrders = new ArrayList<>();
    }

    public void setDeliveredOrders(List<DeliveredOrder> deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer;
        private TextView tv_address;
        private TextView tv_order_id;
        private TextView tv_ordered_at;
        private TextView tv_ordered_date;
        private TextView tv_product_quantity;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer = (TextView) itemView.findViewById(R.id.tv_customer);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            this.tv_product_quantity = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_ordered_at = (TextView) itemView.findViewById(R.id.tv_ordered_at);
            this.tv_ordered_date = (TextView) itemView.findViewById(R.id.tv_order_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        DeliveredOrder clickedDeliveredOrder = deliveredOrders.get(getAdapterPosition());
                        Intent i = new Intent(v.getContext(), DeliveredOrderActivity.class);
                        i.putExtra("ORDER_ID", clickedDeliveredOrder.getOrderId());
                        i.putExtra("shop_id", shop_id);
                        i.putExtra("vendor_id", vendor_id);
                        v.getContext().startActivity(i);
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }
            });
        }

        public void bind(final DeliveredOrder deliveredOrder) {
            this.tv_customer.setText(deliveredOrder.getCustomerName());
            this.tv_address.setText(deliveredOrder.getAddress());
            this.tv_order_id.setText("Order ID: " + deliveredOrder.getOrderId());
            this.tv_ordered_at.setText(deliveredOrder.getOrderedAt().substring(11, 16));
            this.tv_product_quantity.setText(deliveredOrder.getCount() + "");
            this.tv_ordered_date.setText(deliveredOrder.getOrderedAt().substring(0, 11));
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
        holder.bind(this.deliveredOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return deliveredOrders.size();
    }
}
