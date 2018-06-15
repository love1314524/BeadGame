package com.example.apple.beadgame.CatEnemy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.apple.beadgame.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by HatsuneMiku on 2018/1/6.
 */

public class MidBlueCat extends RedCat {


    public static final int CatWidth = 75;
    public static final int CatHeight = 50;

    public MidBlueCat(Context context, int x, int y){
        this(context, 300, 30, x, y, CatWidth, CatHeight);
    }

    public MidBlueCat(Context context, int heal, int attack, int x, int y, int w, int h) {
        super(heal, attack, x, y, w, h);

        setAttackSpeed(0.6f);

        List<Bitmap> w_frame = new LinkedList<>();
        w_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko1), w, h, false));
        w_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko2), w, h, false));

        List<Long> w_time = new LinkedList<>();
        w_time.add(300L);
        w_time.add(300L);
        BitmapFrame b_frame = new BitmapFrame(w_frame, w_time);
        b_frame.setReplay(true);
        animation.addAnimation(b_frame);


        List<Bitmap> a_frame = new LinkedList<>();
        a_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko3), w, h, false));
        a_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko4), w, h, false));

        List<Long> a_time = new LinkedList<>();
        a_time.add((long)(getAttackSpeed() * 500));
        a_time.add((long)(getAttackSpeed() * 500));
        animation.addAnimation(new BitmapFrame(a_frame, a_time));

        animation.start();
    }
}
