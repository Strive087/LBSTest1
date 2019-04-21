package com.example.a84045.lbstest1;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
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
import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.DataUtil;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public LocationClient mLocationClient;

    private MapView mapView;

    private BaiduMap baiduMap;

    private boolean goBackLocation = true;

    private MyOrientationListener myOrientationListener;

    private float mLastX;

    public static BDLocation mlocaltion;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;

    private long firstPressedTime;

    private PopupWindow popupWindow;

    private View rootView;

    private NavigationView navigationView;

    private String housemasterphone;

    private SharedPreferences preferences;

    public abstract class HouseRentCallback extends Callback<List<HouseRent>>
    {
        @Override
        public List<HouseRent> parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            return GsonUtil.changeGsonToList(string,HouseRent.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        rootView = findViewById(R.id.root_main);
        popupWindow = new PopupWindow(getApplicationContext());
        popupWindow=new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation);
        View view = findViewById(R.id.root_main);
        popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
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

    private void houseinfo(){
        OkHttpUtils.post().url(Variable.host+"/getAllHouseRent").build().execute(new HouseRentCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }
            @Override
            public void onResponse(List<HouseRent> response) {
                for (HouseRent houseRent : response){
                    if(response != null && mlocaltion != null)
                    if (Math.pow(houseRent.getHouselongtitude()-mlocaltion.getLongitude(),2)+
                            Math.pow(houseRent.getHouselatitude()-mlocaltion.getLatitude(),2) <= 0.01){
                        Bundle bundle = new Bundle();
                        DataUtil data = new DataUtil();
                        data.setHouseRent(houseRent);
                        bundle.putSerializable("HouseRent",data);
                        displayhouse(houseRent.getHouselatitude(),houseRent.getHouselongtitude(),bundle);
                    }
                }
            }
        });

    }

    private void displayhouse(double latitude,double longitude,Bundle bundle){
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.house);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point) //必传参数
                .scaleX(0.2f)
                .scaleY(0.2f)
                .zIndex(9)
                .icon(bitmap) //必传参数
                .extraInfo(bundle);
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
        mToolbar.setNavigationIcon(R.mipmap.person);
        mToolbar.setContentInsetStartWithNavigation(15);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconPadding(30);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setPopupWindow();
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent=null;
                switch (menuItem.getItemId()){
                    case R.id.nav_rent:
                        intent = new Intent(getApplicationContext(),HouseSell.class);
                        break;
                    case R.id.nav_order:
                        intent = new Intent(getApplicationContext(),HouseOrder.class);
                        break;
                    case R.id.nav_sell:
                        intent = new Intent(getApplicationContext(),MyhouseRent.class);
                        break;
                    default:
                        intent = new Intent(getApplicationContext(),CallMe.class);
                        break;
                }
                startActivity(intent);
                return true;
            }
        });
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
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                DisplayMetrics metrics =new DisplayMetrics();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                }
                final int height = metrics.heightPixels;
                View contentView= LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_popuphouse,null);
                popupWindow.setContentView(contentView);
                popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(height*3/5);
                popupWindow.showAtLocation(rootView,Gravity.BOTTOM,0,0);
                houseRentInfo(marker,contentView);
                return true;
            }
        });
    }

    private void houseRentInfo(Marker marker,View contentView){
        Bundle bundle = marker.getExtraInfo();
        DataUtil data = (DataUtil) bundle.get("HouseRent");
        HouseRent houseRent = data.getHouseRent();
        TextView housename = contentView.findViewById(R.id.house_name);
        housename.setText(houseRent.getHousename()+":");
        TextView housedescription = contentView.findViewById(R.id.house_description);
        housedescription.setText(houseRent.getHousedescription());
        TextView houseshape = contentView.findViewById(R.id.house_shape);
        houseshape.setText(houseRent.getHouseshape());
        TextView houseprice = contentView.findViewById(R.id.house_price);
        houseprice.setText(houseRent.getHouseprice()+" 元");
        TextView housearea = contentView.findViewById(R.id.house_area);
        housearea.setText(houseRent.getHousearea()+" 平方米");
        TextView housemaster = contentView.findViewById(R.id.house_master);
        if(houseRent.getUsersex().equals("1") || houseRent.getUsersex().equals("true")){
            housemaster.setText(houseRent.getUsername()+" (男)");
        }else{
            housemaster.setText(houseRent.getUsername()+" (女)");
        }
        final ImageView housephoto0 = contentView.findViewById(R.id.house_photo0);
        final ImageView housephoto1 = contentView.findViewById(R.id.house_photo1);
        final ImageView housephoto2 = contentView.findViewById(R.id.house_photo2);
        final ImageView housephoto3 = contentView.findViewById(R.id.house_photo3);
        String mail = preferences.getString("mail","");
        if(!houseRent.getHousephoto0().equals("null")){
            OkHttpUtils.get().url(Variable.host+"/downloadImage").addParams("usermail",mail)
                    .addParams("imageName",houseRent.getHousephoto0()).build().execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(Bitmap bitmap) {
                            housephoto0.setImageBitmap(bitmap);
                        }
                    });
            if(!houseRent.getHousephoto1().equals("null")){
                OkHttpUtils.get().url(Variable.host+"/downloadImage").addParams("usermail",mail)
                        .addParams("imageName",houseRent.getHousephoto1()).build().execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        housephoto1.setImageBitmap(bitmap);
                    }
                });
                if(!houseRent.getHousephoto2().equals("null")){
                    OkHttpUtils.get().url(Variable.host+"/downloadImage").addParams("usermail",mail)
                            .addParams("imageName",houseRent.getHousephoto2()).build().execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(Bitmap bitmap) {
                            housephoto2.setImageBitmap(bitmap);
                        }
                    });
                    if(!houseRent.getHousephoto3().equals("null")){
                        OkHttpUtils.get().url(Variable.host+"/downloadImage").addParams("usermail",mail)
                                .addParams("imageName",houseRent.getHousephoto3()).build().execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(Bitmap bitmap) {
                                housephoto3.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            }
        }

        Button housephone = contentView.findViewById(R.id.house_phone);
        housemasterphone = houseRent.getUserphone();
        Button sendorder = contentView.findViewById(R.id.send_order);
        housephone.setOnClickListener(this);
        sendorder.setOnClickListener(this);
        Variable.houseRent = houseRent;
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
        if (baiduMap != null){
            baiduMap.setMyLocationEnabled(true);
        }
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
                MapStatusUpdate update1 =  MapStatusUpdateFactory.zoomTo(18f);
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
            case 2:
                if(grantResults.length>0){
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+housemasterphone));
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.message:
                break;
            case R.id.search:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lwy_button:
                goBackLocation = true;
                navigateTo(mlocaltion);
                break;
            case R.id.house_phone:
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},2);
                }else{
                    Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+housemasterphone));
                    startActivity(intent);
                }
                break;
            case R.id.send_order:
                sendOrder();
                break;
            default:
                break;
        }

    }

    private void sendOrder(){
        EditText et = new EditText(this);
        final EditText et1 = et;
        new AlertDialog.Builder(this)
                .setTitle("输入您要居住的月数").setView(et1)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et1.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "内容不能为空！" + input, Toast.LENGTH_LONG).show();
                        }
                        else {
                            HouseRent houseRent = Variable.houseRent;
                            Date d = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String orderdate = sdf.format(d);
                            String orderprice = (Float)(Float.parseFloat(input) * houseRent.getHouseprice()) + "";
                            OkHttpUtils.post().url(Variable.host+"/addHouseOrder").addParams("orderdate",orderdate)
                                    .addParams("orderprice",orderprice).addParams("orderday",input)
                                    .addParams("orderstatu","0").addParams("houseid",houseRent.getHouseid()+"")
                                    .addParams("housename",houseRent.getHousename()).addParams("houseprovince",houseRent.getHouseprovince())
                                    .addParams("housecity",houseRent.getHousecity()).addParams("housedistrict",houseRent.getHousedistrict())
                                    .addParams("housestreet",houseRent.getHousestreet()).addParams("sellerid",houseRent.getUserid()+"")
                                    .addParams("sellerphone",houseRent.getUserphone()).addParams("sellername",houseRent.getUsername())
                                    .addParams("buyerid",preferences.getString("userid",""))
                                    .addParams("buyername",preferences.getString("name",""))
                                    .addParams("buyerphone",preferences.getString("phone","")).build().execute(new Callback() {
                                @Override
                                public Object parseNetworkResponse(Response response) throws Exception {
                                    return null;
                                }

                                @Override
                                public void onError(Call call, Exception e) {

                                }

                                @Override
                                public void onResponse(Object response) {
                                    Intent intent = new Intent(MainActivity.this,HouseOrder.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation){
                if (location.getCity() != null)
                    mlocaltion = location;
                navigateTo(location);
                houseinfo();
            }
        }
    }


}
