package com.example.apple.beadgame;

import android.content.Context;
import android.util.Log;

import com.example.apple.beadgame.CatEnemy.CatCharacter;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wwwww on 2018/6/15.
 */
public class ServerConnection {

    static final String mqttHost = "tcp://xx.xx.xx.xx";
    static final String HOST = "";
    static final String roomList   = "/roomlist";
    static final String createRoom = "/createroom";
    static final String joinroom   = "/joinroom";
    static final String getUID     = "/uid";
    static final String mqttTopic  = "/catgirl/";

    MqttAndroidClient client;
    String playerId;
    String roomId;
    boolean onRoom = false;

    private IMqttMessageListener roomMessageListener = new IMqttMessageListener() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws JSONException {
            JSONObject object = new JSONObject(new String(message.getPayload()));
            if(object.has("id") && object.getString("id").equals(playerId)) {
                return;
            }

            if(object.has("command") && object.getString("command").equals("start")) {
                if(serverActions != null) {
                    serverActions.onStart();
                }
                return;
            }

            if(object.has("command") && object.getString("command").equals("end")) {
                if(serverActions != null) {
                    serverActions.onEnd();
                }
                return;
            }

            switch (object.getInt("status")){
                case 4:
                    if(serverActions != null && object.has("action")) {
                        serverActions.onEnemyAction(object.getString("action"));
                    }
                    break;
            }
        }
    };

    static class RoomInfo {
        String roomId,
                player1,
                player2;
        int roomPlayerNum;
    }

    interface ServerActions {
        void onEnemyAction(String action);
        void onStart();
        void onEnd();
    }

    ServerActions serverActions;

    private static String streamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte b[] = new byte[128];
        int length;
        while((length = stream.read(b)) != -1) {
            byteBuffer.write(b, 0, length);
        }
        return byteBuffer.toString();
    }

    private JSONObject streamToJsonObject(InputStream stream) throws IOException, JSONException {
        String json = streamToString(stream);
        return new JSONObject(json);
    }

    private static JSONArray streamToJsonArray(InputStream stream) throws IOException, JSONException {
        String json = streamToString(stream);
        return new JSONArray(json);
    }

    public ServerConnection(Context context) {
        try {
            playerId = getPlayerId();
            client = new MqttAndroidClient(context, mqttHost, playerId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPlayerId() throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(HOST + getUID).openConnection();
        connection.setRequestMethod("GET");
        return streamToString(connection.getInputStream());
    }

    public boolean isOnRoom() {
        return onRoom;
    }

    /**
             * join the room
             * @param roomId  which room you want to join
             * @return success or not
             * @throws JSONException json parse error
             * @throws IOException network error
             */
    public boolean joinRoom(String roomId) throws JSONException, IOException, MqttException {
        synchronized (this) {
            if (onRoom) {
                return false;
            }
            JSONObject object = new JSONObject();
            object.put("roomid", roomId);
            object.put("id", playerId);
            HttpURLConnection connection = (HttpURLConnection)new URL(HOST + joinroom).openConnection();
            connection.setRequestMethod("POST");
            connection.getOutputStream().write(object.toString().getBytes());
            String result = streamToString(connection.getInputStream());
            onRoom = result.equals("success");
            if(onRoom){
                this.roomId = roomId;
                client.subscribe(mqttTopic + "room/" + roomId, 0, roomMessageListener);
            }
            return onRoom;
        }
    }

    /**
             *  create the room
             * @return success or not
             * @throws JSONException json parse error
             * @throws IOException network error
             */
    public boolean createRoom() throws JSONException, IOException, MqttException {
        synchronized (this) {
            if (onRoom) {
                return false;
            }
            JSONObject object = new JSONObject();
            object.put("id", playerId);
            HttpURLConnection connection = (HttpURLConnection)new URL(HOST + createRoom).openConnection();
            connection.setRequestMethod("POST");
            connection.getOutputStream().write(object.toString().getBytes());
            String result = streamToString(connection.getInputStream());
            onRoom = result.equals("success");
            if(onRoom){
                roomId = playerId;
                client.subscribe(mqttTopic + "room/" + roomId, 0, roomMessageListener);
            }
            return onRoom;
        }
    }

    public void leaveRoom() throws MqttException {
        synchronized (this) {
            if(!onRoom) {
                return;
            }
            client.unsubscribe(mqttTopic+"room/"+roomId);
            onRoom = false;
        }
    }

    public void sendAction(String action) throws JSONException, MqttException {
        if(client != null && client.isConnected()) {
            JSONObject object = new JSONObject();
            object.put("id", playerId);
            object.put("status", 4);
            object.put("action", action);
            String strMsg = object.toString();
            MqttMessage message = new MqttMessage();
            message.setPayload(strMsg.getBytes());
            client.publish(mqttTopic + "room/" + roomId, message);
        } else {
            Log.e("error", "mqtt not connected");
        }
    }

    public void setActionListener(ServerActions serverActions) {
        this.serverActions = serverActions;
    }

    public static List<RoomInfo> getRoomList() throws IOException, JSONException {
        URLConnection connection = new URL(HOST + roomList).openConnection();
        JSONArray array = streamToJsonArray(connection.getInputStream());
        ArrayList<RoomInfo> infos = new ArrayList<>();
        for(int i = 0; i < array.length(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);
            RoomInfo info = new RoomInfo();
            info.player1 = jsonObject.getString("player1");
            info.player2 = jsonObject.getString("player2");
            info.roomId  = jsonObject.getString("roomname");
            info.roomPlayerNum = jsonObject.getInt("playcou");
            infos.add(info);
        }
        return infos;
    }
}