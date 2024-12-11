package com.example.aircraftwar2024.GameRecord;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameRecordDAOImpl implements GameRecordDAO{

    private List<GameRecord> records;
    public GameRecordDAOImpl(){
        this.records = new ArrayList<GameRecord>();
    }


    @Override
    public void findByIndex(int index) {

    }

    @Override
    public void addRecord(GameRecord record) {
        records.add(record);
    }

    @Override
    public List<GameRecord> getAllRecords() {
        return records;
    }

    @Override
    public void removeRecord(Context context, String filename, String user, int score, String time) {
        List<GameRecord> gameRecords = loadRecordsFromFile(context, filename);

        // 遍历游戏记录列表，查找匹配的记录并删除
        Iterator<GameRecord> iterator = gameRecords.iterator();
        while (iterator.hasNext()) {
            GameRecord record = iterator.next();
            if (record.getUserName().equals(user) && record.getScore() == score && record.getTime().equals(time)) {
                iterator.remove(); // 删除匹配的记录
                break;
            }
        }
        ClearRecords(context, filename);
        // 逐条保存更新后的游戏记录到文件或数据库等持久化存储中
        for (GameRecord record : gameRecords) {
            saveRecordToFile(context, filename, record);
        }
    }

    @Override
    public void saveRecordToFile(Context context, String filename, GameRecord record) {
        try (FileOutputStream fos = context.openFileOutput(filename, context.MODE_APPEND);

             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
             String recordString = record.getScore() + "," + record.getUserName() + "," + record.getTime() + "\n";
             writer.write(recordString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ClearRecords(Context context, String filename) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {

            // 清空文件内容
            writer.write(""); // 写入空字符串来清空文件内容

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GameRecord> loadRecordsFromFile(Context context, String filename) {
        List<GameRecord> records = new ArrayList<>();

        try (FileInputStream fis = context.openFileInput(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 解析每一行记录并添加到列表中
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int score = Integer.parseInt(parts[0]);
                    String userName = parts[1];
                    String time = parts[2];
                    records.add(new GameRecord(score, userName, time));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return records;
    }
}
