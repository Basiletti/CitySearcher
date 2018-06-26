package com.basiletti.gino.citysearcher.enums;

public enum FragmentType {

    SEARCH_FRAGMENT ("SEARCH_FRAGMENT"),
    MAP_FRAGMENT ("MAP_FRAGMENT");

    private final String name;

    FragmentType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }


}
