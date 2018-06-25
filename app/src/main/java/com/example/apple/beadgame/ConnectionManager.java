package com.example.apple.beadgame;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wwwww on 2018/6/15.
 */

public class ConnectionManager {
    private ConnectionManager(){};
    public static class ServerConnection {
        static final String mqttHost = "tcp://47.74.20.158";
        static final String HOST = "http://192.168.0.138:9487";

        static final String roomList   = "/roomlist";
        static final String createRoom = "/createroom";
        static final String joinroom   = "/joinroom";
        static final String getUID     = "/uid";
        static final String mqttTopic  = "catgirl/room/";

        static final int MQTT_WAIT_CODE = 0;
        static final int MQTT_READY_CODE = 1;
        static final int MQTT_START_CODE = 2;
        static final int MQTT_DISCONNECT_CODE = 3;
        static final int MQTT_FIGHTING_CODE = 4;
        static final int MQTT_ENDING_CODE = 5;

        private MqttAndroidClient client;
        private String playerId;
        private String roomId;
        private Context context;

        private IMqttMessageListener roomMessageListener = new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws JSONException {
                JSONObject object = new JSONObject(new String(message.getPayload()));
                if(object.has("id") && object.getString("id").equals(playerId)) {
                    return;
                }

                if(object.has("command")) {
                    switch (object.getString("command")) {
                        case "start":
                            for (WaitRoomActionsListener action : waitRoomActionsListenerList) {
                                action.onStartGame();
                            }
                            return;
                        case "end":
                            for (GameActionsListener action : gameActionsListenerListenerList) {
                                action.onGameEnd();
                            }
                            return;
                        case "leave":
                            for (WaitRoomActionsListener action : waitRoomActionsListenerList) {
                                action.roomClose();
                            }
                            return;
                        case "join":
                            for (WaitRoomActionsListener action : waitRoomActionsListenerList) {
                                action.enemyJoin(object.getString("player"));
                            }
                            return;
                    }
                }

                switch (object.getInt("status")) {
                    case MQTT_FIGHTING_CODE:
                        for(GameActionsListener actionsListener : gameActionsListenerListenerList) {
                            actionsListener.onEnemyAction(object.getString("action"));
                        }
                        break;
                    case MQTT_WAIT_CODE:
                        for(WaitRoomActionsListener actionsListener : waitRoomActionsListenerList) {
                            actionsListener.onEnemyWaiting();
                        }
                        break;
                    case MQTT_READY_CODE:
                        for(WaitRoomActionsListener actionsListener : waitRoomActionsListenerList) {
                            actionsListener.onEnemyReady();
                        }
                        break;
                    case MQTT_DISCONNECT_CODE:
                        for (WaitRoomActionsListener action : waitRoomActionsListenerList) {
                            action.onEnemyLeave();
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

        interface GameActionsListener {
            void onEnemyAction(String action);
            void onGameEnd();
        }

        interface WaitRoomActionsListener {
            void onStartGame();
            void onEnemyReady();
            void onEnemyWaiting();
            void onEnemyLeave();
            void roomClose();
            void enemyJoin(String enemyName);
        }

        List<GameActionsListener> gameActionsListenerListenerList = new LinkedList<>();
        List<WaitRoomActionsListener> waitRoomActionsListenerList = new LinkedList<>();

        void addWaitRoomActionsListener(WaitRoomActionsListener actions) {
            waitRoomActionsListenerList.add(actions);
        }

        void removeWaitRoomActionsListener(WaitRoomActionsListener actions) {
            waitRoomActionsListenerList.remove(actions);
        }

        void addGameActionsListener(GameActionsListener actions) {
            gameActionsListenerListenerList.add(actions);
        }

        void removeGameActionsListener(GameActionsListener actions) {
            gameActionsListenerListenerList.remove(actions);
        }

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

        private ServerConnection(Context context) {
            this.context = context;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection connection = (HttpURLConnection)new URL(HOST + getUID).openConnection();
                        connection.setRequestMethod("GET");
                        playerId = streamToJsonObject(connection.getInputStream()).getString("id");
                        client = new MqttAndroidClient(ServerConnection.this.context, mqttHost, playerId);
                        client.connect();
                    } catch (IOException | MqttException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public String getPlayerId() {
            return playerId;
        }

        public boolean isOnRoom() {
            return roomId != null;
        }

        interface ActionCallback{
            void onSuccess();
            void onFailure(Exception e);
        }

        private void sendMqttStatesMessage(final int state) {
            if(playerId == null) {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("id", playerId);
                        object.put("status", state);
                        String strMsg = object.toString();
                        MqttMessage message = new MqttMessage();
                        message.setPayload(strMsg.getBytes());
                        message.setQos(2);
                        client.publish(mqttTopic + roomId, message);
                    } catch (JSONException | MqttException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        void sendReadyMessage() {
            sendMqttStatesMessage(MQTT_READY_CODE);
        }

        void sendWaitingMessage() {
            sendMqttStatesMessage(MQTT_WAIT_CODE);
        }

        void sendEndingMessage() {
            sendMqttStatesMessage(MQTT_ENDING_CODE);
        }

        /**
         * join the room
         * @param roomId  which room you want to join
         */
        public void joinRoom(final String roomId, final ActionCallback callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(ServerConnection.this.roomId != null) {
                        leaveRoom(null);
                    }
                    synchronized (this) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("roomid", roomId);
                            object.put("id", playerId);
                            HttpURLConnection connection = (HttpURLConnection)new URL(HOST + joinroom).openConnection();
                            connection.setConnectTimeout(1000);
                            connection.setReadTimeout(1000);
                            connection.addRequestProperty("Content-Type", "application/json");
                            connection.setRequestMethod("POST");
                            connection.getOutputStream().write(object.toString().getBytes());
                            String result = streamToString(connection.getInputStream());
                            switch (result) {
                                case "success":
                                    ServerConnection.this.roomId = roomId;
                                    client.subscribe(mqttTopic + roomId, 2, roomMessageListener);
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                    break;
                                case "full":
                                    if (callback != null) {
                                        callback.onFailure(new Exception("room is full"));
                                    }
                                    break;
                                default:
                                    if (callback != null) {
                                        callback.onFailure(new Exception("undefine error"));
                                    }
                                    break;
                            }
                        } catch (JSONException | MqttException | IOException e) {
                            if (callback != null) {
                                callback.onFailure(e);
                            }
                        }
                    }
                }
            }).start();
        }

        /**
         *  create the room
         */
        public void createRoom(final String roomName, final ActionCallback callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(roomId != null) {
                        leaveRoom(null);
                    }
                    synchronized (this) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("id", playerId);
                            object.put("roomname", roomName);
                            HttpURLConnection connection = (HttpURLConnection)new URL(HOST + createRoom).openConnection();
                            connection.setConnectTimeout(1000);
                            connection.setReadTimeout(1000);
                            connection.addRequestProperty("Content-Type", "application/json");
                            connection.setRequestMethod("POST");
                            connection.getOutputStream().write(object.toString().getBytes());
                            String result = streamToString(connection.getInputStream());
                            if(result.equals("success")){
                                roomId = playerId;
                                client.subscribe(mqttTopic + roomId, 2, roomMessageListener);
                                if (callback != null) {
                                    callback.onSuccess();
                                }
                            } else {
                                if (callback != null) {
                                    callback.onFailure(new Exception("undefine error, create room fail"));
                                }
                            }
                        } catch (JSONException | MqttException | IOException e) {
                            if(callback != null) {
                                callback.onFailure(e);
                            }
                        }
                    }
                }
            }).start();
        }

        public void leaveRoom(final ActionCallback callback) {
            synchronized (this) {
                if (roomId == null) {
                    return;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("id", playerId);
                            object.put("status", MQTT_DISCONNECT_CODE);
                            String strMsg = object.toString();
                            MqttMessage message = new MqttMessage();
                            message.setPayload(strMsg.getBytes());
                            message.setQos(2);
                            client.publish(mqttTopic + roomId, message);
                            client.unsubscribe(mqttTopic + roomId);
                            roomId = null;
                            if(callback != null) {
                                callback.onSuccess();
                            }
                        } catch (MqttException e) {
                            if(callback != null) {
                                callback.onFailure(e);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public void sendAction(final String action) {
            if(roomId == null) {
                Toast.makeText(context, "not in room", Toast.LENGTH_SHORT).show();
            }
            else if(client != null && client.isConnected()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("id", playerId);
                            object.put("status", MQTT_FIGHTING_CODE);
                            object.put("action", action);
                            String strMsg = object.toString();
                            MqttMessage message = new MqttMessage();
                            message.setPayload(strMsg.getBytes());
                            message.setQos(2);
                            client.publish(mqttTopic + roomId, message);
                        } catch (JSONException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                Log.e("error", "mqtt not connected");
            }
        }

        interface RoomListCallBack {
            void callback(List<RoomInfo> infoList);
            void onError(Exception e);
        }
        
        public static void getRoomList(final RoomListCallBack callBack) {
            if(callBack == null) { return; }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URLConnection connection =  connection = new URL(HOST + roomList).openConnection();
                        connection.setConnectTimeout(1000);
                        connection.setReadTimeout(1000);
                        JSONArray array = streamToJsonArray(connection.getInputStream());
                        ArrayList<RoomInfo> infos = new ArrayList<>();
                        for(int i = 0; i < array.length(); ++i) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            RoomInfo info = new RoomInfo();
                            if(jsonObject.has("player1")) {
                                info.player1 = jsonObject.getString("player1");
                            }
                            if(jsonObject.has("player2")) {
                                info.player2 = jsonObject.getString("player2");
                            }
                            if(jsonObject.has("roomname")) {
                                info.roomId = jsonObject.getString("roomname");
                            }
                            if(jsonObject.has("playcou")) {
                                info.roomPlayerNum = jsonObject.getInt("playcou");
                            }
                            infos.add(info);
                        }
                        callBack.callback(infos);
                    } catch (IOException | JSONException e) {
                        callBack.onError(e);
                    }
                }
            }).start();
        }
    }

    private static ServerConnection connection;
    private final static Object lock = new Object();
    static ServerConnection getInstance(Context context) {
        synchronized (lock) {
            if(connection == null) {
                connection = new ServerConnection(context);
            }
            return connection;
        }
    }
}