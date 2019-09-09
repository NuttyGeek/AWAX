package com.example.bhati.routeapplication.Pojo;

public class ImageLabel {

    private String id;
    private String name;
    private float score;


    // Default Constructor
    public ImageLabel(){

    }

    public ImageLabel(String id, String name, float score){
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return this.name;
    }

    public float getScore() {
        return score;
    }
}
