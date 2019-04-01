package com.example.a84045.lbstest1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends AppCompatActivity {

    private static final String TAG = "Login1111";

    private static final int REQUEST_SIGNUP = 0;

    EditText emailText;

    EditText passwordText;

    Button loginButton;

    TextView signupLink;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = findViewById(R.id.login_email);
        passwordText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.btn_login);
        signupLink = findViewById(R.id.link_signup);
        progressBar = findViewById(R.id.login_progress);
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
        Log.d(TAG,"login222");
        if(!validate()){
            onLoginFailed();
            return;
        }
        loginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        onLoginSuccess();

                    }
                },3000 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_SIGNUP){
            if(resultCode == RESULT_OK){
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
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
