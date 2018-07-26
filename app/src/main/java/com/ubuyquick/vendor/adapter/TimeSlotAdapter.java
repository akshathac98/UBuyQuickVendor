package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.HomeActivity;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.ShopActivity;
import com.ubuyquick.vendor.auth.LoginActivity;
import com.ubuyquick.vendor.model.Shop;
import com.ubuyquick.vendor.model.TimeSlot;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private static final String TAG = "TimeSlotAdapter";

    private Context context;
    private List<TimeSlot> timeSlots;
    private String shop_id;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public TimeSlotAdapter(Context context, String shop_id) {
        this.context = context;
        this.shop_id = shop_id;
        this.timeSlots = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_timings;
        private TextView tv_deliveries;
        private ImageButton btn_delete;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.tv_timings = (TextView) itemView.findViewById(R.id.tv_time);
            this.tv_deliveries = (TextView) itemView.findViewById(R.id.tv_delivery);
            this.btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);

            this.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete time slot?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection("vendors").document(mAuth.getCurrentUser().getPhoneNumber().substring(3))
                                    .collection("shops").document(shop_id).collection("time_slots").document(timeSlots.get(getAdapterPosition()).getId()).delete();
                                    timeSlots.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
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


        public void bind(TimeSlot timeSlot) {
            this.tv_deliveries.setText(timeSlot.getDeliveries() + "");
            this.tv_timings.setText(timeSlot.getTimings());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_slot, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.timeSlots.get(position));
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }
}
