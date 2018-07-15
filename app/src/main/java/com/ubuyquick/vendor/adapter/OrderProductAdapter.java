package com.ubuyquick.vendor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {

    private static final String TAG = "OrderProductAdapter";

    private Context context;
    private List<OrderProduct> orderProducts;
    private String order_type;

    public OrderProductAdapter(Context context, String order_type) {
        this.context = context;
        this.order_type = order_type;
        orderProducts = new ArrayList<>();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private TextView tv_product_quantity;
        private TextView tv_product_mrp;
        private CheckBox cb_product;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.cb_product = (CheckBox) itemView.findViewById(R.id.cb_product);

            if (order_type.equals("ACCEPTED")) {
                cb_product.setVisibility(View.INVISIBLE);
            } else {
                cb_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            orderProducts.get(getAdapterPosition()).setAvailable(true);
                        } else {
                            orderProducts.get(getAdapterPosition()).setAvailable(false);
                        }
                    }
                });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        OrderProduct clickedProduct = orderProducts.get(getAdapterPosition());
                        Toast.makeText(v.getContext(), "Clicked " + clickedProduct.getProductName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void bind(OrderProduct orderProduct) {
            this.tv_product_name.setText(orderProduct.getProductName());
            this.tv_product_mrp.setText("MRP\n" + orderProduct.getProductMrp());
            this.tv_product_quantity.setText("QTY\n" + orderProduct.getProductQuantity());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.orderProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return orderProducts.size();
    }
}
