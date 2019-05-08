package com.example.a84045.lbstest1.Adapter.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a84045.lbstest1.Entity.User;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.R;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UserAdapter extends BaseAdapter implements View.OnClickListener{

    private List<User> mList;
    private Context mContext;

    public UserAdapter(List<User> mList, Context mContext){
        this.mList = mList;
        this.mContext = mContext;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final User user = mList.get(i);
        final ViewHolder viewHolder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.user_manage_item,null);
            viewHolder = new ViewHolder();
            viewHolder.usermail = view.findViewById(R.id.user_mail_manage);
            viewHolder.username = view.findViewById(R.id.user_name_manage);
            viewHolder.usersex = view.findViewById(R.id.user_sex_manage);
            viewHolder.userphone = view.findViewById(R.id.user_phone_manage);
            viewHolder.userpassword = view.findViewById(R.id.user_password_manage);
            viewHolder.delete = view.findViewById(R.id.user_delete_manage);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.usermail.setText(user.getUsermail());
        viewHolder.username.setText(user.getUsername());
        viewHolder.usersex.setText(user.isUsersex()+"");
        viewHolder.userphone.setText(user.getUserphone());
        viewHolder.userpassword.setText(user.getUserpassword());
        viewHolder.delete.setOnClickListener(this);
        viewHolder.delete.setTag(i);
        return view;
    }


    @Override
    public void onClick(View view) {
        int tag = (int) view.getTag();
        switch (view.getId()){
            case R.id.user_delete_manage:
                deleteList(mList.get(tag).getUsermail());
                mList.remove(tag);
                notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    class ViewHolder{
        TextView username ;
        TextView usermail ;
        TextView usersex;
        TextView userphone ;
        TextView userpassword;
        ImageView delete;
    }

    void deleteList(final String usermail){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils.post().url(Variable.host+"/deleteuser").addParams("usermail",usermail)
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
