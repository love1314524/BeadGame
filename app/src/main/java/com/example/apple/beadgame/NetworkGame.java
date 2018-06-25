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

public class NetworkGame extends CatGame {
    protected ConnectionManager.ServerConnection connection;

    class GameHandlerWithNetwork extends GameHandler {
        void addCharacter(CatCharacter character) {
            connection.sendAction(character.getCatCharacterName() + " " + character.getHeal() + " " + character.getAttack());
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

    public NetworkGame(Activity activity, GameManagerWithCounter gameManager) {
        super(activity, gameManager);
        connection = ConnectionManager.getInstance(activity.getApplicationContext());
        connection.addGameActionsListener(gameActionsListener);
    }

    @Override
    public void setPlayer1(Gamer gameView) {
        gameView.setGameHandler(new GameHandlerWithNetwork());
        gamer1 = gameView;
    }

    @Override
    public void setPlayer2(Gamer gamer) {
        gamer.setGameHandler(new GameHandler());
        gamer2 = gamer;
    }

    @Override
    protected void init() {
        gameClear = false;
        gameOver = false;
        Bitmap a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.red);
        int w = (int)(gameManager.getWidth() * 0.3f);
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
                        gameOver = true;
                        onGameEnd("GAME OVER", true);
                    }
                }));
        a = BitmapFactory.decodeResource(gameManager.getContext().getResources(), R.drawable.blue);
        w = (int)(gameManager.getWidth() * 0.3f);
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
                        gameClear = true;
                        onGameEnd("Victory", true);
                    }
                }));
    }

    protected void onGameEnd(final String gameState, boolean sendMessage) {
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
                            gamePause();
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
