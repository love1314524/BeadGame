
package com.example.apple.beadgame.CatEnemy;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.LinkedList;
import java.util.List;

public abstract class Character {
    enum CharacterState {
        COLLISION_ON,
        COLLISION_OFF,
        WAIT_FOR_DESTROY,
    }

    public Character(){
        state = CharacterState.COLLISION_ON;
    }

    public Character(CharacterState characterState){
        state = characterState;
    }

    protected void onDestroy() {}                           // when destroy
    protected void onScreenSizeChange(int screenWidth, int screenHeight) {}

    /******* DO NOT CHANGE THESE FUNCTION *******/
    private long previousTime = 0;
    final void __collision_init() {
        if(previousTime == 0) {
            previousTime = System.currentTimeMillis();
        }
        _collisionList.clear();
    }

    final void __collision(Character character) {
        if(!_collisionList.contains(character)){
            _collisionList.add(character);
        }
    }

    final void __collision_end() {
        previousTime = System.currentTimeMillis();
    }

    final float getDelTime() {
        return (float)(System.currentTimeMillis() - previousTime) / 1000;
    }
    /***************************************************/

    protected abstract void update(int screenWidth, int screenHeight); //

    public abstract void onHit(Character character); // when someone want to hit you

    public abstract Rect getRect();

    abstract void onDraw(Canvas canvas); // draw event

    public abstract int getHeal();

    public abstract int getAttack();

    public final void setTag(String tag) { this.tag = tag; }
    public final String getTag() { return tag; }

    protected final List<Character> getCollisionList() { return new LinkedList<>(_collisionList); } // who is touching you

    public CharacterState getState(){ return state; }

    protected CharacterState state;
    protected String tag;
    private List<Character> _collisionList = new LinkedList<>();
}
