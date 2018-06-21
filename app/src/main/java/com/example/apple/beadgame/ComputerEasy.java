package com.example.apple.beadgame;

import android.content.Context;

import com.example.apple.beadgame.CatEnemy.EnemyShrimp;

import java.util.Random;

public class ComputerEasy implements Gamer {
    CatGame.GameHandler handler;
    boolean running = false;
    Context context;

    Runnable run = new Runnable() {
        @Override
        public void run() {
            try {
                Random r = new Random();
                while(running) {
                    Thread.sleep(r.nextInt(1000));
                    handler.addCharacter(EnemyShrimp.createCat(context, handler, 80, 10));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    ComputerEasy(Context context) {
        this.context = context;
    }

    @Override
    public void setGameHandler(NetworkGame.GameHandler gameHandler) {
        handler = gameHandler;
    }

    @Override
    public void gameStart() {
        running = true;
        new Thread(run).start();
    }

    @Override
    public void gamePause() {
        running = false;
    }

    @Override
    public void gameStop() {
        running = false;
    }
}
