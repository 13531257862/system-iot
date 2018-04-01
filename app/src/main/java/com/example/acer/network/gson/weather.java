package com.example.acer.network.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by acer on 2018/3/29.
 */

public class weather {
    @SerializedName("status")
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Update update;
    public  Suggestion  suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
