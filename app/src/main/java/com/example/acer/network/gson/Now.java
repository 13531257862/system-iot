package com.example.acer.network.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2018/3/29.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
//天气状态：晴朗或者多云等等
    @SerializedName("cond_txt")
    public String more;
    public class More{
        @SerializedName("hum")
        public String info;
    }
}
