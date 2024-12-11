package com.example.aircraftwar2024.GameRecord;

public class GameRecord {
    private int score;
    private String userName;
    private String time;
    public GameRecord(int score,String userName,String time) {
        this.score = score;
        this.userName = userName;
        this.time = time;
    }

    public int getScore() {
        return score;
    }
    public String getUserName(){
        return userName;
    }
    public String getTime(){
        return time;
    }
}

