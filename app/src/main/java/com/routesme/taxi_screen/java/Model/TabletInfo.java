package com.routesme.taxi_screen.java.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TabletInfo {

    @SerializedName("tabletRegesterPassword")
    @Expose
    private String tabletPassword;

    @SerializedName("tabletRegesterChannelID")
    @Expose
    private int tabletChannelId;



    //Constructor...

    public TabletInfo() {
    }

    public TabletInfo(String tabletPassword, int tabletChannelId) {
        this.tabletPassword = tabletPassword;
        this.tabletChannelId = tabletChannelId;
    }




    //Getter...

    public String getTabletPassword() {
        return tabletPassword;
    }

    public int getTabletChannelId() {
        return tabletChannelId;
    }




    //Setter...

    public void setTabletPassword(String tabletPassword) {
        this.tabletPassword = tabletPassword;
    }

    public void setTabletChannelId(int tabletChannelId) {
        this.tabletChannelId = tabletChannelId;
    }
}
