package com.example.acer.network.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by chencong on 2016/7/13.
 */
public class HttpUtil {
   public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
       OkHttpClient client = new OkHttpClient();
       Request request = new Request.Builder().url(address).build();
       client.newCall(request).enqueue(callback);
   }

}
