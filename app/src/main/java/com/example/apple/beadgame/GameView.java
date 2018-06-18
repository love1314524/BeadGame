package com.example.apple.beadgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder holder;

    private int size_x=6,size_y=6;

    private Bead beads[][];
    int m,n;
    int width,height,BitmapSize;
    boolean TochFlag=true,GameFlag = true,isClear = true;
    Bitmap b;
    NetworkGame.GameHandler gameManager;
    List<List<List<Map<String,Integer>>>> list = new ArrayList<>();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = this.getHolder();
        holder.addCallback(this);

        beads = new Bead[size_x][size_y];
        Log.i("list",list.size()+"");
    }
    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        b = BitmapFactory.decodeResource(getResources(),R.drawable.blue_cat);
        b = Bitmap.createScaledBitmap(b,100,100,false);

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

        if(GameFlag && TochFlag)
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if(row >= size_x | col >= size_y | row < 0 | col < 0)
                        break;
                    else {
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

                            DrawBead();
                        }
                        m = row;
                        n = col;
                        return true;
                    }
                case MotionEvent.ACTION_UP:
                    Log.i("ACTION_UP", "ACTION_UP");
                    TochFlag = false;
                    SearchBead();
                    new Thread(){
                        @Override
                        public void run() {
                            int i=5;
                            while ( i >0)
                                try {
                                    Log.i("time",i+"");
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    i--;
                                }
                            TochFlag = true;
                            Log.i("TochFlag ",TochFlag+"");
                        }
                    }.start();
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

        list.add(new ArrayList<List<Map<String, Integer>>>());
        list.add(new ArrayList<List<Map<String, Integer>>>());
        list.add(new ArrayList<List<Map<String, Integer>>>());
        list.add(new ArrayList<List<Map<String, Integer>>>());
        list.add(new ArrayList<List<Map<String, Integer>>>());

        for(int i=0;i<size_x;i++)
            for(int j=0;j<size_y;j++){
                if(j+1 >=size_y | j-1 <0){
                }
                else if(beads[i][j].kind == beads[i][j+1].kind && beads[i][j].kind == beads[i][j-1].kind)
                {
                    list_index = list.get(beads[i][j].kind).size();
                    if(list_index != 0)
                        map_index = list.get(beads[i][j].kind).get(list_index-1).size()-1;
                    else
                        map_index = -1;

                    if(beads[i][j].check|beads[i][j+1].check|beads[i][j-1].check)
                    {
                        for(int k=j-1;k<=j+1;k++)
                            if(!beads[i][k].check) {
                                list.get(beads[i][j].kind).get(list_index-1).add(new HashMap<String, Integer>());
                                map_index++;
                                list.get(beads[i][j].kind).get(list_index-1).get(map_index).put("x", i);
                                list.get(beads[i][j].kind).get(list_index-1).get(map_index).put("y", k);
                                beads[i][k].check =true;
                            }
                    }
                    else
                    {
                        map_index = -1;
                        list.get(beads[i][j].kind).add( new ArrayList<Map<String, Integer>>());

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

                    list_index = list.get(beads[i][j].kind).size();
                    if(list_index != 0)
                        map_index = list.get(beads[i][j].kind).get(list_index-1).size()-1;
                    else
                        map_index = -1;

                    if(beads[i][j].check|beads[i+1][j].check|beads[i-1][j].check)
                    {
                        for(int k=i-1;k<=i+1;k++)
                            if(!beads[k][j].check) {
                                list.get(beads[i][j].kind).get(list_index-1).add(new HashMap<String, Integer>());
                                map_index++;
                                list.get(beads[i][j].kind).get(list_index-1).get(map_index).put("x", k);
                                list.get(beads[i][j].kind).get(list_index-1).get(map_index).put("y", j);
                                beads[k][j].check =true;
                            }
                    }
                    else
                    {
                        map_index = -1;
                        list.get(beads[i][j].kind).add( new ArrayList<Map<String, Integer>>());

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
            //消除珠子
            for(int k=0;k<list.size();k++) {
                for (int i = 0; i < list.get(k).size(); i++) {
                    for (int j = 0; j < list.get(k).get(i).size(); j++) {
                        int x = list.get(k).get(i).get(j).get("x");
                        int y = list.get(k).get(i).get(j).get("y");
                        beads[x][y].state = false;
                        beads[x][y].kind = -1;
                        beads[x][y].setBitmap();
                    }
                    try {
                        DrawBead();
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SummonCat();
                    SummonCat();
                    SummonCat();

                }
            }
            while (list.size() > 0)
                list.remove(0);
        downBead();
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

            //canvas.drawBitmap(b, 100, 100, null);

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
                            if (beads[i][j].move(j, height,30)) {
                                flag = true;
                            }
                        }
                    }
                    DrawBead();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!isClear){
                    SearchBead();
                }
            }
        }.start();
    }

    public void SummonCat(){
        gameManager.addCharacter(new SmallRedCat(getContext(),
                gameManager.getScreenHeight() / 2,
                gameManager.getScreenWidth() - SmallRedCat.CatHeight));
    }

    public void GamePause(){
        GameFlag = false;
        TochFlag = false;
    }
    public void GameStart(){
        TochFlag = true;
        GameFlag = true;
    }
    public void setGameManager(NetworkGame.GameHandler gameManager) {
        this.gameManager = gameManager;
    }
}