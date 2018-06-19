package com.example.apple.beadgame.CatEnemy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.apple.beadgame.NetworkGame;
import com.example.apple.beadgame.R;

/**
 * Created by HatsuneMiku on 2018/1/7.
 */

public class SmallRedCat extends RedCat {

    public static final int CatWidth = 15;
    public static final int CatHeight = 15;

    public SmallRedCat(Context context, int x, int y){
        this(context, 100, 20, x, y, CatWidth, CatHeight);
    }

    public SmallRedCat(Context context, int heal, int attack, int x, int y, int w, int h) {
        super(heal, attack, x, y, w, h);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.cat_red), w, h, false);
        animation.addAnimation(new OneBitmapFrame(bmp));

        animation.start();
    }

    public static SmallRedCat createCat(Context context, NetworkGame.GameHandler handler, int heal, int attack) {
        SmallRedCat redCat = new SmallRedCat(
                context,
                heal,
                attack,
                0,
                handler.getScreenHeight() - SmallRedCat.CatHeight,
                SmallRedCat.CatWidth,
                SmallRedCat.CatHeight);
        return redCat;
    }
}
