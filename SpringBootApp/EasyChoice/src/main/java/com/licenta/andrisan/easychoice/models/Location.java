package com.licenta.andrisan.easychoice.models;

public class Location {

    private String country;
    private String city;
    private String state;
    private String zipcode;

    public Location(String country, String city, String state, String zipcode) {
        this.country = country;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
