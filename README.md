# AMap
本工程为高德地图Android SDK 3D版本和高德定位Android SDK的Demo，基于AMap3DMap_7.8.0_AMapNavi_7.8.0_AMapSearch_7.7.0_AMapTrack_1.3.0_AMapLocation_5.2.0整合版本SDK

## 前述：
- API 传送门：[高德官方网站申请key](https://developer.amap.com/api/android-location-sdk/guide/create-project/get-key)

## 包含的功能点

- 显示地图
- 获取定位数据
- 地图的放大缩小
- 自动跳转以定位为中心
- 标准，卫星，夜间，导航四种模式的地图的切换与显示
- poi兴趣点的搜索以及路径绘制
- 天气查询
- 天气查询背景图每日更新
- 账号登入UI

## 效果图如下：
![显示地图](https://github.com/Ferguson-fang/AMap/blob/main/app/image/show_map.gif)

![自动跳转以定位为中心](https://github.com/Ferguson-fang/AMap/blob/main/app/image/location_be_center.gif)

![地图的放大缩小](https://github.com/Ferguson-fang/AMap/blob/main/app/image/zoom_in_out.gif)

![标准，卫星，夜间，导航四种模式的地图的切换与显示](https://github.com/Ferguson-fang/AMap/blob/main/app/image/four_patterns.gif)

![poi兴趣点的搜索以及路径绘制](https://github.com/Ferguson-fang/AMap/blob/main/app/image/poi_search_draw.gif)

![天气查询](https://github.com/Ferguson-fang/AMap/blob/main/app/image/weather_search.gif)

![账号登入UI](https://github.com/Ferguson-fang/AMap/blob/main/app/image/login.gif)

![天气查询背景图](https://github.com/Ferguson-fang/AMap/blob/main/app/image/weaather_background.gif)

# 方法实现
### 地图显示
```xml
<com.amap.api.maps.MapView
        android:id="@+id/map"
        android:layout_width="wrap_content"
        android:layout_height="match_parent" />
```
### 获取定位数据
```java
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
  Log.e("aMap","地址来源是："+aMapLocation.getLocationType());
```
###增加控件与定位蓝点
```java
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
        }
```
### 地图模式切换
```
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
```
### POI兴趣点搜索
```xml
设置自动补齐关键字控件
<AutoCompleteTextView
        android:id="@+id/search_poi"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:popupBackground="@android:color/holo_green_light"
        android:hint="请输入目的地址"
        android:layout_marginLeft="70dp"/>
```
```java
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
```
### 路径绘制
```java
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
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
            }
        });
        //启动路径搜索
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery);
    }
```
### 天气查询
```java
    /**
     * 预报天气查询
     */
    private void searchforcastsweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 实时天气查询
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }


    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                reporttime1.setText(weatherlive.getReportTime() + "发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
            } else {
                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }

    /**
     * 天气预报查询结果回调
     */
    @Override
    public void onWeatherForecastSearched(
            LocalWeatherForecastResult weatherForecastResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                weatherforecast = weatherForecastResult.getForecastResult();
                forecastlist = weatherforecast.getWeatherForecast();
                fillforecast();

            } else {
                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }

```
### 每日一图
修改activity_weather_search中的代码
```xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/bing_pic_img"
        android:scaleType="centerCrop"/>
        
    <...
         
</FrameLayout>
```
修改WeatherSearchActivity中的代码
```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_search);
        ...
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }
        
        
private void loadBingPic() {
        String requestBingPic = " http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherSearchActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherSearchActivity.this).load(bingPic).into
                                (bingPicImg);
                    }
                });
            }
        });


    }
```

# 待提升的地方
1. UI设计不美观，需要多看一些好的UI，多进行模仿，实战。
2. 从当前位置到poi兴趣点的路径只设置了一条，没有多条路径供用户选择。
3. 天气的查询没有设置背景图片，显得不美观
4. 账号登录只是制作了UI，没有找到接口实现真正的账号密码登入。
5. 功能相对较少了些
6. 未使用kotlin


# 心得体会
这此的寒假考核对我这样的小白来说是一次很大的挑战，从刚开始拿到题目的无从下手，到现在懵懵懂懂做出一个有点样子的app，不管最后结果怎样，期间的过程是让我受益匪浅的，边学边做已经是常态，几乎每走一步都会遇到自己不明白的地方，然后就开始CSDN上疯狂查找，期间还不断向国林学长提问。其实一开始选题的时候我也是毫无头绪，大概都看了一遍后，因为高德地图平时用的比较多就决定做高德地图了。我觉得高德地图的考核还是挺友好的，根据官方提供的SDK慢慢学一步一步实现功能并不是一件难事。但是有一点我后来才意识到，就是高德地图官方将大部分需要网络请求的内容都封装在了SDK里面，所以使用官方的SDK就不会用到网络请求的部分，那段时间我还一直在想到底哪里需要网络请求，最后问了国林学长才知道。这样的结果就是我对网络请求的体验不到位，虽然自己有去看看网络请求相关教程学习，但没有实战的体验来的深刻。几天前在群里看到学长说寒假考核的主要目的就是让我们体会网络请求和json解析时我就感觉这个寒假我的任务没有达成了，交完作业后，要先好好学习和练习网络请求和json解析，希望不要落下太多。因为网校的考核，这个寒假过的还是挺充实的。
