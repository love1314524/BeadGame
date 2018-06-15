package com.example.apple.beadgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;
import com.example.apple.beadgame.CatEnemy.Level;

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
               gameView.setGameManager(gameManager);
                (new Level(MainActivity.this, gameManager, gameView)).start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
        });
    }
}
