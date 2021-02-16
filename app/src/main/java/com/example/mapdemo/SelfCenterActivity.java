package com.example.mapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelfCenterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private Button dingdan;
    private Button qianbao;
    private Button kajuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_center);
        login = findViewById(R.id.login);
        dingdan = findViewById(R.id.dingdan);
        qianbao = findViewById(R.id.qianbao);
        kajuan = findViewById(R.id.kajuan);

        login.setOnClickListener(this);
        dingdan.setOnClickListener(this);
        qianbao.setOnClickListener(this);
        kajuan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                Intent intent = new Intent(SelfCenterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.dingdan:

                break;
            case R.id.qianbao:

                break;
            case R.id.kajuan:

                break;
        }
    }
}