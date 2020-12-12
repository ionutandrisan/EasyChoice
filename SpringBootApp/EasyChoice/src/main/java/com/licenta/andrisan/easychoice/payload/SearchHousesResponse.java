package com.licenta.andrisan.easychoice.payload;

public class SearchHousesResponse extends HousesResponse {

    private String description;
    private String city;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
