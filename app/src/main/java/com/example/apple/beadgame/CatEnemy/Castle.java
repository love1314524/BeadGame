package com.example.apple.beadgame.CatEnemy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by HatsuneMiku on 2018/1/4.
 */

public class Castle extends CatCharacter {

    public interface DestroyCallBack {
        void onDestroy();
    }

    private DestroyCallBack destroyCallBack;
    private final int onHitMove = 2;
    private final int onHitAnimationMaxFrameCount = 6;
    private int onHitAnimationFrameCount = 0;
    private boolean isOnHit = false;
    private int firstHeal;
    private Bitmap healBitmap;

    public Castle(Bitmap bitmap, int heal, int attack, int x, int y, int w, int h, DestroyCallBack onDestroyCallBack){
        super(heal, attack, x, y, w, h);
        firstHeal = heal;
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, w, h, false);
        animation.addAnimation(new OneBitmapFrame(bmp));
        destroyCallBack = onDestroyCallBack;
        animation.start();
        healBitmap = Bitmap.createBitmap((int)(this.w * ((float)getHeal() / firstHeal)), (int)(this.h * 0.1), Bitmap.Config.ARGB_8888);
        healBitmap.eraseColor(Color.RED);
    }

    public Castle(Bitmap bitmap, int heal, int attack, int x, int y, int w, int h,
                  String tag, DestroyCallBack onDestroyCallBack) {

        super(heal, attack, x, y, w, h);
        firstHeal = heal;
        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, w, h, false);
        animation.addAnimation(new OneBitmapFrame(bmp));
        destroyCallBack = onDestroyCallBack;
        animation.start();
        this.tag = tag;
        healBitmap = Bitmap.createBitmap((int)(this.w * ((float)getHeal() / firstHeal)), (int)(this.h * 0.1), Bitmap.Config.ARGB_8888);
        healBitmap.eraseColor(Color.RED);
    }

    @Override
    protected void update(int screenWidth, int screenHeight) {
        if(isOnHit) {
            if(onHitAnimationFrameCount == onHitAnimationMaxFrameCount){
                onHitAnimationFrameCount = 0;
                isOnHit = false;
                return;
            }

            if(onHitAnimationFrameCount % 2 == 0) {
                this.x -= onHitMove;
            } else {
                this.x += onHitMove;
            }
            onHitAnimationFrameCount += 1;
        }
    }

    @Override
    protected void onDestroy() {
        if(destroyCallBack != null)
            destroyCallBack.onDestroy();
    }

    @Override
    public void onHit(Character character) {
        isOnHit = true;
        heal -= character.getAttack();
        if(heal <= 0) {
            state = CharacterState.WAIT_FOR_DESTROY;
            healBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            healBitmap = Bitmap.createBitmap((int) (this.w * ((float) getHeal() / firstHeal)), (int) (this.h * 0.1), Bitmap.Config.ARGB_8888);
            healBitmap.eraseColor(Color.RED);
        }
    }

    @Override
    void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(healBitmap, this.x, this.y, null);
    }

    @Override
    public String getCatCharacterName() {
        return "Castle";
    }
}
