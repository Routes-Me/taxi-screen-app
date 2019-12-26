package com.routesme.taxi_screen.Model;

public class ItemAnalytics {

    private int id;
    private String name;




    //Constructor...
    public ItemAnalytics() {
    }

    public ItemAnalytics(int id, String name) {
        this.id = id;
        this.name = name;
    }


    //Getter...
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    //Setter...
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
