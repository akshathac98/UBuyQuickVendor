package com.ubuyquick.vendor.model;

public class CreditNote {

    private String customer_id;
    private String customer_name;
    private String customer_mobile;
    private boolean cleared;
    private String order_id;
    private double credit;
    private String id;

    public CreditNote(String id, String customer_id, String customer_name, String customer_mobile, boolean cleared, String order_id, double credit) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_mobile = customer_mobile;
        this.cleared = cleared;
        this.order_id = order_id;
        this.id = id;
        this.credit = credit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomerMobile() {
        return customer_mobile;
    }

    public void setCustomerMobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
}
