package com.example.a84045.lbstest1.Adapter.Admin;

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
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RentAdapter extends BaseAdapter implements View.OnClickListener{

    private List<HouseRent> mList;
    private Context mContext;


    public RentAdapter(List<HouseRent> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        HouseRent houseRent = mList.get(tag);
        delete(houseRent.getHouseid());
        mList.remove(tag);
        notifyDataSetChanged();
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.searchhouse_item, null);
            viewHolder = new ViewHolder();
            viewHolder.searchohusename = convertView.findViewById(R.id.search_housename);
            viewHolder.searchdescription = convertView.findViewById(R.id.search_description);
            viewHolder.searchaddress = convertView.findViewById(R.id.search_address);
            viewHolder.searchname = convertView.findViewById(R.id.search_name);
            viewHolder.searchphone = convertView.findViewById(R.id.search_phone);
            viewHolder.searchprice = convertView.findViewById(R.id.search_price);
            viewHolder.searcharea = convertView.findViewById(R.id.search_area);
            viewHolder.searchshape = convertView.findViewById(R.id.search_shape);
            viewHolder.searchsend = convertView.findViewById(R.id.search_send);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.searchohusename.setText(houseRent.getHousename());
        viewHolder.searchdescription.setText(houseRent.getHousedescription());
        viewHolder.searchaddress.setText(houseRent.getHouseprovince() + houseRent.getHousecity() + houseRent.getHousedistrict() + houseRent.getHousestreet());
        viewHolder.searchname.setText(houseRent.getUsername());
        viewHolder.searchphone.setText(houseRent.getUserphone());
        viewHolder.searchprice.setText(houseRent.getHouseprice() + "");
        viewHolder.searcharea.setText(houseRent.getHousearea());
        viewHolder.searchshape.setText(houseRent.getHousearea());
        viewHolder.searchsend.setText("删除房源");
        viewHolder.searchsend.setOnClickListener(this);
        viewHolder.searchsend.setTag(position);
        return convertView;
    }

    class ViewHolder {
        TextView searchohusename;
        TextView searchdescription;
        TextView searchaddress;
        TextView searchname;
        TextView searchphone;
        TextView searchprice;
        TextView searcharea;
        TextView searchshape;
        Button searchsend;
    }

    public void delete(long id){
        OkHttpUtils.post().url(Variable.host+"/deleteHouseRent").addParams("id", id+"").build().execute(new Callback() {
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
