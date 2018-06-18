package com.example.apple.beadgame;

import android.net.Network;

public class NetworkGamer implements Gamer {
    NetworkGame.GameHandler gameHandler;

    @Override
    public void setGameHandler(NetworkGame.GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }
}
