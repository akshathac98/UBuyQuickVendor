package com.ubuyquick.vendor.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.Credit;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.UniversalImageLoader;
import com.ubuyquick.vendor.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {

    private static final String TAG = "OrderProductAdapter";

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<OrderProduct> orderProducts;
    private String order_type, order_id, shop_id;
    private Utils.OnItemClick mCallback;
    private Utils.OnChange mCallback2;
    private int products_available = 0;

    public OrderProductAdapter(Context context, String order_id, String shop_id, String order_type, Utils.OnItemClick listener, Utils.OnChange changeListener) {
        this.context = context;
        this.order_id = order_id;
        this.shop_id = shop_id;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.mCallback = listener;
        this.mCallback2 = changeListener;
        this.order_type = order_type;
        orderProducts = new ArrayList<>();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
        notifyDataSetChanged();
    }

    public void selectAll(boolean select) {
        if (select) {
            products_available = 0;
            for (OrderProduct orderProduct : orderProducts) {
                if (!orderProduct.isAvailable())
                    orderProduct.setAvailable(true);
            }
            notifyDataSetChanged();
        } else {
            for (OrderProduct orderProduct : orderProducts) {
                if (orderProduct.isAvailable())
                    orderProduct.setAvailable(false);
                products_available--;
            }
            notifyDataSetChanged();
        }
    }

    public void deselectAll() {
        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setAvailable(false);
            products_available = 0;
            mCallback.onClick(products_available);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private TextView tv_product_quantity;
        private TextView tv_product_mrp;
        private CheckBox cb_product;
        private ImageButton btn_edit;

        public ViewHolder(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.cb_product = (CheckBox) itemView.findViewById(R.id.cb_product);
            this.btn_edit = (ImageButton) itemView.findViewById(R.id.btn_edit);

            this.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_product, null, false);
                    final TextInputEditText mrp = (TextInputEditText) viewInflated.findViewById(R.id.et_mrp);

                    final OrderProduct orderProduct = orderProducts.get(getAdapterPosition());
                    mrp.setText(orderProduct.getProductMrp() + "");

                    builder.setTitle("Edit Product MRP:");
                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mrp.getText().toString().isEmpty()) {
                                Toast.makeText(context, "MRP can't be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                mCallback2.onChange(Double.parseDouble(mrp.getText().toString()) - orderProduct.getProductMrp());
                                orderProduct.setProductMrp(Double.parseDouble(mrp.getText().toString()));
                                notifyDataSetChanged();
                                Map<String, Object> productInfo = new HashMap<>();
                                productInfo.put("mrp", Double.parseDouble(mrp.getText().toString()));
                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document(shop_id)
                                        .collection("new_orders").document(order_id).collection("products")
                                        .document(orderProduct.getProductId()).update(productInfo);
                            }
                        }
                    });
                    builder.show();

                }
            });

            if (order_type.equals("ACCEPTED") || order_type.equals("CANCELLED") || order_type.equals("DELIVERED")) {
                cb_product.setVisibility(View.GONE);
                btn_edit.setVisibility(View.INVISIBLE);
            } else {
                cb_product.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            products_available++;
                            mCallback.onClick(products_available);
                            orderProducts.get(getAdapterPosition()).setAvailable(true);
                        } else {
                            products_available--;
                            mCallback.onClick(products_available);
                            orderProducts.get(getAdapterPosition()).setAvailable(false);
                        }
                    }
                });
            }
/*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        OrderProduct clickedProduct = orderProducts.get(getAdapterPosition());
                        Toast.makeText(v.getContext(), "Clicked " + clickedProduct.getProductName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
        }

        public void bind(OrderProduct orderProduct) {
            if (orderProduct.isAvailable()) {
                this.cb_product.setChecked(true);
            } else {
                this.cb_product.setChecked(false);
            }
            this.tv_product_name.setText(orderProduct.getProductName());
            this.tv_product_mrp.setText("\u20B9" + orderProduct.getProductMrp());
            this.tv_product_quantity.setText(orderProduct.getProductQuantity() + "\nKGS");
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
