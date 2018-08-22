package com.ubuyquick.vendor.model;

import android.util.Log;

public class Category {

    private static final String TAG = "Category";

    private String category_id;
    private String category_name;
    private String category_image;
    private String sub_categories[];

    public Category(String category_id, String category_name, String category_image, String[] sub_categories) {
        this.category_id = category_id;
        this.sub_categories = sub_categories;
        this.category_name = category_name;
        this.category_image = category_image;
        for (int i = 0; i < sub_categories.length; i++) {
            Log.d(TAG, "Category: " + sub_categories[i]);
        }
    }

    public String[] getSubCategories() {
        return sub_categories;
    }

    public void setSubCategories(String[] sub_categories) {
        this.sub_categories = sub_categories;
    }

    public String getCategoryId() {
        return category_id;
    }

    public void setCategoryId(String category_id) {
        this.category_id = category_id;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public String getCategoryImage() {
        return category_image;
    }

    public void setCategoryImage(String category_image) {
        this.category_image = category_image;
    }
}
