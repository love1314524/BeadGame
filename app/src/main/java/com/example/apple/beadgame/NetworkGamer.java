package com.example.apple.beadgame;

import android.content.Context;

import com.example.apple.beadgame.CatEnemy.EnemyShrimp;

public class NetworkGamer implements Gamer {
    private NetworkGame.GameHandler gameHandler;
    private ConnectionManager.ServerConnection connection;
    private Context context;

    private ConnectionManager.ServerConnection.GameActionsListener listener = new ConnectionManager.ServerConnection.GameActionsListener() {
        @Override
        public void onEnemyAction(String action) {
            String act[] = action.split(" ");
            if(act.length != 3) {
                return;
            }
            if(act[0].equals("OrangeCat")) {
                gameHandler.addCharacter(EnemyShrimp.createCat(context, gameHandler, 100, 20));
            }
        }

        @Override
        public void onGameEnd() {
            connection.removeGameActionsListener(listener);
        }
    };

    NetworkGamer(Context context) {
        connection = ConnectionManager.getInstance(context);
        this.context = context;
    }

    @Override
    public void setGameHandler(NetworkGame.GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void gameStart() {
        connection.addGameActionsListener(listener);
    }

    @Override
    public void gamePause() {
        connection.removeGameActionsListener(listener);
    }

    @Override
    public void gameStop() {
        connection.removeGameActionsListener(listener);
    }
}
