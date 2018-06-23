package com.example.apple.beadgame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.beadgame.ConnectionManager.ServerConnection;

import java.util.List;
import java.util.zip.Inflater;

class RoomAdapter extends BaseAdapter {
    private List<ServerConnection.RoomInfo> infoList;
    private Context context;
    private LayoutInflater inflater;

    RoomAdapter(Context context, List<ServerConnection.RoomInfo> infoList) {
        this.infoList = infoList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int i) {
        return infoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return infoList.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.room_item_layout, null);
        }
        ServerConnection.RoomInfo info = infoList.get(i);
        ((TextView)view.findViewById(R.id.room_name)).setText(info.roomId);
        ((TextView)view.findViewById(R.id.room_count)).setText(String.format("%d %s", info.roomPlayerNum, context.getString(R.string.people)));
        return view;
    }
}

public class RoomListActivity extends AppCompatActivity {
    List<ServerConnection.RoomInfo> roomInfoList;
    TextView playerInfo;
    ListView roomListView;
    ServerConnection connection;

    ServerConnection.RoomListCallBack getRoomListCallback = new ServerConnection.RoomListCallBack() {
        @Override
        public void callback(List<ServerConnection.RoomInfo> infoList) {
            roomInfoList = infoList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerInfo.setText(String.format("%s %s", getString(R.string.hello_user), connection.getPlayerId()));
                    roomListView.setAdapter(new RoomAdapter(RoomListActivity.this, roomInfoList));
                }
            });
        }

        @Override
        public void onError(final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RoomListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        roomListView = findViewById(R.id.room_listview);
        playerInfo    = findViewById(R.id.user_info);
        ServerConnection.getRoomList(getRoomListCallback);
        connection = ConnectionManager.getInstance(this);
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                roomListView.setEnabled(false);
                connection.joinRoom(roomInfoList.get(i).roomId, new ServerConnection.ActionCallback() {
                    @Override
                    public void onSuccess() {
                        changeActivity();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RoomListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Exception e) {
                        refresh(null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RoomListActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                roomListView.setEnabled(true);
                            }
                        });
                    }
                });
            }
        });
    }

    public void createRoom(View view) {
        connection.createRoom(connection.getPlayerId() + "'s room", new ServerConnection.ActionCallback() {
            @Override
            public void onSuccess() {
                changeActivity();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RoomListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RoomListActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void refresh(View view) {
        ServerConnection.getRoomList(getRoomListCallback);
    }

    public void changeActivity() {
        Intent intent = new Intent(this, WaitingRoom.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ServerConnection.getRoomList(getRoomListCallback);
        roomListView.setEnabled(true);
    }
}
