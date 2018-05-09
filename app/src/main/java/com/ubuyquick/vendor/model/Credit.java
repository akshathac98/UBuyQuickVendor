package com.ubuyquick.vendor.model;

public class Credit {

    private String customer_id;
    private String customer_name;
    private String customer_mobile;
    private double credit;

    public Credit(String customer_id, String customer_name, String customer_mobile, double credit) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_mobile = customer_mobile;
        this.credit = credit;
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
