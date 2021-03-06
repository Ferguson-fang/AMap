package com.example.mapdemo.basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PoiPara;
import com.amap.api.navi.model.search.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.interfaces.IRouteSearch;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.mapdemo.MainActivity;
import com.example.mapdemo.R;
import com.example.mapdemo.SelfCenterActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import overlay.BusRouteOverlay;
import overlay.DrivingRouteOverlay;
import overlay.RideRouteOverlay;
import overlay.WalkRouteOverlay;
import weather.WeatherSearchActivity;

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
    //AMapLocationClient句柄属性对象
    public AMapLocationClientOption aMapLocationClientOption;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //显示当前自己位置的图标
    private Marker selfMaker;
    //当前定位所在的城市
    private String city;

    //设置路线起始坐标
    private com.amap.api.services.core.LatLonPoint startPoint;
    private com.amap.api.services.core.LatLonPoint endPoint;

    private Button basicmap;
    private Button rsmap;
    private Button nightmap;
    private Button navimap;
    private Button search_poi_button;
    boolean isAddSelfMaker;
    private Context mContext;
    private AutoCompleteTextView search_poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_fragment);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        search_poi_button = findViewById(R.id.search_poi_button);
        search_poi = findViewById(R.id.search_poi);
        mContext = this.getApplicationContext();
        init();
        doSearchPOI();
        initAutoCompleteTextView();
    }

    //绘制路径
    protected void drawRouteLine(){

        //1 设置绘制路径的句柄 routeSearch
        RouteSearch routeSearch = new RouteSearch(getApplicationContext());
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint,endPoint);

        //2 设置一个路径搜索的query
        RouteSearch.DriveRouteQuery driveRouteQuery = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DrivingDefault,null,null,"");
        //3 给绘制路径设置callback函数
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
                //公交路线查询

                //判断是否请求成功
                if(i != 1000){
                    Log.e("AMap","搜索公交路径失败");
                    return;
                }

                //画出驾驶路径

                //1拿到得到的路径（默认第一个方案为优选方案）
                BusPath busPath = busRouteResult.getPaths().get(0);

                //驾驶路径的覆盖物
                BusRouteOverlay busRouteOverlay = new BusRouteOverlay(
                        mContext,
                        aMap,
                        busPath,
                        startPoint,
                        endPoint
                );
                //先把之前的路径删除掉
                aMap.clear();

                //将路径添加到地图
                busRouteOverlay.addToMap();
                //以适当的比例收缩
                busRouteOverlay.zoomToSpan();
                //去掉中间转弯的标识
                busRouteOverlay.setNodeIconVisibility(false);


            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                //驾驶路线查询

                //判断是否请求成功
                if(i != 1000){
                    Log.e("AMap","搜索驾驶路径失败");
                    return;
                }

                //画出驾驶路径

                //1 拿到得到的路径（默认第一个方案为优选方案）
                DrivePath drivePath = driveRouteResult.getPaths().get(0);

                //驾驶路径的覆盖物
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        mContext,
                        aMap,
                        drivePath,
                        startPoint,
                        endPoint,null
                );

                //先把之前的路径删除掉
                aMap.clear();

                //将路径添加到地图
                drivingRouteOverlay.addToMap();


                //以适当的比例收缩
                drivingRouteOverlay.zoomToSpan();

                //去掉中间转弯的标识

                drivingRouteOverlay.setNodeIconVisibility(false);
                drivingRouteOverlay.setThroughPointIconVisibility(true);

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
                //步行路线查询

                //判断是否请求成功
                if(i != 1000){
                    Log.e("AMap","搜索步行路径失败");
                    return;
                }

                //画出驾驶路径

                //1 拿到得到的路径（默认第一个方案为优选方案）
                WalkPath walkPath = new WalkRouteResult().getPaths().get(0);

                //驾驶路径的覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                        mContext,
                        aMap,
                        walkPath,
                        startPoint,
                        endPoint
                );
                //先把之前的路径删除掉
                aMap.clear();

                //将路径添加到地图
                walkRouteOverlay.addToMap();


                //以适当的比例收缩
                walkRouteOverlay.zoomToSpan();

                //去掉中间转弯的标识

                walkRouteOverlay.setNodeIconVisibility(false);
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
                //骑行路线查询

                //判断是否请求成功
                if(i != 1000){
                    Log.e("AMap","搜索驾驶路径失败");
                    return;
                }

                //
                RidePath ridePath = new RideRouteResult().getPaths().get(0);

                //
                RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                        mContext,
                        aMap,
                        ridePath,
                        startPoint,
                        endPoint
                );

                //
                aMap.clear();
                //
                rideRouteOverlay.addToMap();
                //
                rideRouteOverlay.zoomToSpan();
                //
                rideRouteOverlay.setNodeIconVisibility(false);
            }
        });
        //启动路径搜索
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }


    //绑定AutoCompleteTextView控件，自动搜索高德地图兴趣点列表，自动弹出输入提示列表
    protected void initAutoCompleteTextView(){
        //给attv控件设置一个阈值
        search_poi.setThreshold(1);

        //给initAutoCompleteTextView绑定自动补齐功能
        search_poi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当文本内容发生改变时调用此函数


                //1 得到搜索的关键字
                String keyword = search_poi.getText().toString();
                if(keyword == null || keyword.length() == 0){
                    Log.e("AMap","search keyword  == null");
                    return;
                }
                //2 创建一个query查询所有tips的条件
                InputtipsQuery inputtipsQuery = new InputtipsQuery(keyword,city);
                //3 创建一个InputTips 查询句柄
                Inputtips inputtips = new Inputtips(getApplicationContext(),inputtipsQuery);
                //4 给InputTips设定回调函数
                inputtips.setInputtipsListener(new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> list, int i) {
                        if(i != 1000){
                            return;
                        }
                        //1 从服务器获得能够匹配的poi数据
                        ArrayList<String> poiList = new ArrayList<String>();

                        for(int index = 0;index<list.size();index++)
                        {
                            Log.e("AMap","通过keyword匹配到的key有"+list.get(index).getName());
                            poiList.add(list.get(index).getName());
                        }

                        //2 给AutoCompleteTextView设置一个适配器Adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_list_item_1,poiList);

                        //3 将Adapter 和 AutoCompleteTextView相关联
                        search_poi.setAdapter(adapter);

                        //4 触发Adapter控件显示单词集合
                        adapter.notifyDataSetChanged();

                    }
                });
                //5 开启InputTips向服务器发起查询请求
                inputtips.requestInputtipsAsyn();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /*
    //向确定的经纬度添加一个标记，因为之前设置的定位小蓝点，因此此标记可以不用。
    private  void addMarkerToMap(double latitude,double longitude)
    {
        selfMaker=aMap.addMarker(new MarkerOptions().position(new LatLng(latitude
                ,longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources()
                                       , R.drawable.location_marker))));
    }
    */
    //
    protected  void doSearchPOI(){
        search_poi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始搜索POI兴趣点

                //拿到用户需要搜索地点的关键字
                String dstAdd = search_poi.getText().toString();

                //开始POI搜索
                //1，创建一个搜索的条件对象
                PoiSearch.Query query = new PoiSearch.Query(dstAdd,"",city);
                //2创建一个POISearch句柄和query关联
                PoiSearch poiSearch = new PoiSearch(getApplicationContext(),query);
                //3给Search绑定一个回调函数
                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                    @Override
                    public void onPoiSearched(PoiResult poiResult, int i) {
                        //处理得到的POI兴趣点集合 poiResult
                        if(i != 1000){
                            Log.e("Amap","poi Search error code = "+ i);
                            return ;
                        }

                        //搜索成功
                        List<PoiItem> poiItemList = poiResult.getPois();
                        for(int index = 0;index<poiItemList.size();index++){
                            //此时表示处理每个已经搜索到的兴趣点
                            Log.e("AMap","搜索到的兴趣点有");
                            PoiItem poiItem = poiItemList.get(index);

                            Log.e("AMap","poi title = "+poiItem.getTitle()+
                                    "latitude ="+poiItem.getLatLonPoint().getLatitude()+
                                    "longitude ="+poiItem.getLatLonPoint().getLongitude());
                            //给标记点画坐标
                            //addMarkerToMap(poiItem.getLatLonPoint().getLatitude(),
                            //        poiItem.getLatLonPoint().getLongitude());

                            //默认第一个兴趣点为我们的坐标点
                            endPoint = new com.amap.api.services.core.LatLonPoint(poiItem.getLatLonPoint().
                                    getLatitude(),poiItem.getLatLonPoint().getLongitude());

                            drawRouteLine();
                            if(index == 0){
                                return;
                            }
                        }
                    }

                    @Override
                    public void onPoiItemSearched(PoiItem poiItem, int i) {

                    }
                });
                //4启动搜索
                poiSearch.searchPOIAsyn();
            }
        });
    }

    //开启定位服务
    protected void doLocation() {
        //1,创建一个句柄
        mLocationClient = new AMapLocationClient(getApplicationContext());

        //给定位客户端设置一些属性
        aMapLocationClientOption = new AMapLocationClientOption();
        //每隔2秒定位一次
        aMapLocationClientOption.setInterval(2000);
        //将option设置给client对象
        mLocationClient.setLocationOption(aMapLocationClientOption);
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
                        Log.e("aMap","地址来源是："+aMapLocation.getLocationType());
                        SimpleDateFormat df = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);

                        //if(isAddSelfMaker == false){
                            //在此位置添加一个标记
                        //    addMarkerToMap(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                        //    isAddSelfMaker = true;
                        //}

                        if(startPoint == null){
                            //得到起始坐标
                            //startPoint = new LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                            startPoint = new com.amap.api.services.core.LatLonPoint(aMapLocation.getLatitude(),aMapLocation.getLongitude());
                        }

                        //设置地址信息
                        city = aMapLocation.getCity();

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
        doLocation();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bottom_menu_home:
                Toast.makeText(BasicFragmentActivity.this,"进入首页",
                        Toast.LENGTH_SHORT).show();
                aMap.clear();
                break;
            case R.id.bottom_menu_found:
                Toast.makeText(BasicFragmentActivity.this,"进入天气",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BasicFragmentActivity.
                        this, WeatherSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.bottom_menu_message:
                Toast.makeText(BasicFragmentActivity.this,"进入我的",
                        Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(BasicFragmentActivity.
                        this, SelfCenterActivity.class);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}