package com.example.apple.beadgame;

import android.content.Context;
import android.net.Network;

import com.example.apple.beadgame.CatEnemy.EnemyShrimp;

import java.sql.Connection;

public class NetworkGamer implements Gamer {
    private NetworkGame.GameHandler gameHandler;
    private ConnectionManager.ServerConnection connection;
    private Context context;

    private ConnectionManager.ServerConnection.EnemyActions listener = new ConnectionManager.ServerConnection.EnemyActions() {
        @Override
        public void onEnemyAction(String action) {
            String act[] = action.split(" ");
            if(act.length != 3) {
                return;
            }
            if(act[0].equals("OrangeCat")) {
                gameHandler.addCharacter(EnemyShrimp.createCat(context));
            }
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
        connection.addEnemyActionListener(listener);
    }

    @Override
    public void gamePause() {
        connection.removeEnemyActionListener(listener);
    }

    @Override
    public void gameStop() {

    }
}
