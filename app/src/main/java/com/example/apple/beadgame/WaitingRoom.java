package com.example.apple.beadgame;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WaitingRoom extends Activity {

    boolean isReady = false;
    String enemyName = null;
    AlertDialog warningMessage;
    ConnectionManager.ServerConnection connection = ConnectionManager.getInstance(this);
    ConnectionManager.ServerConnection.WaitRoomActionsListener waitRoomActionsListener = new ConnectionManager.ServerConnection.WaitRoomActionsListener() {
        @Override
        public void onStartGame() {
            connection.removeWaitRoomActionsListener(waitRoomActionsListener);
            startActivity(new Intent(WaitingRoom.this, MainActivity.class));
        }

        @Override
        public void onEnemyReady() {
            if(WaitingRoom.this.enemyName != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enemyStatesView.setText(String.format("%s\n%s", WaitingRoom.this.enemyName, getString(R.string.enemy_readying)));
                    }
                });
            }
        }

        @Override
        public void onEnemyWaiting() {
            if(WaitingRoom.this.enemyName != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enemyStatesView.setText(String.format("%s\n%s", WaitingRoom.this.enemyName, getString(R.string.enemy_not_ready)));
                    }
                });
            }
        }

        @Override
        public void onEnemyLeave() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WaitingRoom.this, String.format("%s %s", WaitingRoom.this.enemyName, getString(R.string.enemy_leaved)), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void roomClose() {
            finish();
        }

        @Override
        public void enemyJoin(String enemyName) {
            if(!enemyName.equals(connection.getPlayerId())) {
                WaitingRoom.this.enemyName = enemyName;
            } else {
                WaitingRoom.this.enemyName = connection.getPlayerId();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WaitingRoom.this, WaitingRoom.this.enemyName + getString(R.string.enemy_joined), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    TextView enemyStatesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        if(!connection.isOnRoom()) {
            Toast.makeText(this, "Not on room", Toast.LENGTH_SHORT).show();
            finish();
        }
        connection.addWaitRoomActionsListener(waitRoomActionsListener);
        enemyStatesView = findViewById(R.id.enemy_states);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(getString(R.string.leave_room_or_not));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                connection.leaveRoom(null);
                finish();
            }
        });
        warningMessage = builder.create();
    }

    public void readyButton(View view) {
        String text;
        if(isReady) {
            connection.sendWaitingMessage();
            text = getString(R.string.ready);
        } else {
            connection.sendReadyMessage();
            text = getString(R.string.cancel_ready);
        }
        isReady = !isReady;
        ((Button)view).setText(text);
    }

    @Override
    public void onBackPressed() {
        if(warningMessage.isShowing()) {
            return;
        }
        warningMessage.show();
    }
}
