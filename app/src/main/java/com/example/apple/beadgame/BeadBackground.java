package com.example.apple.beadgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BeadBackground {
    int x,y,size;
    Bitmap bitmap;
    public BeadBackground(int x,int y,int size){
        this.x = x;
        this.y = y;

    }

    public void setBitmap(Resources resources){
        switch ((x+y)%2){
            case 0:
                bitmap = BitmapFactory.decodeResource(resources,R.drawable.greenbead);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(resources,R.drawable.greenbead);
                break;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,size,size,false);
    }
    public void draw(Canvas canvas){
        //畫背景
        canvas.drawBitmap(bitmap, x, y, null);
    }


}
