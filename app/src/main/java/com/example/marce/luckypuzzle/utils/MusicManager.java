package com.example.marce.luckypuzzle.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.marce.luckypuzzle.R;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by marce on 17/04/2017.
 */

public class MusicManager {
    private static final String TAG = "MusicManager";

    public static final int MUSIC_PREVIOUS = -1;
    public static final int MUSIC_MENU = 0;
    public static final int MUSIC_WON = 1;
    public static final int MUSIC_GAMEOVER=2;
    public static final int MUSIC_SWIPE=3;

    private static HashMap players = new HashMap();
    private static int currentMusic = -1;
    private static int previousMusic = -1;

    public static void start(Context context, int music) {
        start(context, music, false);
    }

    public static void start(Context context, int music, boolean force) {
        if (!force && currentMusic > -1) {
        // already playing some music and not forced to change
            return;
        }
        if (music == MUSIC_PREVIOUS) {
            Log.d(TAG, "Using previous music [" + previousMusic + "]");
            music = previousMusic;
        }
        if (currentMusic == music) {
        // already playing this music
            return;
        }
        if (currentMusic != -1) {
            previousMusic = currentMusic;
            Log.d(TAG, "Previous music was [" + previousMusic + "]");
            // playing some other music, pause it and change
            pause();
        }
        currentMusic = music;
        Log.d(TAG, "Current music is now [" + currentMusic + "]");
        MediaPlayer mp = (MediaPlayer) players.get(music);
        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
            }
        } else {
            if (music == MUSIC_MENU) {
                mp = MediaPlayer.create(context, R.raw.main_sound);
            } else if (music == MUSIC_WON) {
                mp = MediaPlayer.create(context, R.raw.win);
            } else if (music == MUSIC_GAMEOVER) {
                mp = MediaPlayer.create(context, R.raw.game_over);
            } else if(music==MUSIC_SWIPE) {
                mp = MediaPlayer.create(context, R.raw.swipe);
            }else{
                Log.e(TAG, "unsupported music number - " + music);
                return;
            }
            players.put(music, mp);
            mp.setLooping(true);
            mp.start();
        }
    }

    public static void pause() {
        Collection<MediaPlayer> mps = players.values();
        for (MediaPlayer p : mps) {
            if (p.isPlaying()) {
                p.pause();
            }
        }
    // previousMusic should always be something valid
        if (currentMusic != -1) {
            previousMusic = currentMusic;
            Log.d(TAG, "Previous music was [" + previousMusic + "]");
        }
        currentMusic = -1;
        Log.d(TAG, "Current music is now [" + currentMusic + "]");
    }

}



