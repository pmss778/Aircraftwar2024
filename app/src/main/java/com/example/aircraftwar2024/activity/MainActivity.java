package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.aircraftwar2024.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public boolean musicFlag;
    public static final String IP = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_music_on = (Button) findViewById(R.id.music_on);
        Button btn_music_off = (Button) findViewById(R.id.music_off);
        Button btn_single_mode = (Button) findViewById(R.id.btn_single_mode);
        Button btn_online_mode = (Button) findViewById(R.id.btn_online_mode);

        btn_music_on.setOnClickListener(this);
        btn_music_off.setOnClickListener(this);
        btn_single_mode.setOnClickListener(this);
        btn_online_mode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.music_on){
            musicFlag = true;

        }
        else if (v.getId() == R.id.music_off){
            musicFlag = false;
        }
        else if (v.getId() == R.id.btn_single_mode){
            Intent intent = new Intent(MainActivity.this, OfflineActivity.class);
            intent.putExtra("musicFlag", musicFlag);
            startActivity(intent);
        }
        else if (v.getId() == R.id.btn_online_mode){
            Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
            intent.putExtra("musicFlag", musicFlag);
            startActivity(intent);
        }
    }
}
