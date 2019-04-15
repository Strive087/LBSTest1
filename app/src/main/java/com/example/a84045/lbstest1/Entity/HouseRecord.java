package com.example.a84045.lbstest1.Entity;

import java.sql.Date;

public class HouseRecord {

    private long recordid;

    private Date recorddate;

    private long usrid;

    private long houseid;

    private String housename;

    private String houseshape;

    private String housearea;

    private float houseprice;

    private double houselatitude;

    private double houselongitude;

    public long getUsrid() {
        return usrid;
    }

    public void setUsrid(long usrid) {
        this.usrid = usrid;
    }

    public long getRecordid() {
        return recordid;
    }

    public void setRecordid(long recordid) {
        this.recordid = recordid;
    }

    public Date getRecorddate() {
        return recorddate;
    }

    public void setRecorddate(Date recorddate) {
        this.recorddate = recorddate;
    }

    public long getHouseid() {
        return houseid;
    }

    public void setHouseid(long houseid) {
        this.houseid = houseid;
    }

    public String getHousename() {
        return housename;
    }

    public void setHousename(String housename) {
        this.housename = housename;
    }

    public String getHouseshape() {
        return houseshape;
    }

    public void setHouseshape(String houseshape) {
        this.houseshape = houseshape;
    }

    public String getHousearea() {
        return housearea;
    }

    public void setHousearea(String housearea) {
        this.housearea = housearea;
    }

    public float getHouseprice() {
        return houseprice;
    }

    public void setHouseprice(float houseprice) {
        this.houseprice = houseprice;
    }

    public double getHouselatitude() {
        return houselatitude;
    }

    public void setHouselatitude(double houselatitude) {
        this.houselatitude = houselatitude;
    }

    public double getHouselongitude() {
        return houselongitude;
    }

    public void setHouselongitude(double houselongitude) {
        this.houselongitude = houselongitude;
    }
}
