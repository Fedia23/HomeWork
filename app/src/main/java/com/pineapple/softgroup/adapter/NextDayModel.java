package com.pineapple.softgroup.adapter;

import java.util.ArrayList;
import java.util.List;


public class NextDayModel {
    private String day;
    private String condition;
    private String temperatureCmax;
    private String temperatureMin;
    private int code;

    public NextDayModel(String day, String condition, String temperatureCmax, String temperatureMin, int code) {
        this.day = day;
        this.condition = condition;
        this.temperatureCmax = temperatureCmax;
        this.temperatureMin = temperatureMin;
        this.code = code;
    }

    public String getDay() {
        return day;
    }

    public String getCondition() {
        return condition;
    }

    public String getTemperatureCmax() {
        return temperatureCmax;
    }

    public String getTemperatureMin() {
        return temperatureMin;
    }

    public int getCode() {
        return code;
    }

    public static List<NextDayModel> list = new ArrayList<>();
}
