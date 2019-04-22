package com.example.a84045.lbstest1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.a84045.lbstest1.Adapter.MyHouseRentAdapter;
import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MyhouseRent extends AppCompatActivity implements MyHouseRentAdapter.Callback, OnItemClickListener {

    public abstract class HouseRentCallback extends Callback<List<HouseRent>>
    {
        @Override
        public List<HouseRent> parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            return GsonUtil.changeGsonToList(string,HouseRent.class);
        }
    }

    private SharedPreferences preferences;

    private ListView myrentlist;

    private MyHouseRentAdapter mAdapter;

    private List<HouseRent> houseRentList;

    public static int REQUEST_HOUSE_REMT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myhouse_rent);
        myrentlist= findViewById(R.id.myrent_list);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        getHouseRent();
    }

    public void getHouseRent(){
        OkHttpUtils.post().url(Variable.host+"/getAllHouseRentByUserid")
                .addParams("userid",preferences.getString("id", ""))
                .build().execute(new HouseRentCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(List<HouseRent> response) {
                houseRentList = response;
                mAdapter = new MyHouseRentAdapter(houseRentList,MyhouseRent.this,MyhouseRent.this);
                myrentlist.setAdapter(mAdapter);
                myrentlist.setOnItemClickListener(MyhouseRent.this);
            }
        });
    }

    @Override
    public void click(View v ,HouseRent houseRent) {
        switch (v.getId()){
            case R.id.myrent_list_update:
                Intent intent = new Intent(this,HouseSell.class);
                intent.putExtra("houserent",houseRent);
                startActivityForResult(intent,REQUEST_HOUSE_REMT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.e("整体item----->", position + "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK)
                    getHouseRent();
                break;
        }
    }
}
