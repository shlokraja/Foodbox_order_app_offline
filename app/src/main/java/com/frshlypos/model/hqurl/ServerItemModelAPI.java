package com.frshlypos.model.hqurl;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ServerItemModelAPI implements Serializable{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("item_tag")
    @Expose
    private String itemTag;
    @SerializedName("veg")
    @Expose
    private Boolean veg;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("side_order")
    @Expose
    private String sideOrder;
    @SerializedName("master_id")
    @Expose
    private Integer masterId;
    @SerializedName("mrp")
    @Expose
    private Integer mrp;
    @SerializedName("cgst_percent")
    @Expose
    private String cgstPercent;
    @SerializedName("sgst_percent")
    @Expose
    private String sgstPercent;
    @SerializedName("service_tax_percent")
    @Expose
    private String serviceTaxPercent;
    @SerializedName("vat_percent")
    @Expose
    private String vatPercent;
    @SerializedName("heating_required")
    @Expose
    private Boolean heatingRequired;
    @SerializedName("heating_reduction")
    @Expose
    private String heatingReduction;
    @SerializedName("condiment_slot")
    @Expose
    private Integer condimentSlot;
    @SerializedName("abatement_percent")
    @Expose
    private String abatementPercent;
    @SerializedName("r_id")
    @Expose
    private String rId;
    @SerializedName("r_name")
    @Expose
    private String rName;
    @SerializedName("r_address")
    @Expose
    private String rAddress;
    @SerializedName("r_tin_no")
    @Expose
    private String rTinNo;
    @SerializedName("r_st_no")
    @Expose
    private String rStNo;
    @SerializedName("r_pan_no")
    @Expose
    private String rPanNo;
    @SerializedName("r_entity")
    @Expose
    private String rEntity;
    @SerializedName("r_cgst_percent")
    @Expose
    private String rCgstPercent;
    @SerializedName("r_sgst_percent")
    @Expose
    private String rSgstPercent;
    @SerializedName("r_sender_email")
    @Expose
    private String rSenderEmail;
    @SerializedName("discount_percent")
    @Expose
    private String discountPercent;
    @SerializedName("b_r_id")
    @Expose
    private String bRId;
    @SerializedName("b_r_name")
    @Expose
    private String bRName;
    @SerializedName("b_r_address")
    @Expose
    private String  bRAddress;
    @SerializedName("b_r_tin_no")
    @Expose
    private String bRTinNo;
    @SerializedName("b_id")
    @Expose
    private String bId;
    @SerializedName("b_name")
    @Expose
    private String bName;
    @SerializedName("b_mrp")
    @Expose
    private String bMrp;
    @SerializedName("b_service_tax_percent")
    @Expose
    private String bServiceTaxPercent;
    @SerializedName("b_abatement_percent")
    @Expose
    private String bAbatementPercent;
    @SerializedName("b_vat_percent")
    @Expose
    private String bVatPercent;
    @SerializedName("vending")
    @Expose
    private String vending;
    @SerializedName("subitem_id")
    @Expose
    private String Subitem_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    public Integer getMrp() {
        return mrp;
    }

    public void setMrp(Integer mrp) {
        this.mrp = mrp;
    }

    public String  getCgstPercent() {
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

    public String getServiceTaxPercent() {
        return serviceTaxPercent;
    }

    public void setServiceTaxPercent(String serviceTaxPercent) {
        this.serviceTaxPercent = serviceTaxPercent;
    }

    public String getVatPercent() {
        return vatPercent;
    }

    public void setVatPercent(String vatPercent) {
        this.vatPercent = vatPercent;
    }

    public Boolean getHeatingRequired() {
        return heatingRequired;
    }

    public void setHeatingRequired(Boolean heatingRequired) {
        this.heatingRequired = heatingRequired;
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

    public String getAbatementPercent() {
        return abatementPercent;
    }

    public void setAbatementPercent(String abatementPercent) {
        this.abatementPercent = abatementPercent;
    }

    public String getRId() {
        return rId;
    }

    public void setRId(String rId) {
        this.rId = rId;
    }

    public String getRName() {
        return rName;
    }

    public void setRName(String rName) {
        this.rName = rName;
    }

    public String getRAddress() {
        return rAddress;
    }

    public void setRAddress(String rAddress) {
        this.rAddress = rAddress;
    }

    public String getRTinNo() {
        return rTinNo;
    }

    public void setRTinNo(String rTinNo) {
        this.rTinNo = rTinNo;
    }

    public String getRStNo() {
        return rStNo;
    }

    public void setRStNo(String rStNo) {
        this.rStNo = rStNo;
    }

    public String getRPanNo() {
        return rPanNo;
    }

    public void setRPanNo(String rPanNo) {
        this.rPanNo = rPanNo;
    }

    public String getREntity() {
        return rEntity;
    }

    public void setREntity(String rEntity) {
        this.rEntity = rEntity;
    }

    public String getRCgstPercent() {
        return rCgstPercent;
    }

    public void setRCgstPercent(String rCgstPercent) {
        this.rCgstPercent = rCgstPercent;
    }

    public String getRSgstPercent() {
        return rSgstPercent;
    }

    public void setRSgstPercent(String rSgstPercent) {
        this.rSgstPercent = rSgstPercent;
    }

    public String getRSenderEmail() {
        return rSenderEmail;
    }

    public void setRSenderEmail(String rSenderEmail) {
        this.rSenderEmail = rSenderEmail;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getBRId() {
        return bRId;
    }

    public void setBRId(String bRId) {
        this.bRId = bRId;
    }

    public String getBRName() {
        return bRName;
    }

    public void setBRName(String bRName) {
        this.bRName = bRName;
    }

    public String getBRAddress() {
        return bRAddress;
    }

    public void setBRAddress(String bRAddress) {
        this.bRAddress = bRAddress;
    }

    public String getBRTinNo() {
        return bRTinNo;
    }

    public void setBRTinNo(String bRTinNo) {
        this.bRTinNo = bRTinNo;
    }

    public String getBId() {
        return bId;
    }

    public void setBId(String bId) {
        this.bId = bId;
    }

    public String getBName() {
        return bName;
    }

    public void setBName(String bName) {
        this.bName = bName;
    }

    public String getBMrp() {
        return bMrp;
    }

    public void setBMrp(String bMrp) {
        this.bMrp = bMrp;
    }

    public String getBServiceTaxPercent() {
        return bServiceTaxPercent;
    }

    public void setBServiceTaxPercent(String bServiceTaxPercent) {
        this.bServiceTaxPercent = bServiceTaxPercent;
    }

    public String getBAbatementPercent() {
        return bAbatementPercent;
    }

    public void setBAbatementPercent(String bAbatementPercent) {
        this.bAbatementPercent = bAbatementPercent;
    }

    public String getBVatPercent() {
        return bVatPercent;
    }

    public void setBVatPercent(String bVatPercent) {
        this.bVatPercent = bVatPercent;
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