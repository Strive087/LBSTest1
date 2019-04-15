package com.example.a84045.lbstest1.Entity;

import java.sql.Date;

public class HouseOrder {

    private long orderid;

    private Date orderdate;

    private float orderprice;

    private int orderday;

    private int orderstatu;

    private long houseid;

    private String housename;

    private String houseprovince;

    private String housecty;

    private String housedistrict;

    private String housestreet;

    private long sellerid;

    private String sellerphone;

    private String sellername;

    private long buyerid;

    private String buyerphone;

    private String buyername;

    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public float getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(float orderprice) {
        this.orderprice = orderprice;
    }

    public int getOrderday() {
        return orderday;
    }

    public void setOrderday(int orderday) {
        this.orderday = orderday;
    }

    public int getOrderstatu() {
        return orderstatu;
    }

    public void setOrderstatu(int orderstatu) {
        this.orderstatu = orderstatu;
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

    public String getHouseprovince() {
        return houseprovince;
    }

    public void setHouseprovince(String houseprovince) {
        this.houseprovince = houseprovince;
    }

    public String getHousecty() {
        return housecty;
    }

    public void setHousecty(String housecty) {
        this.housecty = housecty;
    }

    public String getHousedistrict() {
        return housedistrict;
    }

    public void setHousedistrict(String housedistrict) {
        this.housedistrict = housedistrict;
    }

    public String getHousestreet() {
        return housestreet;
    }

    public void setHousestreet(String housestreet) {
        this.housestreet = housestreet;
    }

    public long getSellerid() {
        return sellerid;
    }

    public void setSellerid(long sellerid) {
        this.sellerid = sellerid;
    }

    public String getSellerphone() {
        return sellerphone;
    }

    public void setSellerphone(String sellerphone) {
        this.sellerphone = sellerphone;
    }

    public String getSellername() {
        return sellername;
    }

    public void setSellername(String sellername) {
        this.sellername = sellername;
    }

    public long getBuyerid() {
        return buyerid;
    }

    public void setBuyerid(long buyerid) {
        this.buyerid = buyerid;
    }

    public String getBuyerphone() {
        return buyerphone;
    }

    public void setBuyerphone(String buyerphone) {
        this.buyerphone = buyerphone;
    }

    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }
}
