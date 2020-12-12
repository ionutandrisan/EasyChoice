package com.licenta.andrisan.easychoice.payload;

public class AvailabilityResponse {

    private boolean isAvailable;
    private int noOfDays;

    public AvailabilityResponse(boolean isAvailable, int noOfDays) {
        this.isAvailable = isAvailable;
        this.noOfDays = noOfDays;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(int noOfDays) {
        this.noOfDays = noOfDays;
    }
}
