package com.example.apple.beadgame.CatEnemy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.apple.beadgame.GameView;
import com.example.apple.beadgame.R;

/**
 * Created by HatsuneMiku on 2018/1/5.
 */

public class Level extends Thread{

    protected GameManagerWithCounter gameManager;
    protected GameView gameView;
    protected boolean gameClear = false;
    protected boolean gameOver = false;
    protected boolean allOver = false;
    private Activity activity;

    public Level(Activity activity, GameManagerWithCounter gameManager, GameView gameView){
        this.gameManager = gameManager;
        this.gameView = gameView;
        this.activity = activity;
        init();
    }

    @Override
    public void run() {
        while(!allOver) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!(gameClear || gameOver)) {
                gameManager.regist(
                        new EnemyShrimp(gameManager.getContext(),
                                gameManager.getWidth() - EnemyShrimp.CatWidth / 2 - gameManager.getHeight() / 2,
                                gameManager.getHeight() - EnemyShrimp.CatHeight));
            }
        }
    }

    private void init() {
        gameClear = false;
        gameOver = false;
        Bitmap a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.red);
        int w = (int)(gameManager.getHeight() * ((float)a.getWidth() / a.getHeight()));
        gameManager.regist(new Castle(
                a,
                1000,
                0,
                0,
                0,
                w,
                gameManager.getHeight(),
                "RED TEAM",
                new Castle.DestroyCallBack() {
                    @Override
                    public void onDestroy() {
                        Level.this.gameManager.regist(new TextCharacter("GAME OVER",
                                Level.this.gameManager.getWidth() / 2 - 60,
                                Level.this.gameManager.getHeight() / 2 ,
                                20));
                        Level.this.gameManager.pauseGame();
                        gameOver = true;
                        onGameEnd("GAME OVER");
                    }
                }));
        a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.blue);
        w = (int)(gameManager.getHeight() * ((float)a.getWidth() / a.getHeight()));
        gameManager.regist(new Castle(
                a,
                1000,
                0,
                gameManager.getWidth() - w,
                0,
                w,
                gameManager.getHeight(),
                "BLUE TEAM",
                new Castle.DestroyCallBack() {
                    @Override
                    public void onDestroy() {
                        Level.this.gameManager.regist(new TextCharacter("GAME CLEAR",
                                Level.this.gameManager.getWidth() /  2 - 60,
                                Level.this.gameManager.getHeight() / 2,
                                20));
                        Level.this.gameManager.pauseGame();
                        gameClear = true;
                        onGameEnd("Victory");
                    }
                }));
    }

    private void onGameEnd(final String gameState) {
        gameView.gamePause();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(gameManager.getContext());
                dlgAlert.setMessage(String.format(
                        gameState + "\n" +
                                "your call %d cat\n" +
                                "and kill %d enemy\n"
                        ,
                        gameManager.getRegistTagCount("RED TEAM") - 1,
                        gameManager.getUnregistTagCount("BLUE TEAM")
                ));
                dlgAlert.setTitle("Game Score");
                dlgAlert.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gameManager.cleanAllObject();
                        gameManager.resumeGame();
                        init();
                        gameManager.updateScreen();
                        gameView.gameStart();

                    }
                });
                dlgAlert.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Level.this.allOver = true;
                    }
                });
                dlgAlert.create().show();
            }
        });
    }
}
