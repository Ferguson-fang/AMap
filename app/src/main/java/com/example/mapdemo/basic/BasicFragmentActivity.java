package com.example.mapdemo.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.mapdemo.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BasicFragmentActivity extends AppCompatActivity implements View.OnClickListener {
    //创建一个地图容器对象
    private MapView mapView;
    //设置地图对象
    private AMap aMap;
    //定义一个UiSettings对象,给amap设置地图内嵌控件
    private UiSettings mUiSettings;
    //创建蓝点对象
    private MyLocationStyle myLocationStyle;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;


    private Button basicmap;
    private Button rsmap;
    private Button nightmap;
    private Button navimap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_fragment);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        init();
    }

    //开启定位服务
    protected void startLocation() {
        //1,创建一个句柄
        mLocationClient = new AMapLocationClient(getApplicationContext());

        //2，给客户端句柄设置一个Listener处理服务器返回的定位数据
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //OnLocationChanged：如果服务器给客户端返回数据，调用的回调函数
                //aMapLocation  服务器给客户端返回的定位数据
                if (aMapLocation != null) {
                    //定位成功，aMapLocation获取数据
                    if (aMapLocation.getErrorCode() == 0) {
                        //获取当前定位结果来源（网络等）
                        aMapLocation.getLocationType();
                        //获取纬度
                        aMapLocation.getLatitude();
                        //获取经度
                        aMapLocation.getLongitude();
                        //获取精度信息
                        aMapLocation.getAccuracy();
                        //定位时间
                        SimpleDateFormat df = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);


                    } else {
                        //定位失败，
                        Log.e("AmapError", "location error,code ="
                                + aMapLocation.getErrorCode() + ", info = "
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        //3，开启定位服务
        mLocationClient.startLocation();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            //实例化UiSettings类对象
            mUiSettings = aMap.getUiSettings();
            //设置指南针
            mUiSettings.setCompassEnabled(true);
            //设置比例尺
            mUiSettings.setScaleControlsEnabled(true);

            //设置定位小蓝点
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.interval(2000);
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);
            myLocationStyle.myLocationType(MyLocationStyle.
                    LOCATION_TYPE_LOCATION_ROTATE);
            myLocationStyle.showMyLocation(true);
            myLocationStyle.strokeColor(Color.BLUE);
            myLocationStyle.radiusFillColor(Color.RED);
            myLocationStyle.strokeWidth(20);
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                    fromResource(R.drawable.gps_point));
            myLocationStyle.myLocationType(MyLocationStyle.
                    LOCATION_TYPE_MAP_ROTATE);
            myLocationStyle.anchor(0.1f, 0.1f);

            //设置交通状况
            aMap.setTrafficEnabled(true);

            //开启室内地图
            aMap.showIndoorMap(true);

        }
        basicmap = findViewById(R.id.basicmap);
        basicmap.setOnClickListener(this);
        rsmap = findViewById(R.id.rsmap);
        rsmap.setOnClickListener(this);
        nightmap = findViewById(R.id.nightmap);
        nightmap.setOnClickListener(this);
        navimap = findViewById(R.id.navimap);
        navimap.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basicmap:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.rsmap:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.nightmap:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                break;
            case R.id.navimap:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);
                break;
        }
    }
}