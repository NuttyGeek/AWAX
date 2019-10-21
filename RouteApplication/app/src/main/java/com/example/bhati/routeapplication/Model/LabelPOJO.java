package com.example.bhati.routeapplication.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class LabelPOJO {

    @SerializedName("bicycle")
    @Expose
    private Double bicycle;
    @SerializedName("building")
    @Expose
    private Double building;
    @SerializedName("bus")
    @Expose
    private Double bus;
    @SerializedName("car")
    @Expose
    private Double car;
    @SerializedName("fence")
    @Expose
    private Double fence;
    @SerializedName("motocycle")
    @Expose
    private Double motocycle;
    @SerializedName("person")
    @Expose
    private Double person;
    @SerializedName("pole")
    @Expose
    private Double pole;
    @SerializedName("rider")
    @Expose
    private Double rider;
    @SerializedName("road")
    @Expose
    private Double road;
    @SerializedName("sidewalk")
    @Expose
    private Double sidewalk;
    @SerializedName("sky")
    @Expose
    private Double sky;
    @SerializedName("terrain")
    @Expose
    private Double terrain;
    @SerializedName("traffic light")
    @Expose
    private Double trafficLight;
    @SerializedName("traffic sign")
    @Expose
    private Double trafficSign;
    @SerializedName("train")
    @Expose
    private Double train;
    @SerializedName("truck")
    @Expose
    private Double truck;
    @SerializedName("vegetation")
    @Expose
    private Double vegetation;
    @SerializedName("wall")
    @Expose
    private Double wall;

    public Double getBicycle() {
        return bicycle;
    }

    public void setBicycle(Double bicycle) {
        this.bicycle = bicycle;
    }

    public Double getBuilding() {
        return building;
    }

    public void setBuilding(Double building) {
        this.building = building;
    }

    public Double getBus() {
        return bus;
    }

    public void setBus(Double bus) {
        this.bus = bus;
    }

    public Double getCar() {
        return car;
    }

    public void setCar(Double car) {
        this.car = car;
    }

    public Double getFence() {
        return fence;
    }

    public void setFence(Double fence) {
        this.fence = fence;
    }

    public Double getMotocycle() {
        return motocycle;
    }

    public void setMotocycle(Double motocycle) {
        this.motocycle = motocycle;
    }

    public Double getPerson() {
        return person;
    }

    public void setPerson(Double person) {
        this.person = person;
    }

    public Double getPole() {
        return pole;
    }

    public void setPole(Double pole) {
        this.pole = pole;
    }

    public Double getRider() {
        return rider;
    }

    public void setRider(Double rider) {
        this.rider = rider;
    }

    public Double getRoad() {
        return road;
    }

    public void setRoad(Double road) {
        this.road = road;
    }

    public Double getSidewalk() {
        return sidewalk;
    }

    public void setSidewalk(Double sidewalk) {
        this.sidewalk = sidewalk;
    }

    public Double getSky() {
        return sky;
    }

    public void setSky(Double sky) {
        this.sky = sky;
    }

    public Double getTerrain() {
        return terrain;
    }

    public void setTerrain(Double terrain) {
        this.terrain = terrain;
    }

    public Double getTrafficLight() {
        return trafficLight;
    }

    public void setTrafficLight(Double trafficLight) {
        this.trafficLight = trafficLight;
    }

    public Double getTrafficSign() {
        return trafficSign;
    }

    public void setTrafficSign(Double trafficSign) {
        this.trafficSign = trafficSign;
    }

    public Double getTrain() {
        return train;
    }

    public void setTrain(Double train) {
        this.train = train;
    }

    public Double getTruck() {
        return truck;
    }

    public void setTruck(Double truck) {
        this.truck = truck;
    }

    public Double getVegetation() {
        return vegetation;
    }

    public void setVegetation(Double vegetation) {
        this.vegetation = vegetation;
    }

    public Double getWall() {
        return wall;
    }

    public void setWall(Double wall) {
        this.wall = wall;
    }

    /**
     * this fxn returns full hashmap
     * @return full hashmap
     */
    public HashMap<String, Double> getFullHashMap(){
        HashMap<String, Double> allValues = new HashMap<>();
        allValues.put("bicycle", this.bicycle);
        allValues.put("building", this.building);
        allValues.put("bus", this.bus);
        allValues.put("car", this.car);
        allValues.put("fence", this.fence);
        allValues.put("motocycle", this.motocycle);
        allValues.put("person", this.person);
        allValues.put("pole", this.pole);
        allValues.put("rider", this.rider);
        allValues.put("road", this.road);
        allValues.put("sidewalk", this.sidewalk);
        allValues.put("sky", this.sky);
        allValues.put("terrain", this.terrain);
        allValues.put("traffic light", this.trafficLight);
        allValues.put("traffic sign", this.trafficSign);
        allValues.put("train", this.train);
        allValues.put("truck", this.truck);
        allValues.put("vegetation", this.vegetation);
        allValues.put("wall", this.wall);
        return allValues;
    }

}
