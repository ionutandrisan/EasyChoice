package com.licenta.andrisan.easychoice.models;

public class House {

    private String country;
    private String city;
    private String streetName;
    private double costPerNight;
    private String email;
    private String photo;
    private String houseName;
    private String description;

    public House() {

    }

    public House(String country, String city, String streetName, double costPerNight, String email,
                 String photo, String houseName, String description) {
        this.country = country;
        this.city = city;
        this.streetName = streetName;
        this.costPerNight = costPerNight;
        this.email = email;
        this.photo = photo;
        this.houseName = houseName;
        this.description = description;
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

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public double getCostPerNight() {
        return costPerNight;
    }

    public void setCostPerNight(double costPerNight) {
        this.costPerNight = costPerNight;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
