package com.example.apple.beadgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;

import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;

public class MainActivity extends Activity {
    private GameManagerWithCounter gameManager;
    ConnectionManager.ServerConnection connection = ConnectionManager.getInstance(this);

    CatGame game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = findViewById(R.id.WarCatGameManager);
        final GameView gameView = findViewById(R.id.GameView);
        gameManager.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                switch (getIntent().getIntExtra("gameMode", 0)) {
                    case 0:
                        game = new CatGame(MainActivity.this, gameManager);
                        game.setPlayer2(new ComputerEasy(MainActivity.this.getApplicationContext()));
                        break;
                    case 1:
                        game = new NetworkGame(MainActivity.this, gameManager);
                        game.setPlayer2(new NetworkGamer(MainActivity.this.getApplicationContext()));
                        break;
                    default:
                        game = new CatGame(MainActivity.this, gameManager);
                        game.setPlayer2(new ComputerEasy(MainActivity.this.getApplicationContext()));
                }
                game.setPlayer1(gameView);
                game.start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
        });
    }

    @Override
    public void onBackPressed() {
        game.gamePause();
        finish();
    }
}
