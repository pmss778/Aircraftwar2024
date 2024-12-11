package com.example.aircraftwar2024.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.aircraft.HeroAircraft;
import com.example.aircraftwar2024.game.BaseGame;
import com.example.aircraftwar2024.game.EasyGame;
import com.example.aircraftwar2024.game.HardGame;
import com.example.aircraftwar2024.game.MediumGame;


public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private int gameType = 1;
    private boolean musicFlag = false;
    public static int screenWidth,screenHeight;
    public Handler mhandler;
    private boolean isonline = false;
    private String playerid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenHW();
        mhandler = new Mhandler();

        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);
            musicFlag = getIntent().getBooleanExtra("musicFlag",false);
            isonline = getIntent().getBooleanExtra("isonline",false);
            playerid = getIntent().getStringExtra("playerid");
        }

        BaseGame baseGameView = null;
        if (gameType == 1){
            baseGameView = new EasyGame(this, mhandler);
            baseGameView.setLevel("Easy");
            baseGameView.setMusicFlag(musicFlag);
            baseGameView.setIsonline(isonline);
            if (isonline){
                baseGameView.setPlayerId(playerid);
            }
        }
        else if(gameType == 2){
            baseGameView = new MediumGame(this, mhandler);
            baseGameView.setLevel("Medium");
            baseGameView.setMusicFlag(musicFlag);
            baseGameView.setIsonline(isonline);
            if (isonline){
                baseGameView.setPlayerId(playerid);
            }
        }
        else if(gameType == 3){
            baseGameView = new HardGame(this, mhandler);
            baseGameView.setLevel("Hard");
            baseGameView.setMusicFlag(musicFlag);
            baseGameView.setIsonline(isonline);
            if (isonline){
                baseGameView.setPlayerId(playerid);
            }
        }

        setContentView(baseGameView);

    }

    public void getScreenHW(){
        //定义DisplayMetrics 对象
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getDisplay().getRealMetrics(dm);

        //窗口的宽度
        screenWidth= dm.widthPixels;
        //窗口高度
        screenHeight = dm.heightPixels;

        Log.i(TAG, "screenWidth : " + screenWidth + " screenHeight : " + screenHeight);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class Mhandler extends Handler {

        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            // 根据不同线程发送过来的消息，执行不同的UI操作
            // 根据 Message对象的what属性 标识不同的消息
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(GameActivity.this, RecordActivity.class);
                    intent.putExtra("gameType",gameType);
                    startActivity(intent);
                    break;
                case 2:
                    Intent onlineoverintent = new Intent(GameActivity.this,OnlineOverActivity.class);
                    int [] Result = (int[]) msg.obj;
                    int myScore = Result[0];
                    int otherScore = Result[1];
                    onlineoverintent.putExtra("myScore",myScore);
                    onlineoverintent.putExtra("otherScore",otherScore);
                    startActivity(onlineoverintent);
                    break;
            }
        }
    }
}
