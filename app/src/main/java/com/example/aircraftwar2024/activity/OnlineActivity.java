package com.example.aircraftwar2024.activity;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlineActivity extends AppCompatActivity {
    private static final String TAG = "OnlineActivity";
    private static final String SERVER_ADDRESS = "10.0.2.2";
    private static final int SERVER_PORT = 9999;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Handler handler = new Handler(Looper.getMainLooper());
    private AlertDialog matchingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);

        showMatchingDialog();

        new Thread(() -> {
            try {

                Log.i(TAG,"Connecting...");
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Thread.sleep(1000);
                String playerId = in.readLine();
                String response;
                while ((response = in.readLine()) != null) {
                    if (response.startsWith("PLAYER_ID:")){
                    }
                    else if (response.equals("MATCH_FOUND")) {
                        String finalPlayerId = playerId;
                        handler.post(() -> {
                            dismissMatchingDialog();
                            startGame(finalPlayerId);
                        });
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void showMatchingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("匹配中");
        builder.setMessage("正在匹配对手，请稍候...");
        builder.setCancelable(false);
        matchingDialog = builder.create();
        matchingDialog.show();
    }

    private void dismissMatchingDialog() {
        if (matchingDialog != null && matchingDialog.isShowing()) {
            matchingDialog.dismiss();
        }
    }

    private void startGame(String playerid) {
        Toast.makeText(this, "Match found! Starting game...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameType", 1);
        boolean musicFlag = getIntent().getBooleanExtra("musicFlag",false);
        intent.putExtra("playerid",playerid);
        intent.putExtra("musicFlag",musicFlag);
        intent.putExtra("isonline",true);
        System.out.println("start:" + playerid);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


