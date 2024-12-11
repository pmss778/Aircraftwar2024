package com.example.aircraftwar2024.music;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.aircraftwar2024.R;

public class MyMediaPlayer {
    private MediaPlayer bgmPlayer;
    private MediaPlayer bossBgmPlayer;

    public MyMediaPlayer(Context context) {
        bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
        bossBgmPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
        bgmPlayer.setLooping(true);
        bossBgmPlayer.setLooping(true);
    }

    public void playBgm() {
        if (bgmPlayer != null) {
            bgmPlayer.start();
        }
    }

    public void stopBgm() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.prepareAsync(); // Reset for future playback
        }
    }

    public void playBossBgm() {
        if (bossBgmPlayer != null) {
            bossBgmPlayer.seekTo(0);
            bossBgmPlayer.start();
        }
    }

    public void stopBossBgm() {
        if (bossBgmPlayer != null) {
            bossBgmPlayer.pause();
        }
    }

    public void release() {
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
        if (bossBgmPlayer != null) {
            bossBgmPlayer.release();
            bossBgmPlayer = null;
        }
    }
}
