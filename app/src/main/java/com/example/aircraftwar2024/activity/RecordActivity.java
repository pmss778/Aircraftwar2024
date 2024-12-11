package com.example.aircraftwar2024.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aircraftwar2024.ActivityManager;
import com.example.aircraftwar2024.GameRecord.GameRecord;
import com.example.aircraftwar2024.GameRecord.GameRecordDAOImpl;
import com.example.aircraftwar2024.R;
import com.example.aircraftwar2024.music.MySoundPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "RecordActivity";
    private int gameType = 1;
    public ActivityManager activityManager;
    private String level;

    SimpleAdapter listItemAdapter;
    ListView list;
    GameRecordDAOImpl gameRecordDAO = new GameRecordDAOImpl();
    String file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        activityManager = ActivityManager.getActivityManager();
        activityManager.addActivity(this);

        if (getIntent() != null) {
            gameType = getIntent().getIntExtra("gameType", 1);
        }
        switch (gameType){
            case 1:
                level = "Easy";
                break;
            case 2:
                level = "Medium";
                break;
            case 3:
                level = "Hard";
                break;
            default:
                level = "Easy";
                break;
        }
        file = level + "ModeRecord.txt";
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(level + " Mode");
        Button return_btn = (Button) findViewById(R.id.return_btn);
        return_btn.setOnClickListener(this);

        list = findViewById(R.id.leaderboards_list);
        listItemAdapter = new SimpleAdapter(
                this,
                getData(),
                R.layout.activity_item,
                new String[]{"rank","user","score","date"},
                new int[]{R.id.rank,R.id.user,R.id.score,R.id.date});

        list.setAdapter(listItemAdapter);

        list.setOnItemClickListener((parent, view, position, id) -> {
            // 从适配器中获取点击位置的数据项
            Map<String, Object> itemData = (Map<String, Object>) parent.getItemAtPosition(position);

            // 获取排名、玩家名和得分信息
            final String rank = itemData.get("rank").toString();
            final String user = itemData.get("user").toString();
            final int score = (int) itemData.get("score");
            final String time = itemData.get("date").toString();

            // 构造要显示的消息
            String message = "是否删除该记录？\n排名: " + rank + "\n玩家名: " + user + "\n得分: " + score;

            // 创建对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity.this);
            builder.setMessage(message)
                    .setPositiveButton("是", (dialog, id12) -> {
                        // 点击了是，执行删除操作

                        gameRecordDAO.removeRecord(this,file,user,score,time);
                        String deleteMessage = "已删除排名为 " + rank + " 的记录";
                        Toast.makeText(RecordActivity.this, deleteMessage, Toast.LENGTH_SHORT).show();
                        update();
                    })
                    .setNegativeButton("否", (dialog, id1) -> {

                        dialog.dismiss(); // 关闭对话框
                    });

            // 显示对话框
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.return_btn){
            activityManager.finishAllActivity();
            activityManager.addActivity(new MainActivity());
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    private List<Map<String, Object>> getData() {

        List<GameRecord> gameRecordList = gameRecordDAO.loadRecordsFromFile(this, file);

        // 排序游戏记录列表
        Collections.sort(gameRecordList, Comparator.comparingInt(GameRecord::getScore).reversed());

        List<Map<String, Object>> dataList = new ArrayList<>();
        int rank = 1;

        for (GameRecord record : gameRecordList) {
            Map<String, Object> itemMap = new HashMap<>();
            // 将记录的用户、分数、时间等信息放入 Map 中
            itemMap.put("user", record.getUserName());
            itemMap.put("score", record.getScore());
            itemMap.put("date", record.getTime());
            itemMap.put("rank", rank++); // 将排名存储到Map中，并递增rank计数

            dataList.add(itemMap);
        }

        return dataList;
    }
    private void update(){
        list = findViewById(R.id.leaderboards_list);
        listItemAdapter = new SimpleAdapter(
                this,
                getData(),
                R.layout.activity_item,
                new String[]{"rank","user","score","date"},
                new int[]{R.id.rank,R.id.user,R.id.score,R.id.date});
        list.setAdapter(listItemAdapter);
    }
}

