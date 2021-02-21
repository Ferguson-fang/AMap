package com.example.mapdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.
                        permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new
                            String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }else{
                    getLocationPermission();
                }

                break;
        }
    }
    private void getLocationPermission(){
        try{
            Toast.makeText(MainActivity.this,"进入高德地图",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, BasicFragmentActivity.class);
            startActivity(intent);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocationPermission();
                }else{
                    Toast.makeText(this,"您拒绝了授权",Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"进入高德地图__无法定位",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, BasicFragmentActivity.class);
                    startActivity(intent);
                }
                break;
            default:
        }
    }
}