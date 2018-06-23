package com.example.apple.beadgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.apple.beadgame.CatEnemy.Castle;
import com.example.apple.beadgame.CatEnemy.CatCharacter;
import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;
import com.example.apple.beadgame.CatEnemy.TextCharacter;

public class CatGame extends Thread{

    protected GameManagerWithCounter gameManager;
    protected boolean gameClear = false;
    protected boolean gameOver = false;
    protected boolean allOver = false;
    protected Activity activity;
    protected Gamer gamer1, gamer2;
    protected Castle myCastle, enemyCastle;

    public class GameHandler{
        protected GameHandler(){}
        public int getScreenHeight() {
            return gameManager.getHeight();
        }

        public int getScreenWidth() {
            return gameManager.getWidth();
        }

        void addCharacter(CatCharacter character) {
            gameManager.regist(character);
        }
    }

    public CatGame(Activity activity, GameManagerWithCounter gameManager) {
        this.gameManager = gameManager;
        this.activity = activity;
        init();
    }

    // init castle object
    protected void init() {
        gameClear = false;
        gameOver = false;
        Bitmap a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.red);
        int w = (int)(gameManager.getHeight() * ((float)a.getWidth() / a.getHeight()));
        gameManager.regist(myCastle = new Castle(
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
                        CatGame.this.gameManager.regist(new TextCharacter("GAME OVER",
                                CatGame.this.gameManager.getWidth() / 2 - 60,
                                CatGame.this.gameManager.getHeight() / 2 ,
                                20));
                        gameOver = true;
                        onGameEnd("GAME OVER");
                    }
                }));
        a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.blue);
        w = (int)(gameManager.getHeight() * ((float)a.getWidth() / a.getHeight()));
        gameManager.regist(enemyCastle = new Castle(
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
                        CatGame.this.gameManager.regist(new TextCharacter("GAME CLEAR",
                                CatGame.this.gameManager.getWidth() /  2 - 60,
                                CatGame.this.gameManager.getHeight() / 2,
                                20));
                        gameClear = true;
                        onGameEnd("Victory");
                    }
                }));
    }

    @Override
    public void run() {
        gameStart();
        while(!allOver) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayer1(Gamer gameView) {
        gameView.setGameHandler(new GameHandler());
        gamer1 = gameView;
    }

    public void setPlayer2(Gamer gamer) {
        gamer.setGameHandler(new GameHandler());
        gamer2 = gamer;
    }

    void gamePause() {
        gamer1.gamePause();
        gamer2.gamePause();
        gameManager.pauseGame();
    }

    void gameStop() {
        gamer1.gameStop();
        gamer2.gameStop();
    }

    void gameStart() {
        gamer1.gameStart();
        gamer2.gameStart();
        gameManager.resumeGame();
    }

    protected void onGameEnd(final String gameState) {
        if(!gameManager.isPaused()) {
            this.gamePause();
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
                            CatGame.this.gameStart();

                        }
                    });
                    dlgAlert.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CatGame.this.allOver = true;
                            activity.finish();
                        }
                    });
                    dlgAlert.create().show();
                }
            });
        }
    }
}
