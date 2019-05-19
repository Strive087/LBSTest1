package com.example.a84045.lbstest1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.a84045.lbstest1.Entity.User;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class UserInfo extends AppCompatActivity implements View.OnClickListener {

    private EditText username;

    private EditText usermail;

    private EditText userphone;

    private EditText userpassold;

    private EditText userpassnew;

    private RadioGroup usersex;

    private RadioButton usersex_info_man;

    private RadioButton usersex_info_woman;

    private Button commit;

    private Button username_update;

    private Button usermail_update;

    private Button userphone_update;

    private Button userpass_update;

    private Button usersex_update;

    private SharedPreferences preferences;

    private boolean exit = false;

    private Toolbar toolbar;

    public abstract class UserCallback extends Callback<User>
    {
        @Override
        public User parseNetworkResponse(Response response) throws IOException
        {
            String string = response.body().string();
            return GsonUtil.changeGsonToBean(string,User.class);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.username_update:
                getFocus(username);
                break;
            case R.id.usermail_update:
                getFocus(usermail);
                break;
            case R.id.userphone_update:
                getFocus(userphone);
                break;
            case R.id.usersex_update:
                usersex.setFocusable(true);
                usersex.setFocusableInTouchMode(true);
                usersex_info_man.setFocusable(true);
                usersex_info_man.setFocusableInTouchMode(true);
                usersex_info_woman.setFocusable(true);
                usersex_info_woman.setFocusableInTouchMode(true);
                usersex.requestFocus();
                break;
            case R.id.userpass_update:
                getFocus(userpassold);
                userpassold.setText("");
                userpassnew.setText("");
                userpassnew.setVisibility(View.VISIBLE);
                exit = true;
                break;
            case R.id.user_commit:
                commit();
                break;
            default:
                break;
        }
    }

    private void commit(){
        if(validate()){
            boolean sex = false;
            if(usersex.getCheckedRadioButtonId() == R.id.usersex_info_man){
                sex = true;
            }
            OkHttpUtils.post().url(Variable.host+"/updateuser").addParams("username",username.getText().toString())
                    .addParams("usermail",usermail.getText().toString()).addParams("userphone",userphone.getText().toString())
                    .addParams("userpassword",userpassnew.getText().toString()).addParams("usersex",sex+"")
                    .addParams("userid",preferences.getString("id","")).build().execute(new Callback() {
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
            Toast.makeText(this,"用户信息已保存",Toast.LENGTH_SHORT).show();
            if(exit){
                Intent intent = new Intent(this,Login.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public boolean validate() {
        boolean valid = true;
        String name = username.getText().toString();
        String email = usermail.getText().toString();
        String password = userpassnew.getText().toString();

        if (name.isEmpty() || name.length() < 2) {
            username.setError("at least 2 characters");
            valid = false;
        } else {
            username.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usermail.setError("enter a valid email address");
            valid = false;
        } else {
            usermail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            userpassnew.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            userpassnew.setError(null);
        }
        if(!userpassold.getText().toString().equals(preferences.getString("psd",""))){
            valid = false;
            userpassold.setError("与原先密码不一致");
            userpassold.setText("");
            userpassnew.setText("");
        }
        return valid;
    }

    private void getFocus(EditText usertext){
        usertext.setFocusable(true);
        usertext.setFocusableInTouchMode(true);
        usertext.requestFocus();
    }

    private void init(){
        username = findViewById(R.id.username_info);
        usermail = findViewById(R.id.usermail_info);
        userphone = findViewById(R.id.userphone_info);
        userpassold = findViewById(R.id.userpass_info_old);
        userpassnew = findViewById(R.id.userpass_info_new);
        usersex = findViewById(R.id.usersex_info);
        usersex_info_man = findViewById(R.id.usersex_info_man);
        usersex_info_woman=findViewById(R.id.usersex_info_woman);
        username_update = findViewById(R.id.username_update);
        usermail_update = findViewById(R.id.usermail_update);
        userphone_update = findViewById(R.id.userphone_update);
        usersex_update = findViewById(R.id.usersex_update);
        userpass_update = findViewById(R.id.userpass_update);
        commit = findViewById(R.id.user_commit);
        toolbar = findViewById(R.id.userinfo_toolbar);
        username_update.setOnClickListener(this);
        usermail_update.setOnClickListener(this);
        userphone_update.setOnClickListener(this);
        usersex_update.setOnClickListener(this);
        userpass_update.setOnClickListener(this);
        commit.setOnClickListener(this);
        ActionMenuView actionMenuView = (ActionMenuView) toolbar.findViewById(R.id.action_menu_view);
        getMenuInflater().inflate(R.menu.housesell_toolbar, actionMenuView.getMenu());
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.back:
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        OkHttpUtils.post().url(Variable.host+"/getuserbymail")
                .addParams("usermail",preferences.getString("mail",""))
                .build().execute(new UserCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(User response) {
                username.setText(response.getUsername());
                usermail.setText(response.getUsermail());
                userphone.setText(response.getUserphone());
                if(response.isUsersex()){
                    usersex.check(R.id.usersex_info_man);
                }else {
                    usersex.check(R.id.usersex_info_woman);
                }
                userpassold.setText(response.getUserpassword());
                userpassnew.setText(response.getUserpassword());
            }
        });
    }

}
