package com.frshlypos.model.stockdata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Akshay.Panchal on 19-Jul-17.
 */
public class ItemDetail {

    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("slot_ids")
    @Expose
    private List<Integer> slotIds = null;
    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;
    @SerializedName("expired")
    @Expose
    private Boolean expired;
    @SerializedName("spoiled")
    @Expose
    private Boolean spoiled;
    @SerializedName("isExpired_InsertedintoDb")
    @Expose
    private Boolean isExpiredInsertedintoDb;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Integer> getSlotIds() {
        return slotIds;
    }

    public void setSlotIds(List<Integer> slotIds) {
        this.slotIds = slotIds;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Boolean getSpoiled() {
        return spoiled;
    }

    public void setSpoiled(Boolean spoiled) {
        this.spoiled = spoiled;
    }

    public Boolean getIsExpiredInsertedintoDb() {
        return isExpiredInsertedintoDb;
    }

    public void setIsExpiredInsertedintoDb(Boolean isExpiredInsertedintoDb) {
        this.isExpiredInsertedintoDb = isExpiredInsertedintoDb;
    }

}
