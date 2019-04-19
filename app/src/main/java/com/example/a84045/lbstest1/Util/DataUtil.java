package com.example.a84045.lbstest1.Util;

import com.example.a84045.lbstest1.Entity.HouseRent;

import java.io.Serializable;

public class DataUtil implements Serializable {

    private static final long serialVersionUID = 1;

    private HouseRent houseRent;

    public HouseRent getHouseRent() {
        return houseRent;
    }

    public void setHouseRent(HouseRent houseRent) {
        this.houseRent = houseRent;
    }
}
