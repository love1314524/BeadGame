package com.example.apple.beadgame;

import android.app.Activity;

import com.example.apple.beadgame.CatEnemy.BlueCat;
import com.example.apple.beadgame.CatEnemy.EnemyShrimp;
import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;
import com.example.apple.beadgame.CatEnemy.Level;
import com.example.apple.beadgame.CatEnemy.RedCat;

import java.util.List;

/**
 * Created by wwwww on 2018/6/15.
 */

public class NetworkGameLevel extends Level {
    ConnectManager.ServerConnection connection = ConnectManager.getInstantiation();
    public NetworkGameLevel(Activity activity, NetworkGameManager gameManager, GameView gameView) {
        super(activity, gameManager, gameView);
    }

    @Override
    public void run() {
        while(!allOver) {
            if(!(gameClear || gameOver)) {
                List<String> actions = connection.getEnemyAction();
                for(String action : actions) {
                    String act[] = action.split(" ");
                    int heal = Integer.parseInt(act[1]);
                    int a = Integer.parseInt(act[2]);
                    int x = Integer.parseInt(act[3]);
                    int y = Integer.parseInt(act[4]);
                    int w = Integer.parseInt(act[5]);
                    int h = Integer.parseInt(act[6]);
                    if(act[0].equals("RedCat")){
                        ((NetworkGameManager)gameManager)._regist(new BlueCat(heal, a, x, y, w, h));
                    } else if (act[0].equals("OrangeCat")){
                        ((NetworkGameManager)gameManager)._regist(new EnemyShrimp(gameManager.getContext(), heal, a, x, y, w, h));
                    }
                }
            }
        }
    }
}
