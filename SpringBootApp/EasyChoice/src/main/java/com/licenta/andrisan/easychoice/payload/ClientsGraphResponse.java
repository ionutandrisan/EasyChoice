package com.licenta.andrisan.easychoice.payload;

import java.util.HashMap;

public class ClientsGraphResponse {
    private HashMap<Integer, Integer> clientsHashMap;
    private HashMap<Integer, Integer> earningsPerMonth;
    private HashMap<Integer, Integer> nrReviews;
    private HashMap<Integer, Double> reviewAvg;

    public ClientsGraphResponse() {

    }


    public HashMap<Integer, Integer> getClientsHashMap() {
        return clientsHashMap;
    }

    public void setClientsHashMap(HashMap<Integer, Integer> clientsHashMap) {
        this.clientsHashMap = clientsHashMap;
    }

    public HashMap<Integer, Integer> getEarningsPerMonth() {
        return earningsPerMonth;
    }

    public void setEarningsPerMonth(HashMap<Integer, Integer> earningsPerMonth) {
        this.earningsPerMonth = earningsPerMonth;
    }

    public HashMap<Integer, Integer> getNrReviews() {
        return nrReviews;
    }

    public void setNrReviews(HashMap<Integer, Integer> nrReviews) {
        this.nrReviews = nrReviews;
    }

    public HashMap<Integer, Double> getReviewAvg() {
        return reviewAvg;
    }

    public void setReviewAvg(HashMap<Integer, Double> reviewAvg) {
        this.reviewAvg = reviewAvg;
    }
}
