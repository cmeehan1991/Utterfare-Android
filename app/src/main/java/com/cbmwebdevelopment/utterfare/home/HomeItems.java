package com.cbmwebdevelopment.utterfare.home;

/**
 * Created by Connor Meehan on 2020-01-04.
 * CBM Web Development
 * Connor.Meehan@cbmwebdevelopment.com
 */
public class HomeItems {

    public String itemId, itemName, itemImageUrl;

    public HomeItems(String itemId, String itemName, String itemImageUrl){
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
    }

    public void setItemId(String itemId){
        this.itemId = itemId;
    }

    public String getItemId(){
        return this.itemId;
    }

    public void setItemName(String itemName){
        this.itemName = itemName;
    }

    public String getItemName(){
        return this.itemName;
    }

    public void setItemImageUrl(String url){
        this.itemImageUrl = url;
    }

    public String getItemImageUrl(){
        return this.itemImageUrl;
    }
}
