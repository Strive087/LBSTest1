package com.example.a84045.lbstest1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a84045.lbstest1.Global.Variable;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;

import okhttp3.Response;

public class Signup extends AppCompatActivity {

    private static final int GET_RESPONSE = 1;

    EditText nameText;

    EditText emailText;

    EditText passwordText;

    EditText phoneText;

    RadioGroup sexChoose;

    EditText validateText;

    Button signupButton;

    TextView loginLink;

    ProgressBar progressBar;

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
                        onSignupSuccess();
                    }else{
                        onSignupFailed();
                    }
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameText = findViewById(R.id.signup_name);
        emailText = findViewById(R.id.signup_email);
        passwordText = findViewById(R.id.signup_password);
        phoneText = findViewById(R.id.signup_phone);
        validateText = findViewById(R.id.signup_validate);
        sexChoose =findViewById(R.id.signup_sex);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);
        progressBar = findViewById(R.id.signup_progress);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,null);
                finish();
            }
        });
    }

    public void signup(){
        if(!validate()){
            onSignupFailed();
            return;
        }
        signupButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        final String name = nameText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        final String phone = phoneText.getText().toString();
        final Boolean sex;
        if(sexChoose.getCheckedRadioButtonId() == R.id.btnWoman){
            sex = false;
        }else{
            sex = true;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkHttpUtils.post().url(Variable.host+"/adduser").addParams("usermail",email)
                            .addParams("username",name).addParams("userphone",phone)
                            .addParams("userpassword",password).addParams("usersex",sex.toString())
                            .build().execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("response",responseData);
                    message.what = GET_RESPONSE;
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Signup Success", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
        setResult(RESULT_OK,null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 2) {
            nameText.setError("at least 2 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
