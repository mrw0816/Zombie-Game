package com.vogella.android.zombiegame;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

/**
 * Created by Ryan on 3/28/2017.
 */

public class Player {


    //rect used for drawing
    private RectF rect;
    //rect used for interactions
    private RectF hitBox1;
    private RectF hitBox2;
    private double centerX;
    private double centerY;
    private Point hitBoxCenter1;
    private Point hitBoxCenter2;


    private double x;
    private double y;

    private float height;
    private float width;
    private double area;

    private double angle;
    private double tankAngle;

    private float playerSpeed;
    private double xPercent;
    private double yPercent;
    long checkTimeX = System.currentTimeMillis();
    long checkTimeY = System.currentTimeMillis();




    public Player(double screenX, double screenY){

        height = 200;
        width = 100;

        area = height * width;

        x = screenX/2;
        y = screenY/2;

        xPercent = 0;
        yPercent = 0;

        rect = new RectF((float)x, (float)y, (float)(x + width), (float)(y + height));
        hitBox1 = new RectF();
        hitBox2 = new RectF();

        playerSpeed = 350;

        hitBoxCenter1 = new Point();
        hitBoxCenter2 = new Point();

        tankAngle = 0;

    }


    public RectF getRect(){ return rect; }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public double getAngle(){return angle;}

    public RectF getHitBox1(){return hitBox1;}
    public RectF getHitBox2(){return hitBox2;}

    public double getTankHeadAngle(){return tankAngle;}

    public void update(long fps){

        //Log.d("Player FPS: ",fps + "");
        //update player if player hasn't reached screen boundary
        if(xPercent < 0){

            if(rect.left > 0 ) {
                rect.left = rect.left + ((playerSpeed * (float) xPercent) / fps);
                rect.right = rect.left + width;
            }
        }
        if(xPercent > 0){
            if(rect.left < (x * 2) - width){
                rect.left = rect.left + ((playerSpeed * (float) xPercent) / fps);
                rect.right = rect.left + width;
            }
        }
        if(yPercent < 0){
            if(rect.top > 0) {
                rect.top = rect.top + ((playerSpeed * (float) yPercent) / fps);
                rect.bottom = rect.top + height;
            }
        }
        if(yPercent > 0){
            if(rect.bottom < y * 2){
                rect.top = rect.top + ((playerSpeed * (float) yPercent) / fps);
                rect.bottom = rect.top + height;
            }
        }


        updateHitBoxes();
        //if(yPercent > )

       // Log.d("Player hitbox  ", "rect.left: " + rect.left + "    rect.right: " + rect.right +
             //   "   rect.top: " + rect.top + "   rect.bottom: " + rect.bottom + "  Screen x: " + (x*2) + "  Screen y : " + ( y * 2) );


       // Log.d("Update called   ", "rect.left " + rect.left + " rect.right" + rect.right +
                //"rect.top " + rect.top + " rect.bottom" + rect.bottom);
        /*hitBox.left = rect.left;
        hitBox.right = rect.right;
        hitBox.top = rect.top;
        hitBox.bottom = rect.bottom;

        //playerRotation.setTranslate(hitBox.left + width/2, hitBox.top + height/2);
        playerRotation.setRotate((float)(angle * (180/ Math.PI)));
        playerRotation.mapRect(hitBox);*/
    }

    public void setXPercent(double x){

        //if (x != 0 ){
            xPercent = x;
            checkTimeX = System.nanoTime();
            calcAngle();
       //}

    }

    public void setYPercent(double y){

       // if (y != 0 ){
            yPercent = y;
            checkTimeY = System.nanoTime();
            calcAngle();
       // }

        //Log.d("Right Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
    }
    public void calcAngle(){
        angle = Math.atan2( yPercent, xPercent);
    }
    public long getTimeX(){return checkTimeX;}
    public long getTimeY(){return checkTimeY;}

    public boolean isDead(RectF enemy){
        return false;
    }

    public void updateHitBoxes(){

        centerX = rect.centerX();
        centerY = rect.centerY();

        hitBoxCenter1.x = (int)((height/4)*(Math.cos(angle)));
        hitBoxCenter1.y = (int)((height/4)*(Math.sin(angle)));

        hitBox1.left = (float)(hitBoxCenter1.x - (height/4) + centerX);
        hitBox1.top =(float) (hitBoxCenter1.y - (height/4) + centerY);
        hitBox1.right = hitBox1.left + width - 20;
        hitBox1.bottom = hitBox1.top + width - 20;

        hitBoxCenter2.x = (int)((height/4)*(Math.cos(angle + Math.PI)));
        hitBoxCenter2.y = (int)((height/4)*(Math.sin(angle + Math.PI)));

        hitBox2.left = (float)(hitBoxCenter2.x - (height/4)  + centerX);
        hitBox2.top =(float) (hitBoxCenter2.y - (height/4)  + centerY);
        hitBox2.right = hitBox2.left + width - 20;
        hitBox2.bottom = hitBox2.top + width - 20;
    }

    public void updateTankHeadPosition(int x, int y){

        tankAngle = Math.atan2((y - rect.centerY()), (x - rect.centerX()));
    }



}
