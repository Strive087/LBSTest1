package com.example.a84045.lbstest1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CallMe extends AppCompatActivity implements View.OnClickListener {

    private ImageView callmeicon;

    private TextView callmemail;

    private TextView callmephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_me);
        callmeicon=findViewById(R.id.callme_icon);
        callmemail=findViewById(R.id.callme_mail);
        callmephone=findViewById(R.id.callme_phone);
        callmeicon.setOnClickListener(this);
        callmemail.setOnClickListener(this);
        callmephone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.callme_icon:
                Toast.makeText(this,"别摸我猪头",Toast.LENGTH_SHORT).show();
                break;
            case R.id.callme_mail:
                break;
            case R.id.callme_phone:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},1);
                }else{
                    Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15797716516"));
                    startActivity(intent);
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
                if(grantResults.length>0){
                    if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15797716516"));
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

}
