package com.example.a84045.lbstest1;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a84045.lbstest1.Adapter.Admin.RentAdapter;
import com.example.a84045.lbstest1.Adapter.Admin.UserAdapter;
import com.example.a84045.lbstest1.Entity.HouseOrderEntity;
import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.Entity.User;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class Manage extends AppCompatActivity implements View.OnClickListener{

    final static int ADD_SUCCESS = 1;

    private TextView usermanage;

    private TextView rentmanage;

    private TextView ordermange;

    private ListView managelist;

    private ListView managelistrent;

    private FrameLayout page1;

    private FrameLayout page2;

    private LinearLayout page3;

    private FloatingActionButton fab;

    private FloatingActionButton fabrent;

    public abstract class UserCallback extends Callback<List<User>>
    {
        @Override
        public List<User> parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            return GsonUtil.changeGsonToList(string,User.class);
        }
    }

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
        setContentView(R.layout.activity_manage);
        usermanage = findViewById(R.id.user_manage);
        rentmanage = findViewById(R.id.rent_manage);
        ordermange = findViewById(R.id.order_manage);
        managelist = findViewById(R.id.manage_list);
        managelistrent = findViewById(R.id.manage_list_rent);
        page1 = findViewById(R.id.manage_page1);
        page2 = findViewById(R.id.manage_page2);
        page3 = findViewById(R.id.manage_page3);
        fab = findViewById(R.id.fab);
        fabrent = findViewById(R.id.fab_rent);
        usermanage.setOnClickListener(this);
        rentmanage.setOnClickListener(this);
        ordermange.setOnClickListener(this);
        fab.setOnClickListener(this);
        fabrent.setOnClickListener(this);
        getuser();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.user_manage:
                getuser();
                usermanage.setBackgroundResource(R.color.colorPrimary);
                rentmanage.setBackgroundResource(R.color.colorUnConfirm);
                ordermange.setBackgroundResource(R.color.colorUnConfirm);
                page1.setVisibility(View.VISIBLE);
                page2.setVisibility(View.GONE);
                page3.setVisibility(View.GONE);
                break;
            case R.id.rent_manage:
                getRent();
                rentmanage.setBackgroundResource(R.color.colorPrimary);
                usermanage.setBackgroundResource(R.color.colorUnConfirm);
                ordermange.setBackgroundResource(R.color.colorUnConfirm);
                page2.setVisibility(View.VISIBLE);
                page1.setVisibility(View.GONE);
                page3.setVisibility(View.GONE);
                break;
            case R.id.order_manage:
                ordermange.setBackgroundResource(R.color.colorPrimary);
                usermanage.setBackgroundResource(R.color.colorUnConfirm);
                rentmanage.setBackgroundResource(R.color.colorUnConfirm);
                page3.setVisibility(View.VISIBLE);
                page1.setVisibility(View.GONE);
                page2.setVisibility(View.GONE);
                break;
            case R.id.fab:
                intent = new Intent(this,AdminAddUser.class);
                startActivityForResult(intent,ADD_SUCCESS);
                break;
            case R.id.fab_rent:
                intent = new Intent(this,HouseSell.class);
                intent.putExtra("manage",true);
                startActivityForResult(intent,ADD_SUCCESS);
            default:
                break;
        }
    }
    private void getuser(){
        OkHttpUtils.post().url(Variable.host+"/getalluser").build().execute(new UserCallback() {
            @Override
            public void onError(Call call, Exception e) {
                Log.d("Fafa",e.getMessage());
            }

            @Override
            public void onResponse(List<User> response) {
                UserAdapter adapter = new UserAdapter(response,Manage.this);
                managelist.setAdapter(adapter);
            }
        });
    }

    private void getRent(){
        OkHttpUtils.post().url(Variable.host+"/getAllHouseRent").build().execute(new HouseRentCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(List<HouseRent> response) {
                RentAdapter adapter = new RentAdapter(response,Manage.this);
                managelistrent.setAdapter(adapter);
            }
        });
    }

    private void getTotal(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ADD_SUCCESS){
            if(resultCode == RESULT_OK){
                getuser();
            }
            if (requestCode == 666){
                getRent();
            }
        }
    }
}
