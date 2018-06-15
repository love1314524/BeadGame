package com.example.apple.beadgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by apple on 2018/6/7.
 */
public class Bead{

    class beadKind {
        static final int GREEN = 0,
                RED = 1,
                PURPLE = 2,
                BLUE = 3,
                YELLOW = 4;
    }
    int h,kind,size; //圖片種類 大小
    int x,y,nextY,bcx,bcy;
    Bitmap bitmap,background_bitmap;
    boolean state = true,check;
    Context context;
    public Bead(Context context,int size,int x,int y,int h){

        this.context = context;
        this.size = size;
        this.x = x;
        this.y = y;
        this.h = h;
        this.nextY = y;
        this.check = false;

        newKind();
        setBitmap();
    }
    void newKind(){
        kind = (int)(Math.random()*10000 % 5) ;
        setBitmap();
    }

    void setBitmap(){
        switch (kind){
            case beadKind.GREEN:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.greenbead);
                break;
            case beadKind.RED:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.redbead);
                break;
            case beadKind.PURPLE:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.purplebead);
                break;
            case beadKind.BLUE:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.bluebead);
                break;
            case beadKind.YELLOW:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.yellowbead);
                break;
            default:
                bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.love);
        }
        bitmap = Bitmap.createScaledBitmap(bitmap,size,size,false);
    }
    public void setBackgroundPosition(int x,int y){
        this.bcx = x;
        this.bcy = y;
        setBackground_bitmap();

    }

    public void setBackground_bitmap() {
        //設定珠子背景
        if(((bcx+bcy)/size)%2 == 0) {
            background_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bc_dark);
        }
        else {
            background_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bc_light);
        }
        background_bitmap = Bitmap.createScaledBitmap(background_bitmap,size,size,false);
    }
    public void draw(Canvas canvas){
        //畫珠子
        if(state)
        canvas.drawBitmap(bitmap, x, y, null);
    }
    public void drawBackground(Canvas canvas){
        canvas.drawBitmap(background_bitmap, bcx, bcy, null);
    }

    public int getKind(){
        return kind;
    }

    public boolean move(int pY,int Height,int speed){
        boolean flag = true;
        if(y <= Height - (size * (pY+1)) - speed){
            y += speed;
        }
        else {
            y = Height - (size * (pY + 1));
            flag = false;
        }
        return flag;
    }
}
