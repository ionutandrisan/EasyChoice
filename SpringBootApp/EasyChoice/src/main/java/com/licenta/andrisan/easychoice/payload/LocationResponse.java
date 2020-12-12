package com.licenta.andrisan.easychoice.payload;

import java.io.Serializable;

public class LocationResponse implements Serializable {

    private int locationId;

    public LocationResponse(int locationId) {
        this.locationId = locationId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
}
