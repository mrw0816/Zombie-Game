package com.vogella.android.zombiegame;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import java.util.Random;

import static com.vogella.android.zombiegame.R.id.gameView;

/**
 * Created by Ryan on 3/29/2017.
 */

public class Enemy {


    //rect used for drawing
    private RectF rect;
    //rect used for interactions
    private RectF hitBox;
    private float hitBoxHeight = 90;
    private float hitBoxWidth = 90;

    private int x;
    private int y;
    private double screenX;
    private double screenY;

    private double xRatio;
    private double yRatio;

    private double speedX = 0;
    private double speedY = 0;
    private double angle;

    private float ENEMYSPEED = 250;

    private float frameSpeed;

    private float height = 130;
    private float width = 130;

    private long frameTimer;
    private int frameCounter;

    //default constructor
    public Enemy(double x, double y, Bitmap sheet){
        screenX = x;
        screenY = y;

        xRatio = screenX/1000.0;
        yRatio = screenY/500.0;

        spawnEnemy();



        frameTimer = System.currentTimeMillis();
        frameCounter = 0;
        //Pass in sprite sheet, number of frames, height, width


    }

    public RectF getRect(){ return rect; }
    public float getWidth(){return width;}
    public float getHeight(){return height;}
    public double getAngle(){return angle;}
    public RectF getHitBox(){return hitBox;}

    public void update(long fps, double playerX, double playerY, double hSpeed, double hAngle, boolean hActive){

        angle = Math.atan2( (playerY - rect.top),(playerX - rect.left));
        speedY = Math.sin(angle) * ENEMYSPEED;
        speedX = Math.cos(angle) * ENEMYSPEED;

        /*if(playerX < rect.left){
            speedX = -speedX;
        }
        if(playerY < rect.top){
            speedY = -speedY;
        }*/

        if(hActive){
            speedY = speedY + (Math.sin(hAngle) * hSpeed);
            speedX = speedX + (Math.cos(hAngle) * hSpeed);
        }


        //Log.d("Angle: " ,  "" + angle);
        //Log.d("Player Location: ",  " x: " + playerX + "    y: " + playerY);
        //Log.d("Enemy Spawn Location: ", " x: " + x + " y:" + y);
        //Log.d("Enemy FPS: " , fps + "");
        Log.d("Enemy Location: ", "   x: " + rect.left  + "    y: " + rect.top + "   angle: " + angle);
        //Log.d("Enemy Speed: ",  " x: " + speedX + "    y: " + speedY);




        rect.left = rect.left + (float)(speedX / fps);
        rect.right = rect.left + width;
        rect.top = rect.top + (float)(speedY / fps);
        //this was originally rect.top - height
        rect.bottom = rect.top + height;

        hitBox.left = rect.left + ((width - hitBoxWidth)/2);
        hitBox.right = hitBox.left + hitBoxWidth;
        hitBox.top = rect.top +((height - hitBoxHeight)/2);
        hitBox.bottom = hitBox.top + hitBoxHeight;

        //Log.d("Enemy Location: ", "   x: " + rect.left  + "    y: " + rect.bottom);
    }

    public void spawnEnemy(){
        Random random = new Random();
        int spawnZone = random.nextInt(4);
        //spawnZone corresponds to what edge of the screen the enemy will spawn
        if(spawnZone == 0){
            x = -150;
            y = random.nextInt((int)screenY);
        }else if(spawnZone == 1){
            y = -150;
            x = random.nextInt((int)screenX);
        }else if(spawnZone == 2){
            x = (int)screenX + 150;
            y = random.nextInt((int)screenY);
        }else if(spawnZone == 3){
            y = (int)screenY + 150;
            x = random.nextInt((int)screenX);
        }

        rect = new RectF(x , y, x + width, y + height);
        hitBox = new RectF(rect.left + 20, rect.top + 20, rect.right - 20, rect.bottom - 20);
        ENEMYSPEED =(float)(random.nextInt(300) + 50);
        Log.d("Enemy speed", ENEMYSPEED + "");
        frameSpeed = (50 * 200) / ENEMYSPEED;
        Log.d("Enemy Spawn Location: ", " x: " + rect.left + " y:" + rect.top);
    }

    public int getFrameCounter(){
        if(System.currentTimeMillis() - frameTimer > frameSpeed){
            frameCounter ++;
            frameTimer = System.currentTimeMillis();
        }
        if(frameCounter >= 8){
            frameCounter = 0;
        }
        return frameCounter;
    }

    public void hurricaneUpdate(long fps, double hAngle, double hSpeed){



    }
}
