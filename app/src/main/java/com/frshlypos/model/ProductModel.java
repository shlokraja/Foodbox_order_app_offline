package com.frshlypos.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.frshlypos.BR;

import java.io.Serializable;

/**
 * Created by Akshay.Panchal on 12-Jul-17.
 */

public class ProductModel extends BaseObservable implements Serializable {
    private int id;
    private String name;
    private int quantity = 1;
    private double price;
    private int masterId;
    private boolean veg;
    private String location;
    private double cgst_percent;
    private double sgst_percent;

    @Bindable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @Bindable
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public boolean isVeg() {
        return veg;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getCgst_percent() {
        return cgst_percent;
    }

    public void setCgst_percent(double cgst_percent) {
        this.cgst_percent = cgst_percent;
    }

    public double getSgst_percent() {
        return sgst_percent;
    }

    public void setSgst_percent(double sgst_percent) {
        this.sgst_percent = sgst_percent;
    }
}
