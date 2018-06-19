package com.example.apple.beadgame;

interface Gamer {
    void setGameHandler(NetworkGame.GameHandler gameHandler);
    void gameStart();
    void gamePause();
    void gameStop();
}
