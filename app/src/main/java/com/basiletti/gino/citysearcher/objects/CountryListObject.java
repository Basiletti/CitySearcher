package com.basiletti.gino.citysearcher.objects;

import com.basiletti.gino.citysearcher.objects.CityObject;

import java.util.ArrayList;

public class CountryListObject {

    private ArrayList<CityObject> cityObjects;

    public ArrayList<CityObject> getCityObjects() {
        return cityObjects;
    }

    public void setCityObjects(ArrayList<CityObject> cityObjects) {
        this.cityObjects = cityObjects;
    }
}

