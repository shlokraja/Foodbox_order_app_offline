package com.frshlypos.model.hqurl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Akshay.Panchal on 18-Jul-17.
 */
public class CokeDetails implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("st")
    @Expose
    private String st;
    @SerializedName("abt")
    @Expose
    private String abt;
    @SerializedName("vat")
    @Expose
    private String vat;
    @SerializedName("discount_percent")
    @Expose
    private String discountPercent;
    @SerializedName("restaurant_details")
    @Expose
    private RestaurantDetailsCoke restaurantDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getAbt() {
        return abt;
    }

    public void setAbt(String abt) {
        this.abt = abt;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public RestaurantDetailsCoke getRestaurantDetails() {
        return restaurantDetails;
    }

    public void setRestaurantDetails(RestaurantDetailsCoke restaurantDetails) {
        this.restaurantDetails = restaurantDetails;
    }

}
