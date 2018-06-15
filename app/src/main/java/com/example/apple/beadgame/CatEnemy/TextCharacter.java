package com.example.apple.beadgame.CatEnemy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by HatsuneMiku on 2018/1/4.
 */

public class TextCharacter extends Character {
    private String text;
    private int x, y, textSize;

    public TextCharacter(String text, int x, int y, int textSize) {
        this.state = CharacterState.COLLISION_OFF;
        this.text = text;
        this.x = x;
        this.y = y;
        this.textSize = textSize;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void update(int screenWidth, int screenHeight) { }

    @Override
    public void onHit(Character character) { }

    @Override
    public Rect getRect() { return new Rect(0, 0, 0, 0); }

    @Override
    void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        canvas.drawText(text, x, y, paint);
    }

    @Override
    public int getHeal() { return 0; }

    @Override
    public int getAttack() { return 0; }
}
