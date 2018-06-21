package com.example.apple.beadgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GameModeSelectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode_select);
    }

    public void signalButton(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void networkButton(View view) {
        startActivity(new Intent(this, RoomListActivity.class));
    }
}
