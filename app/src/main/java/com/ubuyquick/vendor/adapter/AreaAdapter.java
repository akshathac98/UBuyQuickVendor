package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.ShopActivity;
import com.ubuyquick.vendor.model.Shop;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {

    private static final String TAG = "AreaAdapter";

    private Context context;
    private List<String> areas;

    public AreaAdapter(Context context) {
        this.context = context;
        this.areas = new ArrayList<>();
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_area;
        private CheckBox cb_area;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.tv_area = (TextView) itemView.findViewById(R.id.tv_area);
            this.cb_area = (CheckBox) itemView.findViewById(R.id.cb_area);

            cb_area.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {

                    } else {

                    }
                }
            });
        }


        public void bind(String area) {
            this.tv_area.setText(area);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_area, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.areas.get(position));
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }
}
