package com.example.a84045.lbstest1;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a84045.lbstest1.Adapter.Admin.UserAdapter;
import com.example.a84045.lbstest1.Entity.HouseOrderEntity;
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

    private TextView usermanage;

    private TextView rentmanage;

    private TextView ordermange;

    private ListView managelist;

    private FloatingActionButton fab;

    public abstract class UserCallback extends Callback<List<User>>
    {
        @Override
        public List<User> parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            Log.d("Fafa",string);
            return GsonUtil.changeGsonToList(string,User.class);
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
        fab = findViewById(R.id.fab);
        usermanage.setOnClickListener(this);
        rentmanage.setOnClickListener(this);
        ordermange.setOnClickListener(this);
        fab.setOnClickListener(this);
        getuser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_manage:
                getuser();
                usermanage.setBackgroundResource(R.color.colorPrimary);
                rentmanage.setBackgroundResource(R.color.colorUnConfirm);
                ordermange.setBackgroundResource(R.color.colorUnConfirm);
                break;
            case R.id.rent_manage:
                rentmanage.setBackgroundResource(R.color.colorPrimary);
                usermanage.setBackgroundResource(R.color.colorUnConfirm);
                ordermange.setBackgroundResource(R.color.colorUnConfirm);
                break;
            case R.id.order_manage:
                ordermange.setBackgroundResource(R.color.colorPrimary);
                usermanage.setBackgroundResource(R.color.colorUnConfirm);
                rentmanage.setBackgroundResource(R.color.colorUnConfirm);
                break;
            case R.id.fab:
                Intent intent = new Intent(this,Signup.class);
                startActivity(intent);
                break;
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

}
