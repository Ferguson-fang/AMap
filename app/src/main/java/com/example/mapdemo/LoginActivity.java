package com.example.mapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login_problem;
    private EditText yongHu;
    private EditText miMa;
    private Button click_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_problem = findViewById(R.id.login_problem);
        yongHu = findViewById(R.id.yongHu);
        miMa = findViewById(R.id.miMa);
        click_login = findViewById(R.id.click_login);

        login_problem.setOnClickListener(this);
        yongHu.setOnClickListener(this);
        miMa.setOnClickListener(this);
        click_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_problem:
                Toast.makeText(LoginActivity.this,"打开用户协议",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.click_login:
                Toast.makeText(LoginActivity.this,"登入成功",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}