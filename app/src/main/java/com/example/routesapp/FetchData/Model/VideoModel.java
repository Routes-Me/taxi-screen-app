package com.example.routesapp.FetchData.Model;

public class VideoModel {

    private int Video_ID;
    private String  Video_URL;


    //Constructor ...

    public VideoModel() {
    }

    public VideoModel(int video_ID, String video_URL) {
        Video_ID = video_ID;
        Video_URL = video_URL;
    }






//Getter

    public int getVideo_ID() {
        return Video_ID;
    }

    public String getVideo_URL() {
        return Video_URL;
    }





    //Setter

    public void setVideo_ID(int video_ID) {
        Video_ID = video_ID;
    }

    public void setVideo_URL(String video_URL) {
        Video_URL = video_URL;
    }

}
