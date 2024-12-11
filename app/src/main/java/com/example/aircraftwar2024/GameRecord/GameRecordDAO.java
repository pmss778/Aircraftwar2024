package com.example.aircraftwar2024.GameRecord;

import android.content.Context;

import java.util.List;

public interface GameRecordDAO {
    void findByIndex(int index);
    void addRecord(GameRecord record);
    List<GameRecord> getAllRecords();
    void removeRecord(Context context, String filename, String user,int score,String time);

    void saveRecordToFile(Context context, String filename, GameRecord record);
    List<GameRecord> loadRecordsFromFile(Context context, String filename);
}

