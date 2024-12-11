package com.example.aircraftwar2024.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aircraftwar2024.R;

public class OnlineOverActivity extends AppCompatActivity implements View.OnClickListener{
    TextView text_1;
    TextView text_2;
    Button returnBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_over);
        text_1 = (TextView) findViewById(R.id.text1);
        text_2 = (TextView) findViewById(R.id.text2);
        returnBtn = (Button) findViewById(R.id.return_button);

        int myScore = getIntent().getIntExtra("myScore",-1);
        int otherScore = getIntent().getIntExtra("otherScore",-1);
        text_1.setText("你的分数是： "+ myScore);
        text_2.setText("对手分数是： "+ otherScore);
        returnBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.return_button){
            Intent intent = new Intent(OnlineOverActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}