package com.licenta.andrisan.easychoice.payload;

public class HouseResponse extends SearchHousesResponse {

    private String streetName;
    private String lastName;
    private int userID;
    private String photo;
    private String[] houseImages;

    public String[] getHouseImages() {
        return houseImages;
    }

    public void setHouseImages(String[] houseImages) {
        this.houseImages = houseImages;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
