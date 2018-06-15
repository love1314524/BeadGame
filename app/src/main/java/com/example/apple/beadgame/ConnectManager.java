package com.example.apple.beadgame;

import com.example.apple.beadgame.CatEnemy.CatCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wwwww on 2018/6/15.
 */
public class ConnectManager {
    static class ServerConnection{
        List<String> getEnemyAction() {
            return new ArrayList<>();
        }

        void sendAction(CatCharacter character) {
            // TODO
        }

        List<String> getRoomList() {
            return new ArrayList<String>();
        }
    }

    private static ServerConnection connection = new ServerConnection();
    public static ServerConnection getInstantiation(){
        return connection;
    }
}