package com.vogella.android.zombiegame;

import android.graphics.Point;
import android.util.Log;

import java.util.Random;


/**
 * Created by Ryan on 5/1/2017.
 */

public class Leaf {
    private static final String TAG = "Leaf";


    private double xLoc;
    private double yLoc;
    private static Random rand = new Random();
    private double screenX;
    private double screenY;
    private double rotSpeed;
    private double rotDirection;
    private double rotAngle;

    public Leaf(double sX, double sY){

        rotSpeed = rand.nextInt(360) + 1;
        rotDirection = rand.nextInt(2);

        if(rotDirection == 0){
            rotSpeed =- rotSpeed;
        }

        rotAngle = rand.nextInt(360);

        screenX = sX;
        screenY = sY;

        xLoc = rand.nextInt((int)screenX);
        yLoc = rand.nextInt((int)screenY);


    }

    public void update(long fps, double angle, double hSpeed){

        hSpeed = hSpeed * 4;

       // Log.d(TAG, "update: angle: " + angle );
        Log.d(TAG, "update: " + xLoc + " + " + ((Math.cos(angle) * hSpeed) / fps));
        xLoc = xLoc + ((Math.cos(angle) * hSpeed) / fps);
        yLoc = yLoc + ((Math.sin(angle) * hSpeed) / fps);

        if(xLoc <= -100){
            xLoc = (int)screenX + 100;
        }
        if(xLoc >= screenX + 100){
            xLoc = -100;
        }
        if(yLoc <= -100){
            yLoc = (int)screenY + 100;
        }
        if(yLoc >= screenY + 100){
            yLoc = -100;
        }

        rotAngle = rotAngle + (rotSpeed/fps);

        Log.d(TAG, "update: X: " + xLoc + "   Y: " + yLoc );


    }

    public double getxLoc(){
        return xLoc;
    }
    public double getyLoc(){
        return yLoc;
    }

    public double getAngle(){
        return rotAngle;
    }
}
