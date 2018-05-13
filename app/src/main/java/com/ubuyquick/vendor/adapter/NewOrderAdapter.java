package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.model.NewOrder;
import com.ubuyquick.vendor.model.OrderProduct;
import com.ubuyquick.vendor.orders.NewOrderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.ViewHolder> {

    private static final String TAG = "NewOrderAdapter";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference newOrderRef;
    private CollectionReference cancelledOrderRef;

    private Context context;
    private List<NewOrder> newOrders;

    private NewOrder clickedOrder;
    Map<String, Object> cancelledOrder;

    public NewOrderAdapter(Context context) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        newOrderRef = db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("new_orders");
        cancelledOrderRef = db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("cancelled_orders");
        this.context = context;
        newOrders = new ArrayList<>();
    }

    public void setNewOrders(List<NewOrder> newOrders) {
        this.newOrders = newOrders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customer;
        private TextView tv_address;
        private TextView tv_order_id;
        private TextView tv_ordered_at;
        private Button btn_cancel;
        private Button btn_accept;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tv_customer = (TextView) itemView.findViewById(R.id.tv_customer);
            this.tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            this.tv_order_id = (TextView) itemView.findViewById(R.id.tv_order_id);
            this.tv_ordered_at = (TextView) itemView.findViewById(R.id.tv_ordered_at);
            this.btn_cancel = (Button) itemView.findViewById(R.id.btn_cancel);
            this.btn_accept = (Button) itemView.findViewById(R.id.btn_accept);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        NewOrder clickedNewOrder = newOrders.get(getAdapterPosition());
                        Intent i = new Intent(v.getContext(), NewOrderActivity.class);
                        i.putExtra("ORDER_ID", clickedNewOrder.getOrderId());
                        v.getContext().startActivity(i);
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }
            });
        }

        public void bind(final NewOrder newOrder) {
            this.tv_customer.setText(newOrder.getCustomerName());
            this.tv_address.setText(newOrder.getAddress());
            this.tv_order_id.setText("Order ID: " + newOrder.getOrderId());
            this.tv_ordered_at.setText(newOrder.getOrderedAt());

//            this.btn_cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
//                    builder.setMessage("Cancel order?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    clickedOrder = newOrders.get(getAdapterPosition());
//                                    cancelledOrder = new HashMap<>();
//                                    cancelledOrder.put("customer_id", clickedOrder.getCustomerId());
//                                    cancelledOrder.put("customer_name", clickedOrder.getCustomerName());
//                                    cancelledOrder.put("delivery_address", clickedOrder.getAddress());
//                                    cancelledOrder.put("order_id", clickedOrder.getOrderId());
//                                    cancelledOrder.put("ordered_at", clickedOrder.getOrderedAt());
//
//                                    Log.d(TAG, "onClick: clickedOrder: " + clickedOrder.getOrderId());
//                                    newOrderRef.document(clickedOrder.getOrderId()).delete()
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        newOrders.remove(getAdapterPosition());
//                                                        notifyItemRemoved(getAdapterPosition());
//                                                    } else {
//                                                        Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//
//                                    cancelledOrderRef.document(clickedOrder.getOrderId()).set(cancelledOrder)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    Toast.makeText(context, "Cancelled order", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                }
//                            })
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            }).show();
//                }
//            });

            this.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Cancel order?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final NewOrder clickedNewOrder = newOrders.get(getAdapterPosition());
                                    Map<String, Object> cancelledOrder = new HashMap<>();
                                    cancelledOrder.put("customer_name", clickedNewOrder.getCustomerName());
                                    cancelledOrder.put("order_id", clickedNewOrder.getOrderId());
                                    cancelledOrder.put("ordered_at", clickedNewOrder.getOrderedAt());
                                    cancelledOrder.put("customer_id", clickedNewOrder.getCustomerId());
                                    cancelledOrder.put("delivery_address", clickedNewOrder.getAddress());

                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document("BHYRAVA_PROVISIONS")
                                            .collection("new_orders").document(clickedNewOrder.getOrderId()).collection("products")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                                                        for (DocumentSnapshot document : documents) {
                                                            db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                    .collection("shops").document("BHYRAVA_PROVISIONS")
                                                                    .collection("cancelled_orders").document(clickedNewOrder.getOrderId())
                                                                    .collection("products").document(document.getId()).set(document.getData());
                                                        }
                                                    } else {
                                                        Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document("BHYRAVA_PROVISIONS")
                                            .collection("cancelled_orders")
                                            .document(clickedNewOrder.getOrderId())
                                            .set(cancelledOrder)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: " + clickedNewOrder.getOrderId());
                                                        db.collection("vendors").document(mAuth.getCurrentUser()
                                                                .getPhoneNumber().substring(3))
                                                                .collection("shops").document("BHYRAVA_PROVISIONS").collection("new_orders")
                                                                .document(clickedNewOrder.getOrderId()).delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(((Activity) context), "Cancelled order", Toast.LENGTH_SHORT).show();
                                                                            newOrders.remove(getAdapterPosition());
                                                                            notifyItemRemoved(getAdapterPosition());
                                                                        } else {
                                                                            Toast.makeText(((Activity) context), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(((Activity) context), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                    }
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

            this.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Accept order?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final NewOrder clickedNewOrder = newOrders.get(getAdapterPosition());
                                    Map<String, Object> acceptedOrder = new HashMap<>();
                                    acceptedOrder.put("customer_name", clickedNewOrder.getCustomerName());
                                    acceptedOrder.put("order_id", clickedNewOrder.getOrderId());
                                    acceptedOrder.put("ordered_at", clickedNewOrder.getOrderedAt());
                                    acceptedOrder.put("customer_id", clickedNewOrder.getCustomerId());
                                    acceptedOrder.put("delivery_address", clickedNewOrder.getAddress());


                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3)).collection("shops").document("BHYRAVA_PROVISIONS")
                                            .collection("new_orders").document(clickedNewOrder.getOrderId()).collection("products")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            CollectionReference collectionReference =
                                                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                                                            .collection("shops").document("BHYRAVA_PROVISIONS")
                                                                            .collection("accepted_orders").document(clickedNewOrder.getOrderId()).collection("products");
                                                            Map<String, Object> product = document.getData();
                                                            collectionReference.add(product);
                                                        }
                                                    }
                                                }
                                            });

                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                            .collection("shops").document("BHYRAVA_PROVISIONS")
                                            .collection("accepted_orders")
                                            .document(clickedNewOrder.getOrderId())
                                            .set(acceptedOrder)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "onComplete: remove id: " + clickedNewOrder.getOrderId());
                                                    db.collection("vendors").document(mAuth.getCurrentUser()
                                                            .getPhoneNumber().substring(3)).collection("shops")
                                                            .document("BHYRAVA_PROVISIONS").collection("new_orders")
                                                            .document(clickedNewOrder.getOrderId()).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(context, "Accepted order.", Toast.LENGTH_SHORT).show();
                                                                    newOrders.remove(getAdapterPosition());
                                                                    notifyItemRemoved(getAdapterPosition());
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.card_neworder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.newOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return newOrders.size();
    }
}
