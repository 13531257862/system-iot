package com.example.acer.network.gson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.acer.network.R;
import com.google.gson.annotations.SerializedName;

public class Basic {
//这里是jason数据里的Object名
    @SerializedName("location")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

}
