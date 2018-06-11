package com.frshlypos.model.hqurl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Akshay.Panchal on 18-Jul-17.
 */
public class RestaurantDetails implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("st_no")
    @Expose
    private String stNo;
    @SerializedName("pan_no")
    @Expose
    private String panNo;
    @SerializedName("cgst_percent")
    @Expose
    private String cgstPercent;
    @SerializedName("sgst_percent")
    @Expose
    private String sgstPercent;
    @SerializedName("entity")
    @Expose
    private String entity;
    @SerializedName("tin_no")
    @Expose
    private String tinNo;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStNo() {
        return stNo;
    }

    public void setStNo(String stNo) {
        this.stNo = stNo;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getCgstPercent() {
        return cgstPercent;
    }

    public void setCgstPercent(String cgstPercent) {
        this.cgstPercent = cgstPercent;
    }

    public String getSgstPercent() {
        return sgstPercent;
    }

    public void setSgstPercent(String sgstPercent) {
        this.sgstPercent = sgstPercent;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getTinNo() {
        return tinNo;
    }

    public void setTinNo(String tinNo) {
        this.tinNo = tinNo;
    }

}
