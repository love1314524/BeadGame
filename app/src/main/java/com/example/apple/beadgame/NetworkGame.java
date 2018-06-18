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

public class NetworkGame extends Thread{
    protected GameManagerWithCounter gameManager;
    protected GameView gameView;
    protected boolean gameClear = false;
    protected boolean gameOver = false;
    protected boolean allOver = false;
    private Activity activity;
    private ConnectManager.ServerConnection connection = ConnectManager.getInstantiation();

    class GameHandler{
        int getScreenHeight() {
            return gameManager.getHeight();
        }

        int getScreenWidth() {
            return gameManager.getWidth();
        }

        void addCharacter(CatCharacter character) {
            connection.sendAction(character);
            gameManager.regist(character);
        }
    }

    class GameHandlerWithNoNetwork extends GameHandler {
        void addCharacter(CatCharacter character) {
            gameManager.regist(character);
        }
    }

    public NetworkGame(Activity activity, GameManagerWithCounter gameManager, GameView gameView) {
        this.gameManager = gameManager;
        this.gameView = gameView;
        this.activity = activity;
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
                        NetworkGame.this.gameManager.regist(new TextCharacter("GAME OVER",
                                NetworkGame.this.gameManager.getWidth() / 2 - 60,
                                NetworkGame.this.gameManager.getHeight() / 2 ,
                                20));
                        NetworkGame.this.gameManager.pauseGame();
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
                        NetworkGame.this.gameManager.regist(new TextCharacter("GAME CLEAR",
                                NetworkGame.this.gameManager.getWidth() /  2 - 60,
                                NetworkGame.this.gameManager.getHeight() / 2,
                                20));
                        NetworkGame.this.gameManager.pauseGame();
                        gameClear = true;
                        onGameEnd("Victory");
                    }
                }));
    }

    @Override
    public void run() {
        while(!allOver) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void setPlayer1(GameView gameView) {
        gameView.setGameManager(new GameHandler());
    }

    public void setPlayer2(Gamer gamer) {
        gamer.setGameHandler(new GameHandlerWithNoNetwork());
    }

    private void onGameEnd(final String gameState) {
        gameView.GamePause();
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
                        gameView.GameStart();

                    }
                });
                dlgAlert.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NetworkGame.this.allOver = true;
                    }
                });
                dlgAlert.create().show();
            }
        });
    }
}

