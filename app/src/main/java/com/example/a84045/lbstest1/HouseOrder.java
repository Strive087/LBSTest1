package com.example.a84045.lbstest1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.a84045.lbstest1.Adapter.BuyerOrderAdapter;
import com.example.a84045.lbstest1.Adapter.SellerOrderAdapter;
import com.example.a84045.lbstest1.Entity.HouseOrderEntity;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class HouseOrder extends AppCompatActivity implements View.OnClickListener{



    public abstract class HouseOrderCallback extends Callback<List<HouseOrderEntity>>
    {
        @Override
        public List<HouseOrderEntity> parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            return GsonUtil.changeGsonToList(string,HouseOrderEntity.class);
        }
    }

    private TextView buyerorder;

    private TextView sellerorder;

    private ListView orderlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_order);
        buyerorder = findViewById(R.id.buyer_order);
        sellerorder = findViewById(R.id.seller_order);
        buyerorder.setOnClickListener(this);
        sellerorder.setOnClickListener(this);
        getBuyerOrder();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buyer_order:
                buyerorder.setBackgroundResource(R.color.colorPrimary);
                sellerorder.setBackgroundResource(R.color.colorUnConfirm);
                getBuyerOrder();
                break;
            case R.id.seller_order:
                sellerorder.setBackgroundResource(R.color.colorPrimary);
                buyerorder.setBackgroundResource(R.color.colorUnConfirm);
                getSellerOrder();
                break;
            default:
                break;
        }
    }

    public void getBuyerOrder(){
        OkHttpUtils.post().url(Variable.host+"/getHouseOrderByBuyerid").addParams("buyerid",Variable.user.getUserid()+"")
                .build().execute(new HouseOrderCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(List<HouseOrderEntity> response) {
                BuyerOrderAdapter adapter = new BuyerOrderAdapter(HouseOrder.this,R.layout.buyerorder_item,response);
                orderlist= findViewById(R.id.order_list);
                orderlist.setAdapter(adapter);
                Log.d("dasfa","Sdf");
            }
        });
    }

    public void getSellerOrder(){
        OkHttpUtils.post().url(Variable.host+"/getHouseOrderBySellerid").addParams("sellerid",Variable.user.getUserid()+"")
                .build().execute(new HouseOrderCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(List<HouseOrderEntity> response) {
                SellerOrderAdapter adapter = new SellerOrderAdapter(HouseOrder.this,R.layout.sellerorder_item,response);
                orderlist= findViewById(R.id.order_list);
                orderlist.setAdapter(adapter);
            }
        });
    }

}
