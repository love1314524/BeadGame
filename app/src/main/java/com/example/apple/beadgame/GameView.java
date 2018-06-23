package com.example.apple.beadgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.apple.beadgame.CatEnemy.BigOrangeCat;
import com.example.apple.beadgame.CatEnemy.GameManager;
import com.example.apple.beadgame.CatEnemy.GameManagerWithCounter;
import com.example.apple.beadgame.CatEnemy.Level;
import com.example.apple.beadgame.CatEnemy.MidBlueCat;
import com.example.apple.beadgame.CatEnemy.RedCat;
import com.example.apple.beadgame.CatEnemy.SmallRedCat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by apple on 2018/6/8.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Gamer{

    private SurfaceHolder holder;

    private int size_x=6,size_y=6;

    private Bead beads[][];
    int m,n;
    int width,height,BitmapSize;
    int combo = 0,comboX,comboY;
    boolean GameFlag = true,isClear = true,comboFlag = false;
    Bitmap bitmap_combo_left,bitmap_combo_mid,bitmap_combo_right;
    Bitmap b;
    NetworkGame.GameHandler gameManager;
    List<List<List<Map<String,Integer>>>> list = new ArrayList<>();
    Sound sound;

    private static class Speed
    {
       static int MoveDownSpeed = 30;
       static int RemoveSpeed = 300;
    }


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = this.getHolder();
        holder.addCallback(this);
        beads = new Bead[size_x][size_y];
        Log.i("list",list.size()+"");

        sound = new Sound(getContext());
    }
    @Override
    public void surfaceCreated(final SurfaceHolder holder) {

        bitmap_combo_right = BitmapFactory.decodeResource(getResources(),R.drawable.score_x);
        bitmap_combo_right = Bitmap.createScaledBitmap(bitmap_combo_right,BitmapSize,BitmapSize,false);
        comboX = width - BitmapSize*3/2;
        comboY = height - BitmapSize*3/2;


        b = BitmapFactory.decodeResource(getResources(),R.drawable.blue_cat);
        b = Bitmap.createScaledBitmap(b,BitmapSize,BitmapSize,false);


        CreateBead();
        DrawMoveBead();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = MeasureSpec.getSize(widthMeasureSpec);
        this.height = MeasureSpec.getSize(heightMeasureSpec);
        //
        setMeasuredDimension( width, height);
        BitmapSize =  width/size_x ;//取得圖片寬度
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        int row = (int)(x / BitmapSize);//計算行列
        int col = (int)(height - y)/BitmapSize;//計算行列

        if(GameFlag)
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if(row >= size_x | col >= size_y | row < 0 | col < 0)
                        break;
                    else {
                        combo = 0;
                        bitmap_combo_left = setComboBitmap(combo/10);
                        bitmap_combo_mid = setComboBitmap(combo % 10);
                        DrawBead();

                        Log.i("ACTION_DOWN", "ACTION_DOWN" + beads[row][col].getKind());
                        m = row;
                        n = col;
                        return true;
                    }

                case MotionEvent.ACTION_MOVE:
                    Log.i("ACTION_MOVE", "ACTION_MOVE");
                    if(row >= size_x | col >= size_y | row < 0 | col < 0)
                        return false;
                    else {
                        Log.i( "row:"+ row, "col:"+col);
                        Log.i( "m:"+ m, "n:"+n);

                        if( m+1 == row | m-1 == row | n+1 == col | n-1 == col)
                        {
                            Bitmap temp = beads[m][n].bitmap;
                            beads[m][n].bitmap = beads[row][col].bitmap;
                            beads[row][col].bitmap = temp;

                            int kind = beads[m][n].kind;
                            beads[m][n].kind = beads[row][col].kind;
                            beads[row][col].kind = kind;
                            if(sound.move_flag) {
                                sound.play_move();
                                sound.wait_move();
                            }
                            DrawBead();
                        }
                        m = row;
                        n = col;
                        return true;
                    }
                case MotionEvent.ACTION_UP:
                    Log.i("ACTION_UP", "ACTION_UP");
                    GameFlag = false;
                    comboFlag = true;
                    combo = 0;
                    SearchBead();

                    return true;
                default:
                    Log.i(event.getAction()+"",event.getAction()+"");
                    break;
            }
        return false;
    }

    private void SearchBead(){
        int list_index;
        int map_index;

        for(int l=0;l<5;l++){
            list.add(new ArrayList<List<Map<String, Integer>>>());
            list.get(l).add(new ArrayList<Map<String, Integer>>());
        }


        //檢查消珠
        for(int i=0;i<size_x;i++)
            for(int j=0;j<size_y;j++){
                if(j+1 >=size_y | j-1 <0){
                }
                else if(beads[i][j].kind == beads[i][j+1].kind && beads[i][j].kind == beads[i][j-1].kind)
                {
                    list_index = 0;
                    map_index = list.get(beads[i][j].kind).get(list_index).size() -1;

                    if(beads[i][j].check|beads[i][j+1].check|beads[i][j-1].check)
                    {
                        for(int k=j-1;k<=j+1;k++)
                            if(!beads[i][k].check) {
                                list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                                map_index++;
                                list.get(beads[i][j].kind).get(list_index).get(map_index).put("x", i);
                                list.get(beads[i][j].kind).get(list_index).get(map_index).put("y", k);
                                beads[i][k].check = true;
                            }
                    }
                    else
                    {
                        list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                        map_index++;

                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("x",i);
                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("y",j);
                        beads[i][j].check = true;


                        list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                        map_index++;

                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("x",i);
                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("y",j-1);
                        beads[i][j-1].check = true;

                        list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                        map_index++;

                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("x",i);
                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("y",j+1);
                        beads[i][j+1].check = true;

                        Log.i("1list","put  x:"+i+" y:"+j);
                    }
                }

                if(i+1 >= size_x | i-1<0){
                }
                else if(beads[i][j].kind == beads[i+1][j].kind && beads[i][j].kind == beads[i-1][j].kind){

                    list_index = 0;
                    map_index = list.get(beads[i][j].kind).get(list_index).size() -1;


                    if(beads[i][j].check|beads[i+1][j].check|beads[i-1][j].check)
                    {
                        for(int k=i-1;k<=i+1;k++)
                            if(!beads[k][j].check) {

                                list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                                map_index++;
                                list.get(beads[i][j].kind).get(list_index).get(map_index).put("x", k);
                                list.get(beads[i][j].kind).get(list_index).get(map_index).put("y", j);
                                beads[k][j].check = true;
                            }
                    }
                    else
                    {
                        list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                        map_index++;

                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("x",i);
                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("y",j);
                        beads[i][j].check = true;

                        list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                        map_index++;

                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("x",i-1);
                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("y",j);
                        beads[i-1][j].check = true;

                        list.get(beads[i][j].kind).get(list_index).add(new HashMap<String, Integer>());
                        map_index++;

                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("x",i+1);
                        list.get(beads[i][j].kind).get(list_index).get(map_index).put("y",j);
                        beads[i+1][j].check = true;
                        //Log.i("2list","put  x:"+i+" y:"+j);
                    }
                }
            }


            //消除隊列
            list.add(new ArrayList<List<Map<String, Integer>>>());


            //設為群組
            int group = -1;
            for(int k = 0;k < list.size() - 1;k++){
                for(int i = 0;i< list.get(k).get(0).size();i++){
                    int x = list.get(k).get(0).get(i).get("x");
                    int y = list.get(k).get(0).get(i).get("y");
                    if(i == 0){
                        list.get(list.size()-1).add(new ArrayList<Map<String, Integer>>());
                        beads[x][y].group = ++group;
                    }
                    else if(beads[x][y].group == -1) {
                        list.get(list.size()-1).add(new ArrayList<Map<String, Integer>>());
                        beads[x][y].group = ++group;
                    }

                    checkgroup(x,y,k);
                }
            }



            //群組加到消除隊列
            for(int k = 0; k < list.size() -1 ;k++){
                for(int i = 0;i < list.get(k).get(0).size();i++){
                    int x = list.get(k).get(0).get(i).get("x");
                    int y = list.get(k).get(0).get(i).get("y");
                    int bead_group =  beads[x][y].group;

                    list.get(list.size()-1).get(bead_group).add(new HashMap<String, Integer>());
                    int index =  list.get(list.size()-1).get(bead_group).size() - 1;

                    list.get(list.size()-1).get(bead_group).get(index).put("x",x);
                    list.get(list.size()-1).get(bead_group).get(index).put("y",y);
                }
            }

            //消除珠子
            for(int g=0;g<= group;g++) {
                for (int j = 0; j < list.get(list.size()-1).get(g).size(); j++) {
                    int x = list.get(list.size()-1).get(g).get(j).get("x");
                    int y = list.get(list.size()-1).get(g).get(j).get("y");

//                    beads[x][y].state = false;
                    beads[x][y].kind = -1;
                    beads[x][y].group = -1;
                    beads[x][y].setBitmap();
                }
                try {

                    combo++;
                    bitmap_combo_left = setComboBitmap(combo/10);
                    bitmap_combo_mid = setComboBitmap(combo % 10);
                    sound.play_hit();
                    DrawBead();
                    Thread.sleep(Speed.RemoveSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //SummonCat();
                //SummonCat();
                //SummonCat();
            }
            while (list.size() > 0)
                list.remove(0);


        if(sound.combo_flag) {
            sound.play_combo();
            sound.wait_combo();
        }

        downBead();
    }

    //判斷群組
    public void checkgroup(int x,int y,int k){

        for (int j=0;j < list.get(k).get(0).size() ; j++){

            int a = list.get(k).get(0).get(j).get("x");
            int b = list.get(k).get(0).get(j).get("y");

            if((Math.abs(x-a) == 1 && y==b) | (Math.abs(y-b) == 1 && x == a)){
                if(beads[a][b].group != beads[x][y].group) {
                    beads[a][b].group = beads[x][y].group;
                    checkgroup(a, b, k);
                }
            }
        }

    }

    //珠子向下移動
    private void downBead(){
        boolean flag = true;
        while (flag) {
            flag = false;
            for (int i = 0; i < size_x; i++) {
                for (int j = 1; j < size_y; j++) {
                    if (beads[i][j].kind != -1)
                        if (beads[i][j - 1].kind == -1) {
                            //上下交換
                            Bead temp = beads[i][j];
                            beads[i][j] = beads[i][j - 1];
                            beads[i][j - 1] = temp;
                            flag = true;
                        }
                }
            }
        }
        addBead();
        DrawMoveBead();
    }
    //增加珠子
    public void addBead(){
        isClear = true;
        for(int i=0;i<size_x;i++)
            for (int j=0;j<size_y;j++) {
                if(beads[i][j].kind == -1){
                    isClear = false;
                    beads[i][j].newKind();
                    beads[i][j].y = 0 - (BitmapSize * (j+1));
                }
                beads[i][j].check = false;
                beads[i][j].state = true;
            }
    }
    //創建珠子
    private void CreateBead() {
        try {
            for(int i=0;i<size_x;i++)
                for (int j=0;j<size_y;j++) {
                    beads[i][j] = new Bead(getContext(), BitmapSize, BitmapSize * i, 0 - (BitmapSize * (j+1)),height);
                    beads[i][j].setBackgroundPosition(BitmapSize * i,height - (BitmapSize * (j+1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //畫出珠子
    private void DrawBead() {
        Log.i("Draw","Draw....");
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        for(int i=0;i<size_x;i++)
            for (int j=0;j<size_y;j++) {
                beads[i][j].drawBackground(canvas);
        }
        for(int i=0;i<size_x;i++)
            for (int j=0;j<size_y;j++) {

                beads[i][j].draw(canvas);
            }

        if(comboFlag) {
            if(combo >= 10)
                canvas.drawBitmap(bitmap_combo_left, comboX-BitmapSize*2, comboY, null);

            canvas.drawBitmap(bitmap_combo_mid, comboX-BitmapSize,comboY , null);
            canvas.drawBitmap(bitmap_combo_right, comboX, comboY, null);
        }

        holder.unlockCanvasAndPost(canvas);
    }

    //畫出珠子向下移動
    private void DrawMoveBead(){
        new Thread(){
            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    flag = false;
                    for (int i = 0; i < size_x; i++) {
                        for (int j = 0; j < size_y; j++) {
                            if (beads[i][j].move(j, height,Speed.MoveDownSpeed)) {
                                flag = true;
                            }
                        }
                    }
                    DrawBead();
                }
                if(!isClear){
                    SearchBead();
                }
                else{

                    comboFlag = false;
                    GameFlag = true;
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                DrawBead();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }.start();
    }

    //設定combo圖片
    public Bitmap setComboBitmap(int combo){
        Bitmap bitmap;
        switch (combo){
            case 0:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_0);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_1);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_2);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_3);
                break;
            case 4:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_4);
                break;
            case 5:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_5);
                break;
            case 6:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_6);
                break;
            case 7:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_7);
                break;
            case 8:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_8);
                break;
            case 9:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_9);
                break;
            default:
                bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.score_x);

        }
        bitmap = Bitmap.createScaledBitmap(bitmap,BitmapSize,BitmapSize,false);
        return bitmap;
    }

    public void SummonCat(){
        gameManager.addCharacter(BigOrangeCat.createCat(getContext(), gameManager, 100, 10));
    }

    @Override
    public void setGameHandler(NetworkGame.GameHandler gameHandler) {
        this.gameManager = gameHandler;
    }

    @Override
    public void gameStart() {
        GameFlag = true;
    }

    @Override
    public void gamePause() {
        GameFlag = false;
    }

    @Override
    public void gameStop() {
        GameFlag = false;
    }
}