package com.example.hasee.baidumaptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private ImageView imageview_up, imageview_down;
    private TextView tv_located, tv_satellite, tv_compass;
    private FrameLayout mMapFrame;
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private static LatLng mCurrentLocation;
    private LocationClient mLocationClient;
    private static String mLocationAddr;
    private boolean mIsFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapFrame = (FrameLayout) findViewById(R.id.activity_main);
        tv_compass = (TextView) findViewById(R.id.tv_compass);
        tv_satellite = (TextView) findViewById(R.id.tv_satellite);
        tv_located = (TextView) findViewById(R.id.tv_located);
        imageview_up = (ImageView) findViewById(R.id.iv_scaleUp);
        imageview_down = (ImageView) findViewById(R.id.iv_scaleDown);
        tv_compass.setOnClickListener(listen);
        tv_satellite.setOnClickListener(listen);
        tv_located.setOnClickListener(listen);
        imageview_up.setOnClickListener(listen);
        imageview_down.setOnClickListener(listen);
        initMapView();
        initLocation();
    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_scaleDown:
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                    break;
                case R.id.iv_scaleUp:
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                    break;
                case R.id.tv_compass:
                    // 指南针有没有显示:指南针是地图上的一个图标
                    boolean compassEnabled = mBaiduMap.getUiSettings().isCompassEnabled();
                    mBaiduMap.getUiSettings().setCompassEnabled(!compassEnabled);
                    break;
                case R.id.tv_located:
                    // 地图状态的设置：设置到定位的地方
                    moveToLocation();
                    break;
                case R.id.tv_satellite:
                    int mapType = mBaiduMap.getMapType();// 获取当前的地图类型
                    // 切换类型
                    mapType = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
                    // 卫星和普通的文字的显示
                    String msg = mapType == BaiduMap.MAP_TYPE_NORMAL ? "卫星" : "普通";
                    mBaiduMap.setMapType(mapType);
                    tv_satellite.setText(msg);
                    break;
            }
        }
    };

    public void moveToLocation() {
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mCurrentLocation)// 定位的位置
                .rotate(0)
                .overlook(0)
                .zoom(19)
                .build();
        // 更新状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 更新展示的地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    private void initMapView() {
        // 设置地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .zoom(19)// 3--21：默认的是12
                .overlook(0)// 俯仰的角度
                .rotate(0)// 旋转的角度
                .build();

        // 设置百度地图的设置信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)// 是否显示指南针
                .zoomGesturesEnabled(true)// 是否允许缩放手势
                .scaleControlEnabled(false)// 不显示比例尺
                .zoomControlsEnabled(false)// 不显示缩放的控件
                ;

        // 创建
        mMapView = new MapView(this, options);
        // 在布局上添加地图控件：0，代表第一位
        mMapFrame.addView(mMapView, 0);

        // 拿到地图的操作类(控制器：操作地图等都是使用这个)
        mBaiduMap = mMapView.getMap();
    }

    // 初始化定位相关
    private void initLocation() {

        // 前置：激活定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 第一步，初始化LocationClient类:LocationClient类必须在主线程中声明，需要Context类型的参数。
        mLocationClient = new LocationClient(this.getApplicationContext());

        // 第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置百度坐标类型，默认gcj02，会有偏差，bd9ll百度地图坐标类型，将无偏差的展示到地图上
        option.setIsNeedAddress(true);// 需要地址信息
        mLocationClient.setLocOption(option);

        // 第三步，实现BDLocationListener接口
        mLocationClient.registerLocationListener(mBDLocationListener);

        // 第四步，开始定位
        mLocationClient.start();

    }

    // 定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        // 获取到定位结果
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            // 如果没有拿到结果，重新请求：部分机型会失败
            if (bdLocation == null) {
                mLocationClient.requestLocation();
                return;
            }

            // 定位结果的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            // 定位的经纬度的类
            mCurrentLocation = new LatLng(latitude, longitude);
            mLocationAddr = bdLocation.getAddrStr();
            Log.i("TAG","定位的位置："+"，经纬度："+latitude+","+longitude);
            // 设置定位图层展示的数据
            MyLocationData data = new MyLocationData.Builder()

                    // 定位数据展示的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)// 定位精度的大小
                    .build();

            // 定位数据展示到地图上
            mBaiduMap.setMyLocationData(data);

            // 移动到定位的地方，在地图上展示定位的信息：位置
            // 做一个判断：第一次进入页面自动移动，其他时候点击按钮移动
            if (mIsFirst) {
                moveToLocation();
                mIsFirst = false;
            }
        }
    };
}
