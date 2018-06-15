package com.example.apple.beadgame.CatEnemy;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by HatsuneMiku on 2018/1/6.
 */

public class GameManagerWithCounter extends GameManager{
    public GameManagerWithCounter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Map<String, Integer> registCount_tag = new TreeMap<>();
    private Map<String, Integer> unregistCount_tag = new TreeMap<>();

    @Override
    public void regist(Character character) {
        super.regist(character);
        if(character.tag == null)
            return;
        Integer i = registCount_tag.get(character.tag);
        if(i == null) {
            i = 0;
        }
        i += 1;
        registCount_tag.put(character.tag, i);
    }

    @Override
    protected void unregist(Character character) {
        super.unregist(character);
        if(character.tag == null)
            return;
        Integer i = unregistCount_tag.get(character.tag);
        if(i == null) {
            i = 0;
        }
        i += 1;
        unregistCount_tag.put(character.tag, i);
    }

    @Override
    public void cleanAllObject() {
        super.cleanAllObject();
        registCount_tag.clear();
        unregistCount_tag.clear();
    }

    public int getRegistTagCount(String tag) {
        if(registCount_tag.get(tag) == null)
            return 0;
        return registCount_tag.get(tag);
    }

    public int getUnregistTagCount(String tag) {
        if(unregistCount_tag.get(tag) == null)
            return 0;
        return unregistCount_tag.get(tag);
    }
}
