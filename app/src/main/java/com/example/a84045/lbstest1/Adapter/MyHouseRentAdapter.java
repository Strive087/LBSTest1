package com.example.a84045.lbstest1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.R;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MyHouseRentAdapter extends BaseAdapter implements View.OnClickListener {

    private List<HouseRent> mList;
    private Context mContext;
    private Callback mCallback;

    public MyHouseRentAdapter(List<HouseRent> mList, Context mContext,Callback mCallback) {
        this.mList = mList;
        this.mContext = mContext;
        this.mCallback = mCallback;
    }


    @Override
    public int getCount() {
        // TODO 自动生成的方法存根
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO 自动生成的方法存根
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO 自动生成的方法存根
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HouseRent houseRent = mList.get(position);
        final ViewHolder viewHolder ;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.myhouserent_item,null);
            viewHolder = new ViewHolder();
            viewHolder.housename = convertView.findViewById(R.id.myrent_list_housename);
            viewHolder.description = convertView.findViewById(R.id.myrent_list_description);
            viewHolder.address = convertView.findViewById(R.id.myrent_list_address);
            viewHolder.username = convertView.findViewById(R.id.myrent_list_name);
            viewHolder.userphone = convertView.findViewById(R.id.myrent_list_phone);
            viewHolder.price = convertView.findViewById(R.id.myrent_list_price);
            viewHolder.update = convertView.findViewById(R.id.myrent_list_update);
            viewHolder.delete = convertView.findViewById(R.id.myrent_list_delete);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.housename.setText(houseRent.getHousename());
        viewHolder.description.setText(houseRent.getHousedescription());
        viewHolder.address.setText(houseRent.getHouseprovince()+houseRent.getHousecity()+houseRent.getHousedistrict()+houseRent.getHousestreet());
        viewHolder.username.setText(houseRent.getUsername());
        viewHolder.userphone.setText(houseRent.getUserphone());
        viewHolder.price.setText(houseRent.getHouseprice()+"元");
        viewHolder.update.setOnClickListener(this);
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(position);
        viewHolder.update.setTag(position);
        return convertView;
    }

    public interface Callback  {
        void click(View v ,HouseRent houseRent);
    }

    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        switch (view.getId()){
            case R.id.myrent_list_delete:
                deleteList(mList.get(tag).getHouseid());
                mList.remove(tag);
                notifyDataSetChanged();
                break;
            case R.id.myrent_list_update:
                HouseRent houseRent = mList.get(tag);
                mCallback.click(view,houseRent);
                break;
            default:
                break;
        }
    }

    class ViewHolder{
        TextView housename ;
        TextView description ;
        TextView address ;
        TextView username ;
        TextView userphone;
        TextView price;
        Button update;
        Button delete;
    }

    void deleteList(final long houseid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.post().url(Variable.host+"/deleteHouseRent").addParams("id",houseid+"")
                        .build().execute(new com.zhy.http.okhttp.callback.Callback() {
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
        }).start();
    }

}
