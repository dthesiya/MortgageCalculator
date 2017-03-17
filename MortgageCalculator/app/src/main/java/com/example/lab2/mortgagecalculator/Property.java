package com.example.lab2.mortgagecalculator;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vicky on 3/15/2017.
 */

public class Property {

    private String type;
    private String address;
    private String city;
    private double loan_amt;
    private double apr;
    private double monthly_pay;
    private LatLng latlng;

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLoan_amt() {
        return loan_amt;
    }

    public void setLoan_amt(double loan_amt) {
        this.loan_amt = loan_amt;
    }

    public double getApr() {
        return apr;
    }

    public void setApr(double apr) {
        this.apr = apr;
    }

    public double getMonthly_pay() {
        return monthly_pay;
    }

    public void setMonthly_pay(double monthly_pay) {
        this.monthly_pay = monthly_pay;
    }


}
