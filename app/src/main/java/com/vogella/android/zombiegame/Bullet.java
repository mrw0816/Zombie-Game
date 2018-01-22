package com.vogella.android.zombiegame;

import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Ryan on 3/29/2017.
 */

public class Bullet {
    private static final String TAG = "Bullet";
    private RectF rect;


    private int x;
    private int y;
    private int playerX;
    private int playerY;

    private int width = 20;
    private int height = 20;

    private final int BULLETSPEED = 650;
    private double speedX;
    private double speedY;
    private double angle;

    public Bullet(int touchX, int touchY, int playX, int playY, double barrelLength){
        x = touchX;
        y = touchY;


        angle = Math.atan2((y - playY), (x - playX));
        Log.d(TAG, "Bullet: angle: " + angle + "   barrelLength: " + barrelLength );
        playerX = (int)((barrelLength - 20) * Math.cos(angle));
        playerY = (int)((barrelLength - 20) * Math.sin(angle));


        rect = new RectF(playX + playerX, playY + playerY,  playX + playerX + width, playY + playerY + height);

        calculateSpeed();
    }

    private void calculateSpeed(){



        speedY = Math.sin(angle) * BULLETSPEED;
        speedX = Math.cos(angle) * BULLETSPEED;
        //Log.d("Touch Event", " x: " + x + "   y: " + y + "    playerX: " + playerX + "   playerY: " + playerY);
        //Log.d("Bullet created", " speedX: " + speedX + "     speedY: " + speedY + "    angle: " + angle);
    }

    public RectF getRect(){ return rect; }
    public double getAngle(){ return angle;}


    public void update(long fps){

        rect.left = rect.left + (float)(speedX / fps);
        rect.right = rect.left + width;
        rect.top = rect.top + (float)(speedY / fps);
        //this was originally rect.top - height
        rect.bottom = rect.top + height;

    }
}
