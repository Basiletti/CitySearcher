package com.basiletti.gino.citysearcher.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CityObject implements Comparable<CityObject>, Parcelable {

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


    protected CityObject(Parcel in) {
        country = in.readString();
        name = in.readString();
        _id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(country);
        dest.writeString(name);
        dest.writeString(_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CityObject> CREATOR = new Creator<CityObject>() {
        @Override
        public CityObject createFromParcel(Parcel in) {
            return new CityObject(in);
        }

        @Override
        public CityObject[] newArray(int size) {
            return new CityObject[size];
        }
    };

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

