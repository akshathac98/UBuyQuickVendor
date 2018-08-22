package com.ubuyquick.vendor.model;

public class MainSearchProduct {

    private String product_name;
    private double product_mrp;
    private String product_measure;
    private int quantity;

    public MainSearchProduct(String product_name, double product_mrp, String product_measure, int quantity) {
        this.product_name = product_name;
        this.product_mrp = product_mrp;
        this.product_measure = product_measure;
        this.quantity = quantity;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public double getProductMrp() {
        return product_mrp;
    }

    public void setProductMrp(double product_mrp) {
        this.product_mrp = product_mrp;
    }

    public String getProductMeasure() {
        return product_measure;
    }

    public void setProductMeasure(String product_measure) {
        this.product_measure = product_measure;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
