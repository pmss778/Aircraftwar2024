package com.example.aircraftwar2024.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.aircraftwar2024.R;

import java.util.HashMap;

public class MySoundPool {
    public static final int SOUND_BULLET_HIT = 1;
    public static final int SOUND_BOMB_EXPLOSION = 2;
    public static final int SOUND_GET_SUPPLY = 3;
    public static final int SOUND_GAME_OVER = 4;

    private static SoundPool mysp;
    private static HashMap<Integer, Integer> soundPoolMap;
    private static Context context;

    public MySoundPool(Context context) {
        MySoundPool.context = context;
        createSoundPool();
        loadSounds();
    }

    @android.annotation.TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createSoundPool() {
        if (mysp == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mysp = new SoundPool.Builder()
                        .setMaxStreams(10)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else {
                mysp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            }
        }
    }

    private void loadSounds() {
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(SOUND_BULLET_HIT, mysp.load(context, R.raw.bullet_hit, 1));
        soundPoolMap.put(SOUND_BOMB_EXPLOSION, mysp.load(context, R.raw.bomb_explosion, 1));
        soundPoolMap.put(SOUND_GET_SUPPLY, mysp.load(context, R.raw.get_supply, 1));
        soundPoolMap.put(SOUND_GAME_OVER, mysp.load(context, R.raw.game_over, 1));
    }

    public void playSound(int sound) {
        mysp.play(soundPoolMap.get(sound), 1, 1, 0, 0, 1);
    }

    public void release() {
        if (mysp != null) {
            mysp.release();
            mysp = null;
        }
    }
}

