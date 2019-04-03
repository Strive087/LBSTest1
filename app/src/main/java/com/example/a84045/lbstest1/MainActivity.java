package com.example.a84045.lbstest1;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public LocationClient mLocationClient;

    private MapView mapView;

    private BaiduMap baiduMap;

    private boolean goBackLocation = true;

    private MyOrientationListener myOrientationListener;

    private float mLastX;

    private BDLocation mlocaltion;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;

    private long firstPressedTime;

    private PopupWindow popupWindow;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_main);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissoins = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissoins,1);
        }else {
            requestLocation();
        }

    }

    private void setPopupWindow(){
        popupWindow = new PopupWindow(getApplicationContext());
        popupWindow=new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        final Button btn = findViewById(R.id.popup_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView= LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_popuphouse,null);
                popupWindow.setContentView(contentView);
                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.showAsDropDown(btn);
                Log.d("adfasf","afasdfasf");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.message:
                break;
            default:
                break;
        }
        return true;
    }

    private void initdb(){
        LitePal.deleteAll(SellerInfo.class);
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setName("zhuduanlei");
        sellerInfo.setLatitude(28.662684);
        sellerInfo.setLongitude(115.807698);
        sellerInfo.save();
        SellerInfo sellerInfo1 = new SellerInfo();
        sellerInfo1.setName("liwenyue");
        sellerInfo1.setLatitude(28.663548);
        sellerInfo1.setLongitude(115.807786);
        sellerInfo1.save();
        SellerInfo sellerInfo2 = new SellerInfo();
        sellerInfo2.setName("hahahah");
        sellerInfo2.setLatitude(28.663648);
        sellerInfo2.setLongitude(115.801086);
        sellerInfo2.save();
        SellerInfo sellerInfo3 = new SellerInfo();
        sellerInfo3.setName("hehehhe");
        sellerInfo3.setLatitude(28.664548);
        sellerInfo3.setLongitude(115.801786);
        sellerInfo3.save();
    }

    private void houseinfo(){
        List<SellerInfo> sellerInfos = LitePal.findAll(SellerInfo.class);
        for (SellerInfo sellerInfo : sellerInfos){
            displayhouse(sellerInfo.getLatitude(),sellerInfo.getLongitude());
        }
    }

    private void displayhouse(double latitude,double longitude){
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.house);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point) //必传参数
                .icon(bitmap) //必传参数
                .draggable(true)
                //设置平贴地图，在地图中双指下拉查看效果
                .flat(true)
                .alpha(0.5f);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }


    public void setInit() {
        mapView = (MapView)findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        mapView.showZoomControls(false);
        Button button = (Button) findViewById(R.id.lwy_button);
        button.setOnClickListener(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar.setNavigationIcon(R.drawable.person1);
        setPopupWindow();
    }

    private void initMap() {
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//默认显示普通地图
        baiduMap.setMyLocationEnabled(true);// 开启定位图层
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        initLocation();//配置定位SDK参数
        mLocationClient.registerLocationListener(new MyLocationListener());    //注册监听函数
        mLocationClient.start();//开启定位
        mLocationClient.requestLocation();//图片点击事件，回到定位点
    }

    public void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//可选，coorType - 取值有3个： 返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系 ：bd09ll
        Log.e("获取地址信息设置", option.getAddrType());//获取地址信息设置
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true); // 是否打开gps进行定位
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setScanSpan(1000);//可选，设置的扫描间隔，单位是毫秒，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        Log.e("获取设置的Prod字段值", option.getProdName());
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setNeedDeviceDirect(true);//在网络定位时，是否需要设备方向- true:需要 ; false:不需要。默认为false
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null,0,0);
        baiduMap.setMyLocationConfiguration(configuration);
    }

    private void requestLocation(){
        setInit();
        initMap();
        initMyOrien();
        initdb();
        houseinfo();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent =new Intent(MainActivity.this,HouseInfo.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private void initMyOrien() {
        //方向传感器
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mLastX = x;
                navigateTo(mlocaltion);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位的允许
        baiduMap.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
            //开启方向传感器
            myOrientationListener.star();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //停止方向传感器
        myOrientationListener.stop();
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
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    private void navigateTo (BDLocation bdLocation){
        if(bdLocation != null){
            if(goBackLocation){
                LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(update);
                MapStatusUpdate update1 =  MapStatusUpdateFactory.zoomTo(19f);
                baiduMap.animateMapStatus(update1);
                goBackLocation = false;
            }
            MyLocationData.Builder locationBuilder = new MyLocationData.Builder()
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .accuracy(bdLocation.getRadius())
                    .direction(mLastX);
            MyLocationData locationData = locationBuilder.build();
            baiduMap.setMyLocationData(locationData);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lwy_button:
                goBackLocation = true;
                navigateTo(mlocaltion);
                break;
            default:
                break;
        }

    }

    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation){
                mlocaltion = location;
                navigateTo(location);
            }
        }
    }


}
