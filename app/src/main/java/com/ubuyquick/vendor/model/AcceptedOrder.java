package com.ubuyquick.vendor.model;

public class AcceptedOrder {

    private String order_id;
    private String customer_name;
    private String customer_id;
    private String address;
    private String ordered_at;
    private int count;
    private String latitude;
    private String longitude;

    public AcceptedOrder(String order_id, String customer_name, String customer_id, String address, String ordered_at,
                         String latitude, String longitude, int count) {
        this.order_id = order_id;
        this.customer_name = customer_name;
        this.count = count;
        this.latitude = latitude;
        this.longitude = longitude;
        this.customer_id = customer_id;
        this.address = address;
        this.ordered_at = ordered_at;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderedAt() {
        return ordered_at;
    }

    public void setOrderedAt(String ordered_at) {
        this.ordered_at = ordered_at;
    }
}
