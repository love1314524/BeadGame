package com.example.apple.beadgame.CatEnemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by HatsuneMiku on 2018/1/6.
 */

interface AnimationFrame {
    void init();
    int size();
    int getIndex();
    boolean setIndex(int index);
    void draw(Canvas canvas, int x, int y);
}

public class Animation {
    private boolean stop = true;
    private int animationIndex = 0;
    private double timer;
    private List<AnimationFrame> animationList = new LinkedList<>();

    public Animation() {};

    public void playAnimation(int index) {
        if(index >= animationList.size() || animationIndex == index){
            return;
        }
        animationIndex = index;
        animationList.get(index).init();
    }

    public boolean addAnimation(AnimationFrame animationFrame) {
        if(animationList.contains(animationFrame)){
            return false;
        }

        return animationList.add(animationFrame);
    }

    public void onDraw(Canvas canvas, int x, int y) {
        if(!stop) {
            animationList.get(animationIndex).draw(canvas, x, y);
        }
    }
    public int getAnimationIndex() { return animationIndex; }
    public int getAnimationSize() { return animationList.size(); }
    public boolean setAnimationFrameIndex(int index) {
        if(animationIndex >= animationList.size()) {
            return false;
        }
        return animationList.get(animationIndex).setIndex(index);
    }
    public boolean start() {
        if(animationIndex >= animationList.size())
            return false;
        stop = false;
        timer = (float)System.currentTimeMillis() / 1000;
        return true;
    }
    public void stop() { stop = false; }
}
