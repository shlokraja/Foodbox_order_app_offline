package com.frshlypos.model;

import java.io.Serializable;

/**
 * Created by Akshay.Panchal on 19-Jul-17.
 */

public class StockItemModel implements Serializable{
    FormattedServerItemModel formattedServerItemModel;
    int itemCode;
    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public FormattedServerItemModel getFormattedServerItemModel() {
        return formattedServerItemModel;
    }

    public void setFormattedServerItemModel(FormattedServerItemModel formattedServerItemModel) {
        this.formattedServerItemModel = formattedServerItemModel;
    }
}
