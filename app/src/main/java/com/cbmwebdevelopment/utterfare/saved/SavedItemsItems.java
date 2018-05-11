package com.cbmwebdevelopment.utterfare.saved;

/**
 * Created by Connor Meehan on 1/7/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class SavedItemsItems {
    private String itemId, dataTable, companyName, companyId, itemImage, itemName;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String val) {
        this.itemId = val;
    }

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String val) {
        this.dataTable = val;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String setCompanyName(String val){
        return this.companyName = val;
    }

    public String getCompanyId(){
        return companyId;
    }

    public void setCompanyId(String val){
        this.companyId = val;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String val) {
        this.itemImage = val;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String val) {
        this.itemName = val;
    }
}
