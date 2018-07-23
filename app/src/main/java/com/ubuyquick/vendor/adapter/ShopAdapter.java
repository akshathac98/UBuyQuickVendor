package com.ubuyquick.vendor.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.R;
import com.ubuyquick.vendor.ShopActivity;
import com.ubuyquick.vendor.model.Shop;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private static final String TAG = "ShopAdapter";

    private Context context;
    private List<Shop> shops;

    public ShopAdapter(Context context) {
        this.context = context;
        this.shops = new ArrayList<>();
        ImageLoader.getInstance().init(new UniversalImageLoader(context).getConfig());
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_shop_name;
        private TextView tv_shop_status;
        private TextView tv_quickdelivery;
        private com.makeramen.roundedimageview.RoundedImageView img_shop;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.tv_shop_name = (TextView) itemView.findViewById(R.id.tv_shop_name);
            this.tv_shop_status = (TextView) itemView.findViewById(R.id.tv_status);
            this.tv_quickdelivery = (TextView) itemView.findViewById(R.id.tv_quickdelivery);
            this.img_shop = (com.makeramen.roundedimageview.RoundedImageView) itemView.findViewById(R.id.img_shop);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        Shop clickedShop = shops.get(getAdapterPosition());
                        Intent i = new Intent(v.getContext(), ShopActivity.class);
                        i.putExtra("shop_id", clickedShop.getShopId());
                        i.putExtra("shop_name", clickedShop.getShopName());
                        i.putExtra("vendor_id", clickedShop.getVendorId());
                        i.putExtra("image_url", clickedShop.getImageUrl());

                        v.getContext().startActivity(i);
                        ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                }
            });

        }


        public void bind(Shop shop) {

            if (!shop.getImageUrl().equals("NA")) {
                UniversalImageLoader.setImage(shop.getImageUrl(), img_shop);

            }
            if (shop.getShopStatus())
                this.tv_shop_status.setText("Shop Status: Online");
            else
                this.tv_shop_status.setText("Shop Status: Offline");

            this.tv_shop_name.setText(shop.getShopName());

            if (!shop.isQuickDelivery())
                tv_quickdelivery.setText("Quick Delivery: Off");
            else
                tv_quickdelivery.setText("Quick Delivery: On");

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(this.context).inflate(R.layout.card_shop, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(this.shops.get(position));
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}
