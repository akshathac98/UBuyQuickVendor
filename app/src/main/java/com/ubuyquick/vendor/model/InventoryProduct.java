package com.ubuyquick.vendor.model;

public class InventoryProduct {

    private String product_name;
    private int product_quantity;
    private double product_mrp;
    private String product_id;
    private String product_category;
    private String product_subcategory;
    private String product_image_url;
    private boolean available;

    public InventoryProduct(String product_name, int product_quantity, double product_mrp, String product_image_url, boolean available, String product_id, String product_category, String product_subcategory) {
        this.product_name = product_name;
        this.product_quantity = product_quantity;
        this.product_mrp = product_mrp;
        this.product_image_url = product_image_url;
        this.available = available;
        this.product_category = product_category;
        this.product_subcategory = product_subcategory;
        this.product_id = product_id;
    }

    public String getProductCategory() {
        return product_category;
    }

    public void setProductCategory(String product_category) {
        this.product_category = product_category;
    }

    public String getProductSubcategory() {
        return product_subcategory;
    }

    public void setProductSubcategory(String product_subcategory) {
        this.product_subcategory = product_subcategory;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProductId(String product_id) {
        this.product_id = product_id;
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
