package com.basiletti.gino.citysearcher.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoordinateObject {

    @JsonProperty("lon")
    private double lon;

    @JsonProperty("lat")
    private double lat;

    public double getLongitude() {
        return lon;
    }

    public void setLongitude(double longitude) {
        this.lon = longitude;
    }

    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double latitude) {
        this.lat = latitude;
    }
}
