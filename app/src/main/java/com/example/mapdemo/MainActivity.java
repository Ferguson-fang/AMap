package com.example.mapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mapdemo.basic.BasicFragmentActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                Toast.makeText(MainActivity.this,"进入高德地图",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, BasicFragmentActivity.class);
                startActivity(intent);
                break;
        }
    }
}