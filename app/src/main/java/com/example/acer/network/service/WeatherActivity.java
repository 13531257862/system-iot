package com.example.acer.network.service;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.acer.network.R;
import com.example.acer.network.gson.weather;
import com.example.acer.network.util.HttpUtil;
import com.example.acer.network.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    //下来刷新的
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    //左侧划出菜单
    public DrawerLayout drawerLayout;
    private Button navButton;
//    private ImageView bingPicImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //获取必应图片
//        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        //下来刷新的
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        ////左侧划出菜单
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        weatherLayout = (ScrollView)findViewById(R.id.weather_layout_hahaa);
        //youyongde
        titleCity = (TextView)findViewById(R.id.title_city);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time) ;
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String bingPic = prefs.getString("bing_pic",null);
//        if(bingPic != null){
//            Glide.with(this).load(bingPic).into(bingPicImg);
//        }else{
//            loadBingPic();
//        }
//        String weatherString= prefs.getString("weather",null);
//        if (weatherString != null){
//            weather weather = Utility.handleWeatherResponse(weatherString);
//            mWeatherId = weather.basic.weatherId;
//            showweatherInfo(weather);
//            Toast.makeText(WeatherActivity.this,"有缓存...",Toast.LENGTH_SHORT).show();
//        }else{
//            //无缓存去服务器查询天气
//            mWeatherId = getIntent().getStringExtra("weather_id");
//            String weatherId = getIntent().getStringExtra("weather_id");
//            weatherLayout.setVisibility(View.INVISIBLE);
//            requestweather(mWeatherId);
//            Toast.makeText(WeatherActivity.this,"无缓存...",Toast.LENGTH_SHORT).show();
//        }
        //设置下拉的下拉时间
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherString= prefs.getString("weather",null);
        if (weatherString != null){
            weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showweatherInfo(weather);
            Toast.makeText(WeatherActivity.this,"有缓存...",Toast.LENGTH_SHORT).show();
        }else{
            //无缓存去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestweather(mWeatherId);
            Toast.makeText(WeatherActivity.this,"无缓存...",Toast.LENGTH_SHORT).show();
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                requestweather(mWeatherId);
            }
        });
    }
    //根据天气id请求城市天气信息
    public void requestweather(final  String weatherId){
      String weatherUrl = "https://free-api.heweather.com/s6/weather/now?location="+weatherId+"&key=75c3fbfabcd44cdea06eb23333afd979";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
              final String responseText = response.body().string();
                final weather weather = Utility.handleWeatherResponse(responseText);
                 final String Status = weather.status;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(Status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showweatherInfo(weather);
                            Toast.makeText(WeatherActivity.this,"这波可以...",Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(WeatherActivity.this,Status,Toast.LENGTH_SHORT).show();
                            Toast.makeText(WeatherActivity.this,"获取天气数据失败...",Toast.LENGTH_SHORT).show();

                        }

                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气数据失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
//        loadBingPic();
    }
//    private void loadBingPic(){
//        String requestBingPic = "http://guolin.tech/api/bing_pic";
//        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String bingPic = response.body().string();
//                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//                editor.putString("bing_pic",bingPic);
//                editor.apply();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
//                    }
//                });
//            }
//
//});
//    }
    private void showweatherInfo (weather weather){
        String cityName = weather.basic.cityName;
        //update方法一定要加public，否则这里访问不到！！！！！！
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more;
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        weatherLayout.setVisibility(View.VISIBLE);


    }
}
