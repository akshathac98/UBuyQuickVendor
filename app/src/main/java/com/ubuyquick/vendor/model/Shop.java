package com.ubuyquick.vendor.model;

public class Shop {

    private String image_url;
    private String shop_name;
    private String shop_status;
    private String shop_id;

    public Shop(String image_url, String shop_name, String shop_status, String shop_id) {
        this.image_url = image_url;
        this.shop_name = shop_name;
        this.shop_status = shop_status;
        this.shop_id = shop_id;
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

    public String getShopStatus() {
        return shop_status;
    }

    public void setShopStatus(String shop_status) {
        this.shop_status = shop_status;
    }

    public String getShopId() {
        return shop_id;
    }

    public void setShopId(String shop_id) {
        this.shop_id = shop_id;
    }
}
