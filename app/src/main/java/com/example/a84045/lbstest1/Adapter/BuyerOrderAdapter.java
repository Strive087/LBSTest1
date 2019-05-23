package com.example.a84045.lbstest1.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a84045.lbstest1.Entity.HouseOrderEntity;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class BuyerOrderAdapter extends ArrayAdapter<HouseOrderEntity> {

    private int resourceId ;

    public BuyerOrderAdapter(Context context, int textViewResourceId, List<HouseOrderEntity> orderEntities){
        super(context,textViewResourceId,orderEntities);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        final HouseOrderEntity orderEntity = getItem(position);
        View view;
        final ViewHolder viewHolder ;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.housename = view.findViewById(R.id.buyer_list_housename);
            viewHolder.orderdate = view.findViewById(R.id.buyer_list_orderdate);
            viewHolder.address = view.findViewById(R.id.buyer_list_address);
            viewHolder.sellername = view.findViewById(R.id.buyer_list_name);
            viewHolder.sellerphone = view.findViewById(R.id.buyer_list_phone);
            viewHolder.price = view.findViewById(R.id.buyer_list_price);
            viewHolder.orderday = view.findViewById(R.id.buyer_list_orderday);
            viewHolder.orderstatu = view.findViewById(R.id.buyer_list_orderstatu);
            viewHolder.cancle = view.findViewById(R.id.buyer_list_cancel);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.housename.setText(orderEntity.getHousename());
        viewHolder.orderdate.setText(orderEntity.getOrderdate()+"");
        viewHolder.address.setText(orderEntity.getHouseprovince()+orderEntity.getHousecity()+orderEntity.getHousedistrict()+orderEntity.getHousestreet());
        viewHolder.sellername.setText(orderEntity.getSellername());
        viewHolder.sellerphone.setText(orderEntity.getSellerphone());
        viewHolder.price.setText(orderEntity.getOrderprice()+"元");
        viewHolder.orderday.setText(orderEntity.getOrderday()+"月");
        viewHolder.cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.cancle.setText("已取消");
                viewHolder.cancle.setClickable(false);
                viewHolder.orderstatu.setText("已取消订单");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils.post().url(Variable.host+"/updateHouseOrder").addParams("orderid",orderEntity.getOrderid()+"")
                                .addParams("orderstatu","-1").build().execute(new Callback() {
                            @Override
                            public Object parseNetworkResponse(Response response) throws Exception {
                                return null;
                            }

                            @Override
                            public void onError(Call call, Exception e) {

                            }

                            @Override
                            public void onResponse(Object response) {
                                Toast.makeText(getContext(),"已取消订单",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();

            }
        });
        if(orderEntity.getOrderstatu() == -1){
            viewHolder.orderstatu.setText("已取消订单");
            viewHolder.cancle.setText("已取消");
            viewHolder.cancle.setClickable(false);
        }
        if(orderEntity.getOrderstatu() == 0){
            viewHolder.orderstatu.setText("等待房东确认");
            viewHolder.cancle.setText("取消订单");
            viewHolder.cancle.setClickable(true);
        }
        if(orderEntity.getOrderstatu() == 1){
            viewHolder.orderstatu.setText("完成交易");
            viewHolder.cancle.setText("已完成");
            viewHolder.cancle.setClickable(false);
        }
        return view;
    }

    class ViewHolder{
        TextView housename ;
        TextView orderdate ;
        TextView address ;
        TextView sellername ;
        TextView sellerphone;
        TextView price;
        TextView orderday ;
        TextView orderstatu ;
        Button cancle ;
    }
}
