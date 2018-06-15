package com.example.apple.beadgame.CatEnemy;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class BitmapFrame implements AnimationFrame{
    List<Bitmap> frames;
    List<Long> playTime;
    int frameCount = 0;
    private boolean replay = true;
    private long timer;

    public BitmapFrame(List<Bitmap> frames, List<Long> playTime){
        this.frames = frames;
        this.playTime = playTime;
        timer = System.currentTimeMillis();
    }

    @Override
    public void init() {
        frameCount = 0;
        timer = System.currentTimeMillis();
    }

    @Override
    public int size() {
        return frames.size();
    }

    @Override
    public int getIndex() {
        return frameCount;
    }

    @Override
    public boolean setIndex(int index) {
        if(index >= size())
            return false;
        this.frameCount = index;
        return true;
    }

    @Override
    public void draw(Canvas canvas, int x, int y) {
        if(frameCount == frames.size())
            return;
        long time = System.currentTimeMillis() - timer;
        if(time > playTime.get(frameCount)) {
            canvas.drawBitmap(frames.get(frameCount), x, y, null);
            ++frameCount;
            if (frameCount == frames.size() && replay)
                frameCount = 0;
            timer = System.currentTimeMillis();
        } else {
            canvas.drawBitmap(frames.get(frameCount), x, y, null);
        }
    }

    public void setReplay(boolean replay) { this.replay = replay; }
}
