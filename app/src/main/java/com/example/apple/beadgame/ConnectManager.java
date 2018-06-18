package com.example.apple.beadgame;

import com.example.apple.beadgame.CatEnemy.CatCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wwwww on 2018/6/15.
 */
public class ConnectManager {
    static final String HOST = "";
    static final String roomList   = "/roomlist";
    static final String createRoom = "/createroom";
    static final String mqtt       = "/mqttpub";
    static class ServerConnection {
        ServerConnection() {
            //TODO
        }

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

    private final static ServerConnection connection = new ServerConnection();
    public static ServerConnection getInstantiation(){
        synchronized (connection) {
            return connection;
        }
    }
}