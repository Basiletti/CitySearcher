package com.basiletti.gino.citysearcher.objects;

import com.google.gson.annotations.SerializedName;

public class CityObject {

    @SerializedName("country")
    private String countryCode;

    @SerializedName("name")
    private String cityName;

    @SerializedName("_id")
    private String countryID;

    @SerializedName("coord")
    private CoordinateObject coordinateObject;

    public CityObject() {
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public CoordinateObject getCoordinateObject() {
        return coordinateObject;
    }

    public void setCoordinateObject(CoordinateObject coordinateObject) {
        this.coordinateObject = coordinateObject;
    }
}

