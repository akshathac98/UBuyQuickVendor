package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.Order;
import com.ubuyquick.vendor.orders.NewOrderActivity;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private static final String TAG = "OrderAdapter";

    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context) {
        this.context = context;
        orders = new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer;
        private TextView tv_address;
        private TextView tv_order_id;
        private TextView tv_ordered_at;
        private ImageButton btn_cancel;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer = (TextView) itemView.findViewById(R.id.tv_customer);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            this.tv_ordered_at = (TextView) itemView.findViewById(R.id.tv_ordered_at);
            this.btn_cancel = (ImageButton) itemView.findViewById(R.id.btn_cancel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        Order clickedOrder = orders.get(getAdapterPosition());
                        Intent i = new Intent(v.getContext(), NewOrderActivity.class);
                        i.putExtra("ORDER_ID", clickedOrder.getOrderId());
                        v.getContext().startActivity(i);
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }
            });
        }

        public void bind(Order order) {
            this.tv_customer.setText(order.getCustomerName());
            this.tv_address.setText(order.getAddress());
            this.tv_order_id.setText("Order ID: " + order.getOrderId());
            this.tv_ordered_at.setText(order.getOrderedAt());

//            this.btn_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(), "Cancel order: " + orders.get(getAdapterPosition()).getOrderId(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
