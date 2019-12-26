package com.routesme.taxi_screen.Model;

public class ItemType {

    // String child;

    private String itemName;
    private boolean isHeader, isNormalItem;
    private int officeId;


    public ItemType() {
    }


    public ItemType(String itemName, boolean isHeader, boolean isNormalItem, int officeId) {
        this.itemName = itemName;
        this.isHeader = isHeader;
        this.isNormalItem = isNormalItem;
        this.officeId = officeId;
    }

    public ItemType(String itemName, boolean isHeader, boolean isNormalItem) {
        this.itemName = itemName;
        this.isHeader = isHeader;
        this.isNormalItem = isNormalItem;
    }



    public String getItemName() {
        return itemName;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean isNormalItem() {
        return isNormalItem;
    }

    public int getOfficeId() {
        return officeId;
    }


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public void setNormalItem(boolean normalItem) {
        isNormalItem = normalItem;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }
}

