package com.example.a84045.lbstest1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.a84045.lbstest1.Entity.HouseRent;
import com.example.a84045.lbstest1.R;

import java.util.ArrayList;
import java.util.List;

public class SearchHouseAdapter extends BaseAdapter implements View.OnClickListener, Filterable {

    private List<HouseRent> mList;
    private Context mContext;
    private Callback mCallback;
    private MyFilter mFilter;
    private List<HouseRent> values = null;
    private Object mLock = new Object();


    public SearchHouseAdapter(List<HouseRent> mList, Context mContext, Callback mCallback) {
        this.mList = mList;
        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        HouseRent houseRent = mList.get(tag);
        mCallback.click(view, houseRent);
    }

    public interface Callback {
        void click(View v, HouseRent houseRent);
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

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (values == null) {
                synchronized (mLock) {
                    values = new ArrayList<HouseRent>(mList);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    List<HouseRent> list1 = new ArrayList<HouseRent>(values);
                    results.values = list1;
                    results.count = list1.size();
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();
                List<HouseRent> values1 = values;
                int count = values1.size();
                List<HouseRent> newValues = new ArrayList<HouseRent>(count);
                for (HouseRent value : values1) {
                    String title = value.getHousename().toLowerCase();
                    if (title.indexOf(prefixString) != -1) {
                        newValues.add(value);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mList = (List<HouseRent>) results.values;
            if(results.count>0){
                notifyDataSetChanged();
            }else{
                notifyDataSetInvalidated();
            }
        }
    }
}
