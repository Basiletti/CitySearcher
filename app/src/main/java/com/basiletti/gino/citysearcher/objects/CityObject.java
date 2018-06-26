package com.basiletti.gino.citysearcher.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CityObject implements Comparable<CityObject> {

    @JsonProperty("country")
    private String country;

    @JsonProperty("name")
    private String name;

    @JsonProperty("_id")
    private String _id;

    @JsonProperty("coord")
    private CoordinateObject coord;

    public CityObject() {
    }

    public CityObject(String country, String name, String _id, CoordinateObject coord) {
        this.country = country;
        this.name = name;
        this._id = _id;
        this.coord = coord;
    }


    public String getCityName() {
        return name;
    }

    public void setCityName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public CoordinateObject getCoordinateObject() {
        return coord;
    }

    public void setCoordinateObject(CoordinateObject coord) {
        this.coord = coord;
    }

    @Override
    public int compareTo(CityObject o) {
        if (!name.equals(o.getCityName())) {
            return name.compareTo(o.getCityName());
        } else {
            return country.compareTo(o.getCountry());
        }
    }
}

