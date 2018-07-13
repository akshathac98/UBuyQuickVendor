package com.ubuyquick.vendor.model;

public class AddProduct {

    private String product_name;
    private String product_id;
    private String image_url;
    private String product_category;
    private String product_subcategory;
    private int product_quantity;
    private double product_mrp;

    public AddProduct(String product_name, double product_mrp, String product_id, String image_url, String product_category, String product_subcategory) {
        this.product_name = product_name;
        this.product_mrp = product_mrp;
        this.product_id = product_id;
        this.image_url = image_url;
        this.product_category = product_category;
        this.product_subcategory = product_subcategory;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
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
}
