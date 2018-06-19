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

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;

public class NetworkGame extends Thread{
    protected ServerConnection connection;
    protected GameManagerWithCounter gameManager;
    protected GameView gameView;
    protected boolean gameClear = false;
    protected boolean gameOver = false;
    protected boolean allOver = false;
    protected Activity activity;
    protected Gamer gamer1, gamer2;

    public class GameHandler{
        private GameHandler(){}
        public int getScreenHeight() {
            return gameManager.getHeight();
        }

        public int getScreenWidth() {
            return gameManager.getWidth();
        }

        void addCharacter(CatCharacter character) {
            try {
                connection.sendAction(character.getCatCharacterName() + " " + character.getHeal() + " " + character.getAttack());
            } catch (JSONException | MqttException e) {
                e.printStackTrace();
            }
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
        connection = new ServerConnection(activity.getApplicationContext());
        init();
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
        gamer.setGameHandler(new GameHandlerWithNoNetwork());
        gamer2 = gamer;
    }

    void gamePause() {
        gamer1.gamePause();
        gamer2.gamePause();
    }

    void gameStop() {
        gamer1.gameStop();
        gamer2.gameStop();
    }

    void gameStart() {
        gamer1.gameStart();
        gamer2.gameStart();
    }

    private void onGameEnd(final String gameState) {
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
                        NetworkGame.this.gameStart();

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

