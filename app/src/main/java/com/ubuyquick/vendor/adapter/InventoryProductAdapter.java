package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.HomeActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.auth.LoginActivity;
import com.ubuyquick.vendor.model.InventoryProduct;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InventoryProductAdapter extends RecyclerView.Adapter<InventoryProductAdapter.ViewHolder> {

    private static final String TAG = "OrderProductAdapter";

    private Context context;
    private List<InventoryProduct> inventoryProducts;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String shop_id, vendor_id;
    private String number;
    private int LOGIN_MODE = 0;

    public InventoryProductAdapter(Context context, String shop_id, String vendor_id) {
        this.context = context;
        inventoryProducts = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.shop_id = shop_id;
        this.vendor_id = vendor_id;

        SharedPreferences preferences = context.getSharedPreferences("LOGIN_MODE", Context.MODE_PRIVATE);
        LOGIN_MODE = preferences.getInt("LOGIN_MODE", 0);
        if (LOGIN_MODE == 1) {
            number = vendor_id;
        } else {
            number = mAuth.getCurrentUser().getPhoneNumber().substring(3);
        }
//        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setInventoryProducts(List<InventoryProduct> inventoryProducts) {
        this.inventoryProducts = inventoryProducts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_product;
        private TextView tv_product_name;
        private TextView tv_product_quantity;
        private TextView tv_product_mrp;
        private ImageButton btn_remove, btn_edit;
        private CheckBox cb_product;

        public ViewHolder(View itemView) {
            super(itemView);

            this.img_product = (ImageView) itemView.findViewById(R.id.img_product);
            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (TextView) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
            this.btn_remove = (ImageButton) itemView.findViewById(R.id.btn_remove);
            this.btn_edit = (ImageButton) itemView.findViewById(R.id.btn_edit);

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_product_info, null, false);
                    final TextInputEditText name = (TextInputEditText) viewInflated.findViewById(R.id.et_name);
                    final TextInputEditText price = (TextInputEditText) viewInflated.findViewById(R.id.et_price);
                    final TextInputEditText stock = (TextInputEditText) viewInflated.findViewById(R.id.et_stock);

                    final InventoryProduct inventoryProduct = inventoryProducts.get(getAdapterPosition());
                    name.setText(inventoryProduct.getProductName());
                    price.setText(inventoryProduct.getProductMrp() + "");
                    stock.setText(inventoryProduct.getProductQuantity() + "");

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(v.getContext());
                    builder.setTitle("Edit product info:");
                    builder.setView(viewInflated);
                    builder.setNegativeButton("Cancel", null);

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(price.getText().toString()) || TextUtils.isEmpty(stock.getText().toString()))
                                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                            else {
                                Map<String, Object> product = new HashMap<>();
                                product.put("product_name", name.getText().toString());
                                product.put("product_mrp", Double.parseDouble(price.getText().toString()));
                                product.put("product_quantity", Integer.parseInt(stock.getText().toString()));

                                db.collection("vendors").document(number)
                                        .collection("shops").document(shop_id).collection("product_categories")
                                        .document(inventoryProduct.getProductCategory()).collection(inventoryProduct.getProductSubcategory()).document(inventoryProduct.getProductId()).update(product)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context, "Update success.", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                db.collection("vendors").document(number)
                                        .collection("shops").document(shop_id).collection("inventory").document(inventoryProduct.getProductId())
                                        .update(product);

                            }
                        }
                    });
                    builder.show();
                }
            });

            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Remove item from inventory?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("vendors").document(number)
                                            .collection("shops").document(shop_id).collection("inventory")
                                            .document(inventoryProducts.get(getAdapterPosition()).getProductId())
                                            .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            db.collection("vendors").document(number)
                                                    .collection("shops").document(shop_id).collection("product_categories")
                                                    .document(inventoryProducts.get(getAdapterPosition()).getProductCategory())
                                                    .collection(inventoryProducts.get(getAdapterPosition()).getProductSubcategory())
                                                    .document(inventoryProducts.get(getAdapterPosition()).getProductId())
                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "Item removed from inventory.", Toast.LENGTH_SHORT).show();
                                                    inventoryProducts.remove(getAdapterPosition());
                                                    notifyItemRemoved(getAdapterPosition());
                                                }
                                            });
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            });
        }

        public void bind(InventoryProduct inventoryProduct) {
//            UniversalImageLoader.setImage(inventoryProduct.getProductImageUrl(), this.img_product);
            this.tv_product_name.setText(inventoryProduct.getProductName());
            this.tv_product_mrp.setText("\u20B9" + inventoryProduct.getProductMrp());
            this.tv_product_quantity.setText("In stock: " + inventoryProduct.getProductQuantity());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_inventory_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.inventoryProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return inventoryProducts.size();
    }
}
