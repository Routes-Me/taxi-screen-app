package com.example.routesapp.MoneyAndNewsPart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse {

    @SerializedName("items")
    @Expose
    private List<News> news;

    public List<News> getNews(){
        return news;
    }

    public void setNews(List<News> news){
        this.news = news;
    }



}
