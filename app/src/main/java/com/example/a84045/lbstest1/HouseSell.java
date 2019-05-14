package com.example.a84045.lbstest1;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.Entity.User;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.BitmapUtil;
import com.google.gson.Gson;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citythreelist.CityBean;
import com.lljjcoder.style.citythreelist.ProvinceActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class HouseSell extends AppCompatActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    public static int NUM_PHOTO = 1;

    private Uri imageUri;

    private GeoCoder mCoder;

    Toolbar toolbar;

    EditText housesell_name;

    EditText housesell_description;

    EditText housesell_shape;

    EditText housesell_price;

    EditText housesell_area;

    ImageView housesell_photo1;

    ImageView housesell_photo2;

    ImageView housesell_photo3;

    ImageView housesell_photo4;

    String housesell_provice;

    String housesell_city;

    String housesell_district;

    String housesell_street;

    double housesell_longitude;

    double housesell_latitude;

    private PopupWindow popupWindow;

    private View rootView;

    Button housesell_commit;

    Button take_photo;

    Button choose_from_album;

    TextView select_area;

    EditText detailed_area;

    CheckBox select_location;

    String photoName ;

    String filename;

    ArrayList<String> listFilePath=new ArrayList<String>(){{
       add("null");add("null");add("null");add("null");
    }};

    private SharedPreferences preferences;

    public static boolean UPDATE_HOUSE_SELL = false;

    private HouseRent mhouseRent;

    public abstract class UserCallback extends Callback<User>
    {
        @Override
        public User parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            User user = new Gson().fromJson(string, User.class);
            return user;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_sell);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = findViewById(R.id.housesell_toolbar);
        housesell_name = findViewById(R.id.housesell_name);
        housesell_description = findViewById(R.id.housesell_discription);
        housesell_shape = findViewById(R.id.housesell_shape);
        housesell_price = findViewById(R.id.housesell_price);
        housesell_area = findViewById(R.id.housesell_area);
        housesell_photo1 = findViewById(R.id.housesell_photo1);
        housesell_photo2 = findViewById(R.id.housesell_photo2);
        housesell_photo3 = findViewById(R.id.housesell_photo3);
        housesell_photo4 = findViewById(R.id.housesell_photo4);
        select_area = findViewById(R.id.select_area);
        detailed_area = findViewById(R.id.detailed_area);
        select_location = findViewById(R.id.select_localtion);
        housesell_commit = findViewById(R.id.housesell_commit);
        housesell_commit.setOnClickListener(this);
        housesell_photo1.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setPopupWindow();
        }
        ActionMenuView actionMenuView = (ActionMenuView) toolbar.findViewById(R.id.action_menu_view);
        getMenuInflater().inflate(R.menu.housesell_toolbar, actionMenuView.getMenu());
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.back:
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        mCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (null != geoCodeResult && null != geoCodeResult.getLocation()) {
                    if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                        //没有检索到结果
                        return;
                    } else {
                        housesell_latitude = geoCodeResult.getLocation().latitude;
                        housesell_longitude = geoCodeResult.getLocation().longitude;
                    }
                }
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        };
        mCoder.setOnGetGeoCodeResultListener(listener);
        boolean isChecked = true;
        select_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    select_area.setOnClickListener(null);
                    detailed_area.setFocusable(false);
                    detailed_area.setFocusableInTouchMode(false);
                    housesell_provice = MainActivity.mlocaltion.getProvince();
                    housesell_city = MainActivity.mlocaltion.getCity();
                    housesell_district = MainActivity.mlocaltion.getDistrict();
                    housesell_street = MainActivity.mlocaltion.getStreet();
                    select_area.setText(housesell_provice+" "+ housesell_city+" "+housesell_district);
                    detailed_area.setText(housesell_street);
                    housesell_longitude = MainActivity.mlocaltion.getLongitude();
                    housesell_latitude = MainActivity.mlocaltion.getLatitude();
                }else{
                    select_area.setOnClickListener(HouseSell.this);
                    detailed_area.setFocusable(true);
                    detailed_area.setFocusableInTouchMode(true);
                }
            }
        });
        Intent intent = getIntent();
        if (intent.getSerializableExtra("houserent") != null){
            final HouseRent houseRent = (HouseRent) intent.getSerializableExtra("houserent");
            mhouseRent = houseRent;
            housesell_name.setText(houseRent.getHousename());
            housesell_description.setText(houseRent.getHousedescription());
            housesell_shape.setText(houseRent.getHouseshape());
            housesell_price.setText(houseRent.getHouseprice()+"");
            housesell_area.setText(houseRent.getHousearea());
            housesell_provice = houseRent.getHouseprovince();
            housesell_city = houseRent.getHousecity();
            housesell_district = houseRent.getHousedistrict();
            housesell_street = houseRent.getHousestreet();
            select_area.setText(houseRent.getHouseprovince()+" "+houseRent.getHousecity()+" "+houseRent.getHousedistrict());
            detailed_area.setText(houseRent.getHousestreet());
            UPDATE_HOUSE_SELL = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> photoList = new ArrayList<>();
                    photoList.add(houseRent.getHousephoto0());
                    photoList.add(houseRent.getHousephoto1());
                    photoList.add(houseRent.getHousephoto2());
                    photoList.add(houseRent.getHousephoto3());
                    for (String filename : photoList){
                        if(!filename.equals("null")){
                           OkHttpUtils.get().url(Variable.host+"/downloadImage")
                                    .addParams("usermail",preferences.getString("mail",""))
                                    .addParams("imageName",filename).build().execute(new BitmapCallback() {
                               @Override
                               public void onError(Call call, Exception e) {

                               }

                               @Override
                               public void onResponse(Bitmap response) {
                                   showSumPhoto(response);
                               }
                           });

                        }
                    }

                }
            }).start();
            select_location.setChecked(!isChecked);
        }else if (intent.getBooleanExtra("manage",false ) == true) {
            select_location.setVisibility(View.GONE);
        }else {
            select_location.setChecked(isChecked);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.housesell_photo1:
                NUM_PHOTO = 1;
                getPopupWindow();
                break;
            case R.id.housesell_photo2:
                NUM_PHOTO = 2;
                getPopupWindow();
                break;
            case R.id.housesell_photo3:
                NUM_PHOTO = 3;
                getPopupWindow();
                break;
            case R.id.housesell_photo4:
                NUM_PHOTO = 4;
                getPopupWindow();
                break;
            case R.id.take_photo:
                TakePhoto();
                popupWindow.dismiss();
                break;
            case R.id.choose_from_album:
                ChoosePhoto();
                popupWindow.dismiss();
                break;
            case R.id.select_area:
                SelectArea();
                break;
            case R.id.housesell_commit:
                CommitForm();
                break;
            default:
                break;
        }
    }


    private void CommitForm(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        filename = month+""+day+""+hour+""+minute;
        new AsyncTask<Void,Void,List<File>>(){
            @Override
            protected List<File> doInBackground(Void... voids) {
                List<File> files = new ArrayList<>();
                int num = 0;
                for(String path : listFilePath){
                    if(!path.equals("null")){
                        String suffixName = path.substring(path.lastIndexOf("."));
                        File file = new File(BitmapUtil.compressImage(path,getExternalCacheDir()+"/"+ preferences.getString("mail","")+"_photo"+num+suffixName));
                        files.add(file);
                        num++;
                    }
                }
                return files;
            }

            @Override
            protected void onPostExecute(final List<File> files) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int num = 0 ; files.size() > num ; num++){
                            String filename1 = filename+"_"+num+".jpg";
                            OkHttpUtils.post().addFile("file",filename1,files.get(num))
                                    .addParams("usermail",preferences.getString("mail","")).url(Variable.host+"/uploadImage").build().execute(new Callback() {
                                @Override
                                public Object parseNetworkResponse(Response response) throws Exception {
                                    return null;
                                }

                                @Override
                                public void onError(Call call, Exception e) {

                                }

                                @Override
                                public void onResponse(Object response) {

                                }
                            });
                        }
                    }
                }).start();
            }
        }.execute();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filename0="null";
                String filename1="null";
                String filename2="null";
                String filename3="null";
                if(!listFilePath.get(0).equals("null")){
                    filename0=filename+"_0.jpg";
                }
                if(!listFilePath.get(1).equals("null")){
                    filename1=filename+"_1.jpg";
                }
                if(!listFilePath.get(2).equals("null")){
                    filename2=filename+"_2.jpg";
                }
                if(!listFilePath.get(3).equals("null")){
                    filename3=filename+"_3.jpg";
                }
                String userid = preferences.getString("id","");
                String username = preferences.getString("name","");
                String userphone = preferences.getString("phone","");
                String usersex;
                if (preferences.getString("sex","").equals(true))
                    usersex = "1";
                else
                    usersex = "0";
                String houselatitude = housesell_latitude+"";
                String houselongtitude = housesell_longitude+ "";
                if (UPDATE_HOUSE_SELL){
                    if(filename0.equals("null"))
                        filename0 = mhouseRent.getHousephoto0();
                    if(filename1.equals("null"))
                        filename1 = mhouseRent.getHousephoto1();
                    if(filename2.equals("null"))
                        filename2 = mhouseRent.getHousephoto2();
                    if(filename3.equals("null"))
                        filename3 = mhouseRent.getHousephoto3();
                    Log.d("adfa",filename0);
                    OkHttpUtils.post().url(Variable.host+ "/updateHouseRent").addParams("housename",housesell_name.getText().toString())
                            .addParams("housephoto0",filename0).addParams("housephoto1",filename1).addParams("housephoto2",filename2)
                            .addParams("housephoto3",filename3).addParams("housedescription",housesell_description.getText().toString())
                            .addParams("houseprovince",housesell_provice).addParams("housecity",housesell_city).addParams("housedistrict",housesell_district)
                            .addParams("housestreet",housesell_street).addParams("houseshape",housesell_shape.getText().toString())
                            .addParams("housearea",housesell_area.getText().toString()).addParams("houseprice",housesell_price.getText().toString())
                            .addParams("houselatitude",houselatitude).addParams("houselongtitude",houselongtitude)
                            .addParams("userid",userid).addParams("username",username).addParams("houseid",mhouseRent.getHouseid()+"")
                            .addParams("userphone",userphone).addParams("usersex",usersex).build().execute(new Callback() {
                        @Override
                        public Object parseNetworkResponse(Response response) throws Exception {
                            return null;
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                        }

                        @Override
                        public void onResponse(Object response) {
                            Toast.makeText(HouseSell.this,"发布成功",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } else {
                    OkHttpUtils.post().url(Variable.host+ "/addHouseRent").addParams("housename",housesell_name.getText().toString())
                            .addParams("housephoto0",filename0).addParams("housephoto1",filename1).addParams("housephoto2",filename2)
                            .addParams("housephoto3",filename3).addParams("housedescription",housesell_description.getText().toString())
                            .addParams("houseprovince",housesell_provice).addParams("housecity",housesell_city).addParams("housedistrict",housesell_district)
                            .addParams("housestreet",housesell_street).addParams("houseshape",housesell_shape.getText().toString())
                            .addParams("housearea",housesell_area.getText().toString()).addParams("houseprice",housesell_price.getText().toString())
                            .addParams("houselatitude",houselatitude).addParams("houselongtitude",houselongtitude)
                            .addParams("userid",userid).addParams("username",username)
                            .addParams("userphone",userphone).addParams("usersex",usersex).build().execute(new Callback() {
                        @Override
                        public Object parseNetworkResponse(Response response) throws Exception {
                            return null;
                        }

                        @Override
                        public void onError(Call call, Exception e) {
                        }

                        @Override
                        public void onResponse(Object response) {
                            Toast.makeText(HouseSell.this,"发布成功",Toast.LENGTH_SHORT).show();
                            setResult(666);
                            finish();
                        }
                    });
                }
            }
        }).start();
    }

    private void SelectArea(){
        CityListLoader.getInstance().loadProData(this);
        Intent intent = new Intent(this, ProvinceActivity.class);
        startActivityForResult(intent, ProvinceActivity.RESULT_DATA);
    }

    private void TakePhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HouseSell.this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            getTakePtoto();
        }
    }

    private void getTakePtoto(){
        if(NUM_PHOTO == 1){
            photoName = "housesell_photo1.jpg";
        }else if(NUM_PHOTO == 2){
            photoName = "housesell_photo2.jpg";
        }else if(NUM_PHOTO == 3){
            photoName = "housesell_photo3.jpg";
        }else{
            photoName = "housesell_photo4.jpg";
        }
        File outputImage = new File(getExternalCacheDir(),photoName);
        try {
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(HouseSell.this,
                    "com.example.cameraalbumtest.fileprovider",outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    private void ChoosePhoto(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(albumIntent, CHOOSE_PHOTO);
        }
    }

    private void addFile(String path){
        if(NUM_PHOTO == 1){
            listFilePath.set(0,path);
        }else if(NUM_PHOTO == 2 ){
            listFilePath.set(1,path);
        }else if(NUM_PHOTO == 3 ){
            listFilePath.set(2,path);
        }else {
            listFilePath.set(3,path);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        int degree = BitmapUtil.getRotateAngle(getExternalCacheDir()+"/"+photoName);
                        Bitmap bitmap1 = BitmapUtil.setRotateAngle(degree,bitmap);
                        addFile(getExternalCacheDir()+"/"+photoName);
                        showSumPhoto(bitmap1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case ProvinceActivity.RESULT_DATA:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    CityBean province = data.getParcelableExtra("province");
                    CityBean city = data.getParcelableExtra("city");
                    CityBean area = data.getParcelableExtra("area");
                    housesell_provice = province.getName();
                    housesell_city = city.getName();
                    housesell_district = area.getName();
                    housesell_street = detailed_area.getText().toString();
                    select_area.setText(province.getName()+" "+city.getName()+" "+area.getName());
                    mCoder.geocode(new GeoCodeOption()
                            .city(city.getName())
                            .address(area.getName()));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent("android.intent.acton.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,CHOOSE_PHOTO);
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getTakePtoto();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.download.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads//public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            int degree = BitmapUtil.getRotateAngle(imagePath);
            Bitmap bitmap1 = BitmapUtil.setRotateAngle(degree,bitmap);
            addFile(imagePath);
            showSumPhoto(bitmap1);
        }else{
            Toast.makeText(this,"Failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    private void showSumPhoto(Bitmap bitmap){
        switch (NUM_PHOTO){
            case 1:
                housesell_photo1.setImageBitmap(bitmap);
                if(housesell_photo2.getVisibility() != View.VISIBLE){
                    housesell_photo2.setVisibility(View.VISIBLE);
                    housesell_photo2.setOnClickListener(this);
                }
                break;
            case 2:
                housesell_photo2.setImageBitmap(bitmap);
                if(housesell_photo3.getVisibility() != View.VISIBLE){
                    housesell_photo3.setVisibility(View.VISIBLE);
                    housesell_photo3.setOnClickListener(this);
                }
                break;
            case 3:
                housesell_photo3.setImageBitmap(bitmap);
                if(housesell_photo4.getVisibility() != View.VISIBLE){
                    housesell_photo4.setVisibility(View.VISIBLE);
                    housesell_photo4.setOnClickListener(this);
                }
                break;
            default:
                housesell_photo4.setImageBitmap(bitmap);
                break;
        }
    }

    private void setPopupWindow(){
        rootView = findViewById(R.id.housesell_root);
        popupWindow = new PopupWindow(getApplicationContext());
        popupWindow=new PopupWindow(this);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.pop_animation);
        View view = findViewById(R.id.housesell_root);
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    private void getPopupWindow(){
        View contentView= LayoutInflater.from(HouseSell.this).inflate(R.layout.activity_popupphoto,null);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(rootView,Gravity.BOTTOM,0,0);
        take_photo = contentView.findViewById(R.id.take_photo);
        choose_from_album = contentView.findViewById(R.id.choose_from_album);
        take_photo.setOnClickListener(this);
        choose_from_album.setOnClickListener(this);
    }
}
