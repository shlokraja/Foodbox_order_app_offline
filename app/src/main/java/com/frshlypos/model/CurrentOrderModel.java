package com.frshlypos.model;

import java.io.Serializable;

/**
 * Created by Akshay.Panchal on 20-Jul-17.
 */

public class CurrentOrderModel implements Serializable{
    int itemId;
    int quantity;
    int coke_quantity;
    String location;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCoke_quantity() {
        return coke_quantity;
    }

    public void setCoke_quantity(int coke_quantity) {
        this.coke_quantity = coke_quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
