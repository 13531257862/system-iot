package com.example.acer.network.service;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.network.MainActivity;
import com.example.acer.network.R;
import com.example.acer.network.db.City;
import com.example.acer.network.db.County;
import com.example.acer.network.db.Province;
import com.example.acer.network.util.HttpUtil;
import com.example.acer.network.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class Choose_areaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private Button back_button;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private  List<County> countyList;

    //选中的省份
    private Province selectedProvince;

    //选中的市
    private City selectedCity;

    //当前选中的级别
    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        back_button = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    /////看这里
//                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
//                        intent.putExtra("weather_id", weatherId);
//                        startActivity(intent);
//                        getActivity().finish();

                    if(getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity) {
                            WeatherActivity activity = (WeatherActivity) getActivity();
                            activity.drawerLayout.closeDrawers();
                            activity.swipeRefresh.setRefreshing(true);
                            activity.requestweather(weatherId);
                        }
                    }
                }

        });
        back_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces (){
        titleText.setText("中国");
        back_button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

            currentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china/";
            queryFromServer(address,"province");
        }
    }
    private  void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        back_button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
     //       titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            int provinceConde = selectedProvince.getProvinceCode();

            String address = "http://guolin.tech/api/china/"+provinceConde;
            queryFromServer(address,"city");
        }
    }
    private  void queryCounties(){
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        titleText.setText(selectedCity.getCityName());
        back_button.setVisibility(View.VISIBLE);
        if (countyList.size() >0 ){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceConde = selectedProvince.getProvinceCode();
            int cityConde = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceConde+"/"+cityConde;
            queryFromServer(address,"county");
        }
    }

    /*根据传入的代号和类型从服务器上查询省市县的数据*/
    private void queryFromServer( String address,final String type){
        showProgressDialog();
          //开启线程
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //handle**Response()方法都是boolean类型的，判断是否将数据存储到表中
          @Override
            public void onResponse(Call call,Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(responseText,selectedCity.getId());
                }

                if (result){
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call,IOException e) {
                //通过runOnUiThread()方法回调主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败啦(╥╯^╰╥)", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });
    }

    /*显示进度对话框*/
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("努力加载中.... ↖(^ω^)↗");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*
    * 关闭进度对话框*/
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /*
    * 捕获Back键，根据当前级别来判断，是应该市列表、省列表，还是直接退出*/


}
