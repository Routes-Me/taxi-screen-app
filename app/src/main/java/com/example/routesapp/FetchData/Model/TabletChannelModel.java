package com.example.routesapp.FetchData.Model;

public class TabletChannelModel {

    private int Channel_ID ;
    private String Channel_Language  ;


    //Constructor...

    public TabletChannelModel() {
    }

    public TabletChannelModel(int channel_ID, String channel_Language) {
        Channel_ID = channel_ID;
        Channel_Language = channel_Language;
    }


//Getter...

    public int getChannel_ID() {
        return Channel_ID;
    }

    public String getChannel_Language() {
        return Channel_Language;
    }


    //Setter...

    public void setChannel_ID(int channel_ID) {
        Channel_ID = channel_ID;
    }

    public void setChannel_Language(String channel_Language) {
        Channel_Language = channel_Language;
    }
}
