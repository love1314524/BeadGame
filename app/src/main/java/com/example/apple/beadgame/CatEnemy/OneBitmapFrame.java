package com.example.apple.beadgame.CatEnemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by HatsuneMiku on 2018/1/7.
 */

public class OneBitmapFrame implements AnimationFrame {

    private Bitmap bitmap;
    public OneBitmapFrame(Bitmap bmp) {
        bitmap = bmp;
    }

    @Override
    public void init() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public boolean setIndex(int index) {
        return true;
    }

    @Override
    public void draw(Canvas canvas, int x, int y) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}
