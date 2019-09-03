package com.example.bhati.routeapplication.Model;

public class Recorder {
    private String lat;
    private String lng;
    private  String time;
    private String astatus;

    public Recorder(String lat, String lng, String time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public Recorder(String lat, String lng, String time,String astatus) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.astatus = astatus;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }
    public String getAudio() {
        return astatus;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAudioStatus(String astatus) {
        this.astatus= astatus;
    }
}
