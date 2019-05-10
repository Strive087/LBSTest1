package com.example.a84045.lbstest1;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.a84045.lbstest1.Global.Variable;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class AdminAddUser extends AppCompatActivity {

    EditText nameText;

    EditText emailText;

    EditText passwordText;

    EditText phoneText;

    RadioGroup sexChoose;

    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);
        nameText = findViewById(R.id.add_name);
        emailText = findViewById(R.id.add_email);
        passwordText = findViewById(R.id.add_password);
        phoneText = findViewById(R.id.add_phone);
        sexChoose =findViewById(R.id.add_sex);
        signupButton = findViewById(R.id.add_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup(){
        final String name = nameText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        final String phone = phoneText.getText().toString();
        final Boolean sex;
        if(sexChoose.getCheckedRadioButtonId() == R.id.addWoman){
            sex = false;
        }else{
            sex = true;
        }
        OkHttpUtils.post().url(Variable.host+"/adduser").addParams("usermail",email)
                .addParams("username",name).addParams("userphone",phone)
                .addParams("userpassword",password).addParams("usersex",sex.toString())
                .build().execute(new Callback() {
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
