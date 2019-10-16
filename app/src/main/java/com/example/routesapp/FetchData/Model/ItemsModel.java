package com.example.routesapp.FetchData.Model;

public class ItemsModel {


    private int Item_ID, ItemList_Discount_Amount;
    private String Item_Type, ItemList_Title_En, ItemList_Title_Ar, ItemList_Title_Or, ItemList_Title_Ta, ItemList_Logo_URL, ItemList_Pic_URL, ItemList_Page_URL;
    private float ItemList_Rate;


    //Constructor...

    public ItemsModel() {
    }

    public ItemsModel(int item_ID, int itemList_Discount_Amount, String item_Type, String itemList_Title_En, String itemList_Title_Ar, String itemList_Title_Or, String itemList_Title_Ta, String itemList_Logo_URL, String itemList_Pic_URL, String itemList_Page_URL, float itemList_Rate) {
        Item_ID = item_ID;
        ItemList_Discount_Amount = itemList_Discount_Amount;
        Item_Type = item_Type;
        ItemList_Title_En = itemList_Title_En;
        ItemList_Title_Ar = itemList_Title_Ar;
        ItemList_Title_Or = itemList_Title_Or;
        ItemList_Title_Ta = itemList_Title_Ta;
        ItemList_Logo_URL = itemList_Logo_URL;
        ItemList_Pic_URL = itemList_Pic_URL;
        ItemList_Page_URL = itemList_Page_URL;
        ItemList_Rate = itemList_Rate;
    }





    //Getter...

    public int getItem_ID() {
        return Item_ID;
    }

    public int getItemList_Discount_Amount() {
        return ItemList_Discount_Amount;
    }

    public String getItem_Type() {
        return Item_Type;
    }

    public String getItemList_Title_En() {
        return "" + ItemList_Title_En;
    }

    public String getItemList_Title_Ar() {
        String Title_Ar = "" + ItemList_Title_Ar;

        if (!Title_Ar.trim().isEmpty() && !Title_Ar.trim().equals("null")){
            return Title_Ar;
        }else {
            return ItemList_Title_En;
        }
    }

    public String getItemList_Title_Or() {
        String Title_Or = "" + ItemList_Title_Or;

        if (!Title_Or.trim().isEmpty() && !Title_Or.trim().equals("null")){
            return Title_Or;
        }else {
            return ItemList_Title_En;
        }
    }

    public String getItemList_Title_Ta() {
        String Title_Ta = "" + ItemList_Title_Ta;

        if (!Title_Ta.trim().isEmpty() && !Title_Ta.trim().equals("null")){
            return Title_Ta;
        }else {
            return ItemList_Title_En;
        }
    }

    public String getItemList_Logo_URL() {
        return ItemList_Logo_URL;
    }

    public String getItemList_Pic_URL() {
        return ItemList_Pic_URL;
    }

    public String getItemList_Page_URL() {
        return ItemList_Page_URL;
    }

    public float getItemList_Rate() {
        return ItemList_Rate;
    }





    //Setter...

    public void setItem_ID(int item_ID) {
        Item_ID = item_ID;
    }

    public void setItemList_Discount_Amount(int itemList_Discount_Amount) {
        ItemList_Discount_Amount = itemList_Discount_Amount;
    }

    public void setItem_Type(String item_Type) {
        Item_Type = item_Type;
    }

    public void setItemList_Title_En(String itemList_Title_En) {
        ItemList_Title_En = itemList_Title_En;
    }

    public void setItemList_Title_Ar(String itemList_Title_Ar) {
        ItemList_Title_Ar = itemList_Title_Ar;
    }

    public void setItemList_Title_Or(String itemList_Title_Or) {
        ItemList_Title_Or = itemList_Title_Or;
    }

    public void setItemList_Title_Ta(String itemList_Title_Ta) {
        ItemList_Title_Ta = itemList_Title_Ta;
    }

    public void setItemList_Logo_URL(String itemList_Logo_URL) {
        ItemList_Logo_URL = itemList_Logo_URL;
    }

    public void setItemList_Pic_URL(String itemList_Pic_URL) {
        ItemList_Pic_URL = itemList_Pic_URL;
    }

    public void setItemList_Page_URL(String itemList_Page_URL) {
        ItemList_Page_URL = itemList_Page_URL;
    }

    public void setItemList_Rate(float itemList_Rate) {
        ItemList_Rate = itemList_Rate;
    }
}
