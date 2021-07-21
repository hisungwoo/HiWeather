
package org.ganache.hiweather.model;

import javax.annotation.Generated;

public class Item {

    @com.squareup.moshi.Json(name = "baseDate")
    private String baseDate;
    @com.squareup.moshi.Json(name = "baseTime")
    private String baseTime;
    @com.squareup.moshi.Json(name = "category")
    private String category;
    @com.squareup.moshi.Json(name = "fcstDate")
    private String fcstDate;
    @com.squareup.moshi.Json(name = "fcstTime")
    private String fcstTime;
    @com.squareup.moshi.Json(name = "fcstValue")
    private float fcstValue;
    @com.squareup.moshi.Json(name = "nx")
    private int nx;
    @com.squareup.moshi.Json(name = "ny")
    private int ny;

    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public String getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(String baseTime) {
        this.baseTime = baseTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFcstDate() {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public float getFcstValue() {
        return fcstValue;
    }

    public void setFcstValue(float fcstValue) {
        this.fcstValue = fcstValue;
    }

    public int getNx() {
        return nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public int getNy() {
        return ny;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

}
