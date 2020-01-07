package com.cbmwebdevelopment.utterfare.saved;

/**
 * Created by Connor Meehan on 1/7/18.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */

public class SavedItems {
    private String itemId, companyName, itemImage, itemName, itemShortDescription;

    public SavedItems(String itemId, String companyName, String itemImage, String itemName, String itemShortDescription){
        this.itemId = itemId;
        this.companyName = companyName;
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemShortDescription = itemShortDescription;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String val) {
        this.itemId = val;
    }
    public String getCompanyName(){
        return companyName;
    }

    public String setCompanyName(String val){
        return this.companyName = val;
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

    public String getItemShortDescription(){ return itemShortDescription;}

    public void setItemShortDescription(String val){ this.itemShortDescription = val;}
}
