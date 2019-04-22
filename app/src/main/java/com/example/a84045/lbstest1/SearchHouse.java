package com.example.a84045.lbstest1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a84045.lbstest1.Adapter.SearchHouseAdapter;
import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SearchHouse extends AppCompatActivity implements SearchHouseAdapter.Callback {

    private SearchView mSearchView;

    private ListView mListView;

    private List<HouseRent> houseRentList;

    private SharedPreferences preferences;

    private Spinner spinnerprice;

    private Spinner spinnerarea;

    int lowprice = 0;

    int upprice = 0;

    int lowarea = 0;

    int uparea = 0;

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
        setContentView(R.layout.activity_search_house);
        mSearchView = findViewById(R.id.searchView);
        mListView =findViewById(R.id.searchListView);
        spinnerprice =findViewById(R.id.spinner_price);
        spinnerarea = findViewById(R.id.spinner_area);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSearchView.setIconified(false);
        mSearchView.setIconifiedByDefault(false);
        mListView.setTextFilterEnabled(true);
        mSearchView.setSubmitButtonEnabled(true);
        spinnerprice.setSelection(5,true);
        spinnerarea.setSelection(5,true);
        getAllHouseRent();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                SearchHouseAdapter adapter = (SearchHouseAdapter) mListView.getAdapter();
                if(adapter instanceof Filterable){
                    Filter filter = ((Filterable)adapter).getFilter();
                    if(s==null || s.length()==0){
                        filter.filter(null);
                    }else{
                        filter.filter(s);
                    }
                }
                return true;
            }
        });
        spinnerprice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    upprice = 999;
                }else if(i == 1){
                    lowprice = 1000;
                    upprice = 1999;
                }else if (i == 2){
                    lowprice = 2000;
                    upprice = 2999;
                }else if (i == 3){
                    lowprice = 3000;
                    upprice = 3999;
                }else if (i == 4){
                    lowprice = 4000;
                    upprice = 99999999;
                }else{
                    lowprice = 0;
                    upprice = 0;
                }
                getHouseRentByOption();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerarea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    uparea = 29;
                }else if(i == 1){
                    lowarea = 30;
                    uparea = 59;
                }else if (i == 2){
                    lowarea = 60;
                    uparea = 89;
                }else if (i == 3){
                    lowarea = 90;
                    uparea = 119;
                }else if (i == 4){
                    lowarea = 120;
                    uparea = 99999999;
                }else{
                    lowarea = 0;
                    uparea = 0;
                }
                getHouseRentByOption();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getHouseRentByOption(){
        if (lowarea ==0 &&lowprice==0 && upprice==0&&uparea==0)
            getAllHouseRent();
        else {
            OkHttpUtils.post().url(Variable.host+"/getAllHouseRentByOption")
                    .addParams("lowprice",lowprice+"").addParams("upprice",upprice+"")
                    .addParams("lowarea",lowarea+"").addParams("uparea",uparea+"")
                    .build().execute(new HouseRentCallback() {
                @Override
                public void onError(Call call, Exception e) {

                }

                @Override
                public void onResponse(List<HouseRent> response) {
                    houseRentList = response;
                    SearchHouseAdapter adapter = new SearchHouseAdapter(houseRentList,SearchHouse.this,SearchHouse.this);
                    mListView.setAdapter(adapter);
                }
            });
        }
    }

    public void getAllHouseRent(){
        OkHttpUtils.post().url(Variable.host+"/getAllHouseRent").build().execute(new HouseRentCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(List<HouseRent> response) {
                Log.d("sadf",response.size()+"");
                houseRentList = response;
                SearchHouseAdapter adapter = new SearchHouseAdapter(houseRentList,SearchHouse.this,SearchHouse.this);
                mListView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void click(View v ,HouseRent houseRent) {
        switch (v.getId()){
            case R.id.search_send:
                sendOrder(houseRent);
                break;
        }
    }

    private void sendOrder(final HouseRent houseRent){
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
                                    .addParams("buyerid",preferences.getString("id",""))
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
                                    Intent intent = new Intent(SearchHouse.this,HouseOrder.class);
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }

}
