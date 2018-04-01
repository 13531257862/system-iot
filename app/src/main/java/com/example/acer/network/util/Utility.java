package com.example.acer.network.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.acer.network.db.City;
import com.example.acer.network.db.County;
import com.example.acer.network.db.Province;
import com.example.acer.network.gson.weather;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//由于服务器返回的数据库为“代号|城市”这种格式的，所以需要提供包工具类来解析处理这种数据
public class Utility {
    /*
    * 解析和处理服务器返回的省级数据*/
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
    * 解析和处理市级数据*/
    public static boolean handleCitiesResponse(String response,int provinceId){
        if (! TextUtils.isEmpty(response)){
            try{
                JSONArray allcities = new JSONArray(response);
                for(int i = 0;i <allcities.length();i++){
                    JSONObject cityObject = allcities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        return false;
    }

    /*
    * 解析和处理县级数据*/
    public static boolean handleCountiesResponse(String response,int cityId){
        if (! TextUtils.isEmpty(response)){
            try{
                JSONArray allcounties = new JSONArray(response);
                for(int i = 0;i <allcounties.length();i++){
                    JSONObject countyObject = allcounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
                return false;
            }


            //将返回到jason数据流解析成实体类
        public static weather handleWeatherResponse(String response){
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weatherContent = jsonArray.getJSONObject(0).toString();
                return  new Gson().fromJson(weatherContent,weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }
        }



