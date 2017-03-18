package com.example.lab2.mortgagecalculator.daos;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by vicky on 3/15/2017.
 */

public class Property implements Serializable{

    private int id;
    private String type;
    private String address;
    private String city;
    private String state;
    private String zipcode;

    private double loan_amt;
    private double down_pay;
    private double apr;
    private int term;

    private double result;

    private LatLng latlng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getLoan_amt() {
        return loan_amt;
    }

    public void setLoan_amt(double loan_amt) {
        this.loan_amt = loan_amt;
    }

    public double getDown_pay() {
        return down_pay;
    }

    public void setDown_pay(double down_pay) {
        this.down_pay = down_pay;
    }

    public double getApr() {
        return apr;
    }

    public void setApr(double apr) {
        this.apr = apr;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
