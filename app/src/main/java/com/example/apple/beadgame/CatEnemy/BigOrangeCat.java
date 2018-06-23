package com.example.apple.beadgame.CatEnemy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.apple.beadgame.NetworkGame;
import com.example.apple.beadgame.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by HatsuneMiku on 2018/1/7.
 */

public class BigOrangeCat extends RedCat{

    public static final int CatWidth = 75;
    public static final int CatHeight = 50;

    static BitmapFrame w_bFrame;
    static BitmapFrame a_bFrame;

    public BigOrangeCat(Context context, int heal, int attack, int x, int y, int w, int h) {
        super(heal, attack, x, y, w, h);

        setAttackSpeed(0.6f);

        if(w_bFrame == null) {
            List<Bitmap> w_frame = new ArrayList<>();
            w_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko21), w, h, false));
            w_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko22), w, h, false));

            List<Long> w_time = new ArrayList<>();
            w_time.add(300L);
            w_time.add(300L);

            List<Bitmap> a_frame = new ArrayList<>();
            a_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko23), w, h, false));
            a_frame.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.neko24), w, h, false));

            List<Long> a_time = new ArrayList<>();
            a_time.add((long) (getAttackSpeed() * 500));
            a_time.add((long) (getAttackSpeed() * 500));
            w_bFrame = new BitmapFrame(w_frame, w_time);
            a_bFrame = new BitmapFrame(a_frame, a_time);
        }

        animation.addAnimation(w_bFrame);
        animation.addAnimation(a_bFrame);
        animation.start();
    }

    @Override
    public String getCatCharacterName() {
        return "OrangeCat";
    }

    public static BigOrangeCat createCat(Context context, NetworkGame.GameHandler handler, int heal, int attack) {
        BigOrangeCat redCat = new BigOrangeCat(
                context,
                heal,
                attack,
                0,
                handler.getScreenHeight() - BigOrangeCat.CatHeight,
                BigOrangeCat.CatWidth,
                BigOrangeCat.CatHeight);
        redCat.setStepSize((int) (handler.getScreenWidth() * 0.1));
        return redCat;
    }
}
