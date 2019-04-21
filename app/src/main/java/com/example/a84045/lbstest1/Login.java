package com.example.a84045.lbstest1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a84045.lbstest1.Entity.User;
import com.example.a84045.lbstest1.Global.Variable;
import com.example.a84045.lbstest1.Util.GsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;

import okhttp3.Response;


public class Login extends AppCompatActivity {

    private static final int GET_RESPONSE = 1;

    private static final int GET_USER = 2;

    private static final int REQUEST_SIGNUP = 0;

    EditText emailText;

    EditText passwordText;

    Button loginButton;

    TextView signupLink;

    ProgressBar progressBar;

    CheckBox savePsd;

    private SharedPreferences.Editor editor;

    private SharedPreferences preferences;

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case GET_RESPONSE:
                    Bundle data = message.getData();
                    String responseData = data.getString("response");
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                }
                            }, 1000);
                    if(responseData.equals("true")){
                        onLoginSuccess();
                    } else {
                        onLoginFailed();
                    }
                    break;
                case GET_USER:
                    Bundle data1 = message.getData();
                    String responseData1 = data1.getString("responseData");
                    User user = GsonUtil.changeGsonToBean(responseData1,User.class);
                    editor = preferences.edit();
                    editor.putBoolean("savepsd",savePsd.isChecked());
                    editor.putString("name",user.getUsername());
                    editor.putString("mail",user.getUsermail());
                    editor.putString("psd",user.getUserpassword());
                    editor.putString("phone",user.getUserphone());
                    editor.putString("sex",user.isUsersex()+"");
                    editor.putString("id",user.getUserid()+"");
                    editor.apply();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        emailText = findViewById(R.id.login_email);
        passwordText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        progressBar = findViewById(R.id.login_progress);
        savePsd = findViewById(R.id.save_psd);
        boolean isSavePsd = preferences.getBoolean("savepsd",false);
        if (isSavePsd){
            String mail = preferences.getString("mail","");
            String psd = preferences.getString("psd","");
            emailText.setText(mail);
            passwordText.setText(psd);
            savePsd.setChecked(true);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Signup.class);
                startActivityForResult(intent,REQUEST_SIGNUP);
            }
        });
    }

    public void login(){
        if(!validate()){
            onLoginFailed();
            return;
        }
        loginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpUtils.post().url(Variable.host+"/verifypassword")
                            .addParams("usermail",email).addParams("password",password).build().execute();
                    String responseData = response.body().string();
                    Log.i("aff",responseData);
                    Message message = new Message();
                    message.what = GET_RESPONSE;
                    Bundle data = new Bundle();
                    data.putString("response",responseData);
                    message.setData(data);
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_SIGNUP){
            if(resultCode != RESULT_OK){
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpUtils.post().url(Variable.host+"/getuserbymail").addParams("usermail",emailText.getText().toString()).build().execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    message.what = GET_USER;
                    Bundle data = new Bundle();
                    data.putString("responseData" , responseData);
                    message.setData(data);
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Toast.makeText(getBaseContext(),"login success",Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
        finish();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }


    public void onLoginFailed(){
        Toast.makeText(getBaseContext(),"login failed",Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }

    public boolean validate(){
        boolean vaild = true;
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("enter a vaild email address");
            vaild = false;
        } else {
            emailText.setError(null);
        }
        if(password.isEmpty() || password.length() < 6 || password.length() > 10){
            passwordText.setError("between 6 and 10 alphanumeric characters");
            vaild = false;
        } else {
            passwordText.setError(null);
        }
        return vaild;
    }
}
