package com.ubuyquick.vendor.model;

public class TimeSlot {

    private String timings;
    private int deliveries;
    private String id;

    public TimeSlot(String timings, int deliveries, String id) {
        this.timings = timings;
        this.deliveries = deliveries;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public int getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(int deliveries) {
        this.deliveries = deliveries;
    }
}
