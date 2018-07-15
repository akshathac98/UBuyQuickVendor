package com.ubuyquick.vendor.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.AddProduct;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProductAdapter extends RecyclerView.Adapter<AddProductAdapter.ViewHolder> {

    private static final String TAG = "AddProductAdapter";

    private Context context;
    private List<AddProduct> addProducts;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String shop_id;
    private String category;
    private String sub_category;

    private int existing_quantity = 0;

    private Map<String, Object> product;

    public AddProductAdapter(Context context, String shop_id, String category, String sub_category) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        addProducts = new ArrayList<>();
        this.shop_id = shop_id;
        this.category = category;
        this.sub_category = sub_category;
    }

    public void setAddProducts(List<AddProduct> addProducts) {
        this.addProducts = addProducts;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;
        private EditText tv_product_quantity;
        private TextView tv_product_mrp;
        private ImageButton btn_plus;
        private ImageButton btn_minus;
        private Button btn_add;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_product_name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.tv_product_quantity = (EditText) itemView.findViewById(R.id.tv_product_quantity);
            this.tv_product_mrp = (TextView) itemView.findViewById(R.id.tv_product_mrp);
//            this.btn_minus = (ImageButton) itemView.findViewById(R.id.btn_minus);
//            this.btn_plus = (ImageButton) itemView.findViewById(R.id.btn_plus);
            this.btn_add = (Button) itemView.findViewById(R.id.btn_add);
        }

        public void bind(final AddProduct addProduct) {
            this.tv_product_name.setText(addProduct.getProductName());
            this.tv_product_mrp.setText("MRP: " + addProduct.getProductMrp());

            this.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product = new HashMap<>();
                    product.put("product_name", addProduct.getProductName());
                    product.put("product_mrp", addProduct.getProductMrp());
                    product.put("image_url", addProduct.getImageUrl());
                    product.put("product_id", addProduct.getProductId());

                    DocumentReference docRef = db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                            .collection("shops").document(shop_id).collection("product_categories")
                            .document(category).collection(sub_category).document(addProduct.getProductId());

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                existing_quantity = Integer.parseInt(task.getResult().getData().get("product_quantity").toString());
                                    try {
                                        product.put("product_quantity", Integer.parseInt(tv_product_quantity.getText().toString()) + existing_quantity);
                                    } catch (NumberFormatException e) {
                                        product.put("product_quantity", existing_quantity);
                                        Toast.makeText(context, "Cannot add empty values", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    try {
                                        product.put("product_quantity", 0);
                                    } catch (NumberFormatException e) {
                                        Toast.makeText(context, "Cannot add empty values", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("shops").document(shop_id).collection("product_categories")
                                        .document(category).collection(sub_category).document(addProduct.getProductId()).set(product)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context, "Added " + tv_product_quantity.getText().toString() + " units of " + addProduct.getProductName() + " to your inventory.", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                product.put("category", category);
                                product.put("sub_category", sub_category);

                                db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                        .collection("shops").document(shop_id).collection("inventory").document(addProduct.getProductId())
                                        .set(product);
                            }
                        }
                    });



                }
            });
/*
            this.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        AddProduct clickedProduct = addProducts.get(getAdapterPosition());
                        int quantity = clickedProduct.getProductQuantity();
                        tv_product_quantity.setText(quantity + 1 + "");
                        clickedProduct.setProductQuantity(quantity + 1);
                    }
                }
            });

            this.btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        AddProduct clickedProduct = addProducts.get(getAdapterPosition());
                        if (Integer.parseInt(tv_product_quantity.getText().toString()) != 0) {
                            int quantity = clickedProduct.getProductQuantity();
                            tv_product_quantity.setText(quantity - 1 + "");
                            clickedProduct.setProductQuantity(quantity - 1);
                        }
                    }
                }
            });*/
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_add_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.addProducts.get(position));
    }

    @Override
    public int getItemCount() {
        return addProducts.size();
    }
}
