package com.frshlypos.model;

import com.frshlypos.model.hqurl.CokeDetails;
import com.frshlypos.model.hqurl.RestaurantDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class FormattedServerItemModel implements Serializable{

    @SerializedName("itemId")
    @Expose
    private Integer itemId;

    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("mrp")
    @Expose
    private Integer mrp;
    @SerializedName("sgst_percent")
    @Expose
    private String sgstPercent;
    @SerializedName("cgst_percent")
    @Expose
    private String cgstPercent;
    @SerializedName("master_id")
    @Expose
    private Integer masterId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("item_tag")
    @Expose
    private String itemTag;
    @SerializedName("veg")
    @Expose
    private Boolean veg;
    @SerializedName("service_tax_percent")
    @Expose
    private String serviceTaxPercent;
    @SerializedName("abatement_percent")
    @Expose
    private String abatementPercent;
    @SerializedName("vat_percent")
    @Expose
    private String vatPercent;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("side_order")
    @Expose
    private String sideOrder;
    @SerializedName("restaurant_details")
    @Expose
    private RestaurantDetails restaurantDetails;
    @SerializedName("coke_details")
    @Expose
    private CokeDetails cokeDetails;
    @SerializedName("heating_reqd")
    @Expose
    private Boolean heatingReqd;
    @SerializedName("heating_reduction")
    @Expose
    private String heatingReduction;
    @SerializedName("condiment_slot")
    @Expose
    private Integer condimentSlot;
    @SerializedName("vending")
    @Expose
    private String vending;
    @SerializedName("subitem_id")
    @Expose
    private String Subitem_id;
    @SerializedName("stock_quantity")
    @Expose
    private Integer stockQuantity;

    public Integer getMrp() {
        return mrp;
    }

    public void setMrp(Integer mrp) {
        this.mrp = mrp;
    }

    public String getSgstPercent() {
        return sgstPercent;
    }

    public void setSgstPercent(String sgstPercent) {
        this.sgstPercent = sgstPercent;
    }

    public String getCgstPercent() {
        return cgstPercent;
    }

    public void setCgstPercent(String cgstPercent) {
        this.cgstPercent = cgstPercent;
    }

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public Boolean getVeg() {
        return veg;
    }

    public void setVeg(Boolean veg) {
        this.veg = veg;
    }

    public String getServiceTaxPercent() {
        return serviceTaxPercent;
    }

    public void setServiceTaxPercent(String serviceTaxPercent) {
        this.serviceTaxPercent = serviceTaxPercent;
    }

    public String getAbatementPercent() {
        return abatementPercent;
    }

    public void setAbatementPercent(String abatementPercent) {
        this.abatementPercent = abatementPercent;
    }

    public String getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(String vatPercent) {
        this.vatPercent = vatPercent;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSideOrder() {
        return sideOrder;
    }

    public void setSideOrder(String sideOrder) {
        this.sideOrder = sideOrder;
    }

    public RestaurantDetails getRestaurantDetails() {
        return restaurantDetails;
    }

    public void setRestaurantDetails(RestaurantDetails restaurantDetails) {
        this.restaurantDetails = restaurantDetails;
    }

    public CokeDetails getCokeDetails() {
        return cokeDetails;
    }

    public void setCokeDetails(CokeDetails cokeDetails) {
        this.cokeDetails = cokeDetails;
    }

    public Boolean getHeatingReqd() {
        return heatingReqd;
    }

    public void setHeatingReqd(Boolean heatingReqd) {
        this.heatingReqd = heatingReqd;
    }

    public String getHeatingReduction() {
        return heatingReduction;
    }

    public void setHeatingReduction(String heatingReduction) {
        this.heatingReduction = heatingReduction;
    }

    public Integer getCondimentSlot() {
        return condimentSlot;
    }

    public void setCondimentSlot(Integer condimentSlot) {
        this.condimentSlot = condimentSlot;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getVending() {
        return vending;
    }

    public void setVending(String vending) {
        this.vending = vending;
    }

    public String getSubitem_id() {
        return Subitem_id;
    }

    public void setSubitem_id(String subitem_id) {
        this.Subitem_id = subitem_id;
    }
}


