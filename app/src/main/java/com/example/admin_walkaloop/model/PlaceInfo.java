package com.example.admin_walkaloop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceInfo {
    @SerializedName("SubPlace")
    private String SubPlace;
    @SerializedName("place_name")
    private String place_name;
    @SerializedName("Description")
    private String Description;
    @SerializedName("Latitude")
    private String Latitude;
    @SerializedName("Longitude")
    private String Longitude;
    @SerializedName("MarkeePoint")
    private int MarkeePoint;
    @SerializedName("Image")
    private String Image;


    public int getNpostion() {
        return MarkeePoint;
    }

    public void setNpostion(int npostion) {
        this.MarkeePoint = npostion;
    }


    public String getImg_encode() {
        return Image;
    }

    public void setImg_encode(String img_encode) {
        this.Image = img_encode;
    }


    public String getStr_name() {
        return SubPlace;
    }

    public void setStr_name(String str_name) {
        this.SubPlace = str_name;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }


    public String getPlace_desc() {
        return Description;
    }

    public void setPlace_desc(String place_desc) {
        this.Description = place_desc;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
