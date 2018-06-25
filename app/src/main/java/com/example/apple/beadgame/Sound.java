package com.example.apple.beadgame;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.Random;

/**
 * Created by apple on 2018/6/23.
 */

public class Sound {

    SoundPool sound1,sound2;

    int move_sound,hit_sound,combo_sound[];
    boolean move_flag,combo_flag;
    Context context;

    public Sound(Context context){
        this.context = context;
        sound1 = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        sound2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);

        combo_sound = new int[7];

        combo_sound[0] = sound1.load(context, R.raw.comboburst_1 , 1);
        combo_sound[1] = sound1.load(context, R.raw.comboburst_2 , 1);
        combo_sound[2] = sound1.load(context, R.raw.comboburst_3 , 1);
        combo_sound[3] = sound1.load(context, R.raw.comboburst_4 , 1);
        combo_sound[4] = sound1.load(context, R.raw.comboburst_5 , 1);
        //combo_sound[5] = sound1.load(context, R.raw.comboburst_6 , 1);
        //combo_sound[6] = sound1.load(context, R.raw.comboburst_7 , 1);

        combo_flag = true;

        hit_sound = sound2.load(context, R.raw.hit_sound , 1);
        move_flag = true;

        move_sound = sound2.load(context, R.raw.move_sound , 1);

    }


    public void play_combo(){

        int i = (int)(Math.random()*10000 % 5);


        sound1.play(combo_sound[i], 1, 1, 0, 0, 1);
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
        sound2.play(hit_sound, 1, 1, 0, 0, 1);
    }

    public void play_move(){
        sound2.play(move_sound, 1, 1, 0, 0, 1);
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
