package com.example.apple.beadgame;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by apple on 2018/6/23.
 */

public class Sound {

    SoundPool sound;
    int move_sound,hit_sound,combo_sound;
    boolean move_flag,combo_flag;

    public Sound(Context context){
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);

        combo_sound = sound.load(context, R.raw.comboburst_2 , 1);
        combo_flag = true;

        hit_sound = sound.load(context, R.raw.hit_sound , 1);
        move_flag = true;

        move_sound = sound.load(context, R.raw.move_sound , 1);

    }

    public void play_combo(){
        sound.play(combo_sound, 1, 1, 0, 0, 1);
        combo_flag = false;
    }
    public void wait_combo(){
        new Thread(){
            @Override
            public void run() {
                try {
                    for (int i=0;!combo_flag && i<3000;i++)
                        Thread.sleep(1);
                    combo_flag = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void play_hit(){
        sound.play(hit_sound, 1, 1, 0, 0, 1);
    }

    public void play_move(){
        sound.play(move_sound, 1, 1, 0, 0, 1);
        move_flag = false;
    }
    public void wait_move(){
        new Thread(){
            @Override
            public void run() {
                try {
                    for (int i=0;!move_flag && i<100;i++)
                        Thread.sleep(1);
                    move_flag = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
