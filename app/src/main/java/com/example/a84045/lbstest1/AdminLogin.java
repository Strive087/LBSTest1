package com.example.a84045.lbstest1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdminLogin extends AppCompatActivity {

    private EditText mail;

    private EditText pass;

    private Button login;

    private TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        mail = findViewById(R.id.admin_email);
        pass = findViewById(R.id.admin_password);
        login = findViewById(R.id.admin_login);
        user = findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    Intent intent = new Intent(getApplicationContext(),Manage.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(AdminLogin.this,"admin login failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validate(){
        boolean vaild = true;
        String email = mail.getText().toString();
        String password = pass.getText().toString();
        if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mail.setError("enter a vaild email address");
            vaild = false;
        } else {
            if (email.equals("liwenyue@qq.com")){
                mail.setError(null);
            }else{
                vaild = false;
            }
        }
        if(password.isEmpty() || password.length() < 6 || password.length() > 10){
            pass.setError("between 6 and 10 alphanumeric characters");
            vaild = false;
        } else {
            if (password.equals("liwenyue")){
                pass.setError(null);
            }else{
                vaild = false;
            }
        }
        return vaild;
    }
}
