package com.example.apple.beadgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telecom.Call;

import com.example.apple.beadgame.CatEnemy.Castle;
import com.example.apple.beadgame.CatEnemy.CatCharacter;
import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;
import com.example.apple.beadgame.CatEnemy.TextCharacter;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;

public class NetworkGame extends Thread {
    protected ConnectionManager.ServerConnection connection;
    protected GameManagerWithCounter gameManager;
    protected GameView gameView;
    protected boolean gameClear = false;
    protected boolean gameOver = false;
    protected boolean allOver = false;
    protected Activity activity;
    protected Gamer gamer1, gamer2;
    protected Castle myCastle, enemyCastle;

    public class GameHandler{
        private GameHandler(){}
        public int getScreenHeight() {
            return gameManager.getHeight();
        }

        public int getScreenWidth() {
            return gameManager.getWidth();
        }

        void addCharacter(CatCharacter character) {
            connection.sendAction(character.getCatCharacterName() + " " + character.getHeal() + " " + character.getAttack());
            gameManager.regist(character);
        }
    }

    class GameHandlerWithNoNetwork extends GameHandler {
        void addCharacter(CatCharacter character) {
            gameManager.regist(character);
        }
    }

    ConnectionManager.ServerConnection.GameActionsListener gameActionsListener = new ConnectionManager.ServerConnection.GameActionsListener() {
        @Override
        public void onEnemyAction(String action) {
            // ignore
        }

        @Override
        public void onGameEnd() {
            if(myCastle.getHeal() > enemyCastle.getHeal()) {
                NetworkGame.this.onGameEnd("Victory", false);
            } else {
                NetworkGame.this.onGameEnd("GAME OVER", false);
            }
        }
    };

    public NetworkGame(Activity activity, GameManagerWithCounter gameManager, GameView gameView) {
        this.gameManager = gameManager;
        this.gameView = gameView;
        this.activity = activity;
        connection = ConnectionManager.getInstance(activity.getApplicationContext());
        init();
        connection.addGameActionsListener(gameActionsListener);
    }



    private void init() {
        gameClear = false;
        gameOver = false;
        Bitmap a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.red);
        int w = 400;
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
                        NetworkGame.this.gameManager.regist(new TextCharacter("GAME OVER",
                                NetworkGame.this.gameManager.getWidth() / 2 - 60,
                                NetworkGame.this.gameManager.getHeight() / 2 ,
                                20));
                        NetworkGame.this.gameManager.pauseGame();
                        gameOver = true;
                        onGameEnd("GAME OVER", true);
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
                        NetworkGame.this.gameManager.regist(new TextCharacter("GAME CLEAR",
                                NetworkGame.this.gameManager.getWidth() /  2 - 60,
                                NetworkGame.this.gameManager.getHeight() / 2,
                                20));
                        NetworkGame.this.gameManager.pauseGame();
                        gameClear = true;
                        onGameEnd("Victory", true);
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

    private void onGameEnd(final String gameState, boolean sendMessage) {
        if(!gameManager.isPaused()) {
            this.gamePause();
            if (sendMessage) {
                connection.sendEndingMessage();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(gameManager.getContext());
                    dlgAlert.setMessage(String.format(
                            gameState + "\n" +
                                    "your call %d cat\n" +
                                    "and kill %d enemy\n"
                            ,
                            gameManager.getRegistTagCount("RED TEAM") - 1,
                            gameManager.getUnregistTagCount("BLUE TEAM")
                    ));
                    dlgAlert.setTitle("Game Score");
                    dlgAlert.setPositiveButton("Ok", null);
                    dlgAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            activity.finish();
                        }
                    });
                    NetworkGame.this.allOver = true;
                    dlgAlert.create().show();
                }
            });
        }
    }
}

