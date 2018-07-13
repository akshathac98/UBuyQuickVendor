package com.ubuyquick.vendor.model;

public class Shop {

    private String image_url;
    private String shop_name;
    private boolean quick_delivery;
    private boolean shop_status;
    private String shop_id;
    private String vendor_id;

    public Shop(String image_url, String shop_name, boolean shop_status, String shop_id, String vendor_id, boolean quick_delivery) {
        this.image_url = image_url;
        this.shop_name = shop_name;
        this.shop_status = shop_status;
        this.shop_id = shop_id;
        this.vendor_id = vendor_id;
        this.quick_delivery = quick_delivery;
    }

    public boolean isQuickDelivery() {
        return quick_delivery;
    }

    public void setQuickDelivery(boolean quick_delivery) {
        this.quick_delivery = quick_delivery;
    }

    public String getVendorId() {
        return vendor_id;
    }

    public void setVendorId(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public String getShopName() {
        return shop_name;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public boolean getShopStatus() {
        return shop_status;
    }

    public void setShopStatus(boolean shop_status) {
        this.shop_status = shop_status;
    }

    public String getShopId() {
        return shop_id;
    }

    public void setShopId(String shop_id) {
        this.shop_id = shop_id;
    }
}
