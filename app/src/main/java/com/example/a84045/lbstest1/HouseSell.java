package com.example.a84045.lbstest1;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HouseSell extends AppCompatActivity implements View.OnClickListener {

    public static final int TAKE_PHOTO =1;

    public static final int CHOOSE_PHOTO =2;

    private Uri imageUri;

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

    private PopupWindow popupWindow;

    private View rootView;

    Button take_photo;

    Button choose_from_album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_sell);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.housesell_photo1:
                getPopupWindow();
                break;
            case R.id.housesell_photo2:
            case R.id.housesell_photo3:
            case R.id.housesell_photo4:
            case R.id.take_photo:
                TakePhoto();
                popupWindow.dismiss();
                break;
            case R.id.choose_from_album:
                ChoosePhoto();
                popupWindow.dismiss();
                break;
            default:
                break;
        }
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

    private void TakePhoto(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HouseSell.this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            getTakePtoto();
        }
    }

    private void getTakePtoto(){
        File outputImage = new File(getExternalCacheDir(),"outputImage.jpg");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        int degree = getBitmapDegree(getExternalCacheDir()+"/outputImage.jpg");
                        Log.d("dafad",getExternalCacheDir()+"");
                        Bitmap bitmap1 = rotateBitmapByDegree(bitmap,degree);
                        housesell_photo1.setImageBitmap(bitmap1);
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
            housesell_photo1.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"Failed to get image",Toast.LENGTH_SHORT).show();
        }
    }

    private int getBitmapDegree(String path) {
        int degree = 0;//被旋转的角度
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
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
