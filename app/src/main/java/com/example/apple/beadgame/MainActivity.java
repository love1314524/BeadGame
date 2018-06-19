package com.example.apple.beadgame;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;

public class MainActivity extends Activity {
    private GameManagerWithCounter gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameManager = (GameManagerWithCounter)findViewById(R.id.WarCatGameManager);
        final GameView gameView = (GameView)findViewById(R.id.GameView);
        gameManager.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                NetworkGame game = new NetworkGame(MainActivity.this, gameManager, gameView);
                game.setPlayer1(gameView);
                game.setPlayer2(new NetworkGamer());
                game.start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
        });
    }
}
