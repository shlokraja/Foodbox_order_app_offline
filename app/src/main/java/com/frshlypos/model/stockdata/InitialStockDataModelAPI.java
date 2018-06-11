package com.frshlypos.model.stockdata;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class InitialStockDataModelAPI {

    @SerializedName("item_details")
    @Expose
    private List<ItemDetail> itemDetails = null;
    @SerializedName("locked_count")
    @Expose
    private Integer lockedCount;
    @SerializedName("mobile_locked_count")
    @Expose
    private Integer mobileLockedCount;

    public List<ItemDetail> getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(List<ItemDetail> itemDetails) {
        this.itemDetails = itemDetails;
    }

    public Integer getLockedCount() {
        return lockedCount;
    }

    public void setLockedCount(Integer lockedCount) {
        this.lockedCount = lockedCount;
    }

    public Integer getMobileLockedCount() {
        return mobileLockedCount;
    }

    public void setMobileLockedCount(Integer mobileLockedCount) {
        this.mobileLockedCount = mobileLockedCount;
    }

}





