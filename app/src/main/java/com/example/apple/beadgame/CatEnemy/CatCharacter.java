package com.example.apple.beadgame.CatEnemy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by HatsuneMiku on 2018/1/4.
 */

//see annotation on Character.java
public abstract class CatCharacter extends Character {

    protected int heal;
    protected int attack;
    protected int x, y, w, h;
    protected Animation animation;
    public final int WALK_ANIMATION = 0;
    public final int ATTACK_ANIMATION = 1;

    public CatCharacter(Animation animation, int heal, int attack, int x, int y, int w, int h){
        this.animation = animation;
        this.heal = heal;
        this.attack = attack;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public CatCharacter(int heal, int attack, int x, int y, int w, int h){
        animation = new Animation();
        this.heal = heal;
        this.attack = attack;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    protected abstract void update(int screenWidth, int screenHeight);

    @Override
    public abstract void onHit(Character character);

    @Override
    public Rect getRect() {
        return new Rect(x, y, x + w, y + h);
    }

    @Override
    void onDraw(Canvas canvas) {
        if(animation != null) {
            animation.onDraw(canvas, x, y);
        }
    }

    @Override
    public int getHeal() {
        return heal;
    }

    @Override
    public int getAttack() {
        return attack;
    }

    void moveRight(int screenWidth, int stepSize) {
        if (x + w + stepSize > screenWidth) {
            return;
        }
        x += stepSize;
    }

    void moveLeft(int stepSize) {

        if (x - stepSize < 0) {
            return;
        }
        x -= stepSize;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }
}
