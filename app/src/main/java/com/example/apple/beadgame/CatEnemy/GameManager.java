package com.example.apple.beadgame.CatEnemy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;
import java.util.List;

public class GameManager extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private class Thread extends java.lang.Thread{

        private boolean stop = false;

        public Thread(Runnable gameThread) {
            super(gameThread);
        }

        @Override
        public void run() {
            while(!stop) {
                super.run();
            }
        }

        public void setStop(){
            stop = true;
        }

    }

    private List<Character> characterList = new LinkedList<>();

    private SurfaceHolder holder;
    private Thread thread;
    private final long perTime = 30;
    private boolean pause = false;
    private boolean requestUpdateScreen = true;

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        if(!pause) {

            int size = characterList.size();

            for (int i = 0; i < size; ++i) { // main thread, process collision, update, etc...
                Character chA = characterList.get(i);
                chA.update(GameManager.this.getWidth(), GameManager.this.getHeight());
                if (chA.getState() == Character.CharacterState.COLLISION_ON) {
                    Rect rectA = chA.getRect();
                    chA.__collision_init();
                    for (int o = i + 1; o < size; ++o) {
                        Character chB = characterList.get(o);
                        if (chB.getState() == Character.CharacterState.COLLISION_ON) {
                            boolean isCollision = Rect.intersects(rectA, chB.getRect());
                            if (isCollision) {
                                chA.__collision(chB);
                                chB.__collision(chA);
                            }
                        }
                    }
                    chA.__collision_end();
                }
            }
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            for (int i = 0; i < characterList.size(); ++i) { //destroy wait for destroy
                Character character = characterList.get(i);
                switch (character.getState()) {
                    case WAIT_FOR_DESTROY:
                        character.onDestroy();
                        unregist(character);
                        --i;
                        break;
                    default:
                        character.onDraw(canvas);
                }
            }
            holder.unlockCanvasAndPost(canvas);

        } else if(requestUpdateScreen) {
            requestUpdateScreen = false;
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            for(Character character : characterList){
                character.onDraw(canvas);
            }

            holder.unlockCanvasAndPost(canvas);
        }

        long endTime = System.currentTimeMillis();
        long delayTime = perTime - endTime + startTime;
        if(delayTime > 0) {
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public GameManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        thread = new Thread(this);
        holder = this.getHolder();
        holder.addCallback(this);
    }

    public void regist(Character character) {
        if(character == null || characterList.contains(character) || this.isPaused())
            return;
        characterList.add(character);
    }

    protected void unregist(Character character) {
        if(character == null || !characterList.contains(character))
            return;
        characterList.remove(character);
    }

    public void cleanAllObject() {
        characterList.clear();
    }

    public void pauseGame() { this.pause = true; }
    public void resumeGame() { this.pause = false; }

    public boolean isPaused() { return this.pause | this.thread.stop; }
    public boolean isStoped() { return this.thread.stop; }

    public void updateScreen() { requestUpdateScreen = true; }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        for(Character character : characterList) {
            character.onScreenSizeChange(getWidth(), getHeight());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        thread.setStop();
    }
}
