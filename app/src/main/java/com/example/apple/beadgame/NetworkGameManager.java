package com.example.apple.beadgame;

import android.content.Context;
import android.util.AttributeSet;

import com.example.apple.beadgame.CatEnemy.CatCharacter;
import com.example.apple.beadgame.CatEnemy.Character;
import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;

/**
 * Created by wwwww on 2018/6/15.
 */

public class NetworkGameManager extends GameManagerWithCounter{
    ConnectManager.ServerConnection connection = ConnectManager.getInstantiation();
    public NetworkGameManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void regist(CatCharacter character) {
        connection.sendAction(character);
        super.regist(character);
    }

    public void _regist(Character character) {
        super.regist(character);
    }
}
