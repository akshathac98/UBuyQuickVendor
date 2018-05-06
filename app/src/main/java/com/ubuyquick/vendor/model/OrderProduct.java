package com.ubuyquick.vendor.model;

public class OrderProduct {

    private String product_name;
    private int product_quantity;
    private double product_mrp;
    private String product_image_url;
    private boolean available;

    public OrderProduct(String product_name, int product_quantity, double product_mrp, String product_image_url, boolean available) {
        this.product_name = product_name;
        this.product_quantity = product_quantity;
        this.product_mrp = product_mrp;
        this.product_image_url = product_image_url;
        this.available = available;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public int getProductQuantity() {
        return product_quantity;
    }

    public void setProductQuantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public double getProductMrp() {
        return product_mrp;
    }

    public void setProductMrp(double product_mrp) {
        this.product_mrp = product_mrp;
    }

    public String getProductImageUrl() {
        return product_image_url;
    }

    public void setProductImageUrl(String product_image_url) {
        this.product_image_url = product_image_url;
    }

    public boolean isAvailable() {
        return available;
    }
}
