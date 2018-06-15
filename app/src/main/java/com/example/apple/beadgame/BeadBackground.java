package com.example.apple.beadgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

public class BeadBackground {
    int x,y,size,a,b;
    Bitmap bitmap;
    public BeadBackground(Context context, int x, int y, int size,int a,int b){
        this.x = x;
        this.y = y;
        this.size = size;
        this.a = a;
        this.b = b;
        setBitmap(context.getResources());
    }

    public void setBitmap(Resources resources){
        switch ((a+b)%2){
            case 0:
                bitmap = BitmapFactory.decodeResource(resources,R.drawable.bc_dark);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(resources,R.drawable.bc_light);
                break;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,size,size,false);
    }
    public void draw(Canvas canvas){
        //畫背景
        canvas.drawBitmap(bitmap, x, y, null);
    }


}
