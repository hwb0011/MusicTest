package com.cccg.example.musictest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnBigCar;
    private ImageButton btnSmallCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnBigCar=(ImageButton) findViewById(R.id.btn_big_car);
        btnSmallCar=(ImageButton) findViewById(R.id.btn_small_car);
        btnBigCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,BigCarActivity.class));
                finish();
            }
        });
        btnSmallCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SmallCarActivity.class));
                finish();
            }
        });
    }
}
