package com.licenta.andrisan.easychoice.payload;

public class HousesResponse {

    private int houseId;
    private String houseName;
    private double costPerNight;
    private String mainImage;
    private String country;
    private double rating;
    private double reviewNumber;

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public double getCostPerNight() {
        return costPerNight;
    }

    public void setCostPerNight(double costPerNight) {
        this.costPerNight = costPerNight;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getReviewNumber() {
        return reviewNumber;
    }

    public void setReviewNumber(double reviewNumber) {
        this.reviewNumber = reviewNumber;
    }
}
