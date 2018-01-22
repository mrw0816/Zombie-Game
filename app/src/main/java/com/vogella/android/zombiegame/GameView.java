package com.vogella.android.zombiegame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;


/**
 * Created by Ryan on 3/28/2017.
 */

public class GameView extends SurfaceView implements Runnable, JoystickView.JoystickListener{
    private static final String TAG = "GameView";
    //Thread for GameView
    Thread gameThread = null;

    //surface holder for paint and canvas
    SurfaceHolder surfaceHolder;


    volatile boolean playing;

    Canvas canvas;
    Paint paint;

    //variables for calculating frames per second
    long fps;
    private long timeThisFrame;


    double screenX;
    double screenY;

    int playerScore = 0;

    //number of enemies alive
    //will increase based on score
    int enemyCounter = 1;

    Player player;
    Matrix playerRotation = new Matrix();
    Bullet[] bullet = new Bullet[6];
    int bulletCounter = 0;
    boolean reloading = false;
    double reloadTime = 0;

    //At max 100 enemies
    Enemy[] enemy = new Enemy[100];

    //Object is not moving at the start of the game
    boolean isMoving = false;
    Matrix matrix = new Matrix();

    JoystickView joystick;

    double joyStickX;
    double joyStickY;

    float hurricaneAngle = 0;
    boolean hurricaneAvailable = true;
    boolean hurricaneActive = false;
    long hurricaneTimer = 0;
    double hurricaneSpeed = 300;

    Bitmap zombieBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.zombie_topdown);
    Animation walkAnimation = new Animation(zombieBitMap, 8, zombieBitMap.getWidth()/36, zombieBitMap.getHeight()/8, 6, 5);

    Bitmap tanks = BitmapFactory.decodeResource(getResources(), R.drawable.tanks);



    Bitmap tankBase = Bitmap.createScaledBitmap(Bitmap.createBitmap(tanks, ((tanks.getWidth()*330)/419) + 10, ((tanks.getHeight() * 192)/ 338) + 10 , (( tanks.getWidth() * 40) / 419) - 10, ((tanks.getHeight() * 65)/ 338) - 15) , 100, 200, false);
    Bitmap tankHead = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tankhead));

    Bitmap dayTile = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sandtile));
    Bitmap tankshell = Bitmap.createScaledBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tankshell)), 50, 25, false);

    Bitmap bloodsplatter1 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bloodsplatter1));
    Bitmap bloodsplatter2 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bloodsplatter2));
    Bitmap bloodsplatter3 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bloodsplatter3));
    Bitmap bloodsplatter4 = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bloodsplatter4));

    Bitmap hurricaneArrow = Bitmap.createScaledBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.arrow2)), 100, 100, false);

    Bitmap leaf1 = Bitmap.createScaledBitmap(Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.leaf1)), 50, 50, false);


    BloodSplatter[] bloodSplatters = new BloodSplatter[1000];
    int enemiesKilled = 0;
    Random rand = new Random();

    Leaf[] leaf = new Leaf[20];


    public GameView(Context context){

        super(context);

        surfaceHolder = getHolder();
        paint = new Paint();

        //Get the size of screen

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenX = size.x - (size.x * 0.05);
        screenY = size.y - (size.y * (190.0/1250.0));
        //Log.d("GameView constructor ", " screenX: " + screenX + " screenY: " + screenY);
        player = new Player(screenX, screenY);

        //spawn enemy
        enemy[0] = new Enemy(screenX, screenY, zombieBitMap);

        joystick = (JoystickView) findViewById(R.id.joystickRight);

    }

    public GameView(Context context, AttributeSet attributes)  {
        super(context, attributes);
        surfaceHolder = getHolder();
        paint = new Paint();



        //screenY = canvas.getHeight();
        //screenX = canvas.getWidth();
        //screenX = findViewById(R.id.gameView).getRight();
        //screenY = findViewById(R.id.gameView).getBottom();
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        screenX = size.x - (size.x * 0.05);
        screenY = size.y - (size.y * (190.0/1250.0));
        Log.d("GameView constructor ", " screenX: " + screenX + " screenY: " + screenY);
        player = new Player(screenX, screenY);

        //spawn enemy
        enemy[0] = new Enemy(screenX, screenY, zombieBitMap);


        joystick = (JoystickView) findViewById(R.id.joystickRight);
    }

    public GameView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        surfaceHolder = getHolder();
        paint = new Paint();

        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenX = size.x - (size.x * 0.05);
        screenY = size.y - (size.y * (190.0/1250.0));
        Log.d("GameView constructor ", " screenX: " + screenX + " screenY: " + screenY);
        player = new Player(screenX, screenY);

        //spawn enemy
        enemy[0] = new Enemy(screenX, screenY, zombieBitMap);

        joystick = (JoystickView) findViewById(R.id.joystickRight);
    }


    @Override
    public void run(){

        while (playing){

            long startFrameTime = System.currentTimeMillis();

            update();

            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if(timeThisFrame > 0){
                fps = 1000 / timeThisFrame;

            }

        }
        if(!playing){
            //Log.d("playing", playing + "");
        }
    }

    public void update(){

        //check if player has stopped moving
        //THIS ISNT WORKING
        /*if((System.nanoTime() - player.getTimeX())/ 1000000000.0 >= 0.1){
            player.setXPercent(0);
        }

        if((System.nanoTime() - player.getTimeY())/ 1000000000.0 >= 0.1){
            player.setYPercent(0);
        }*/


        //update player
        player.update(fps);
        //player.getHitBox().updatePoints(player.getAngle(), player.getRect());

        //update bullets
        for(int i = 0; i < bullet.length; i ++) {
            if (bullet[i] != null) {
                bullet[i].update(fps);
            }
        }
        //check if player is reloading. if not save time for when reload starts
        if(!reloading){
            reloadTime = System.nanoTime();
        }
        //MAYBE ADD RELOAD ANIMATION HERE

        //check if player is done reloading
        if(reloading && (System.nanoTime() - reloadTime)/1000000000.0 > 3){
            bulletCounter = 0;
            reloading = false;
            for(int i = 0; i < bullet.length; i ++ ){
                bullet[i] = null;
            }
        }




        //Update Each enemy;
        for(int i = 0; i < enemyCounter; i ++) {
            //Check if enemy exists
            if (enemy[i] != null && fps != 0) {
                enemy[i].update(fps, player.getRect().left, player.getRect().top, hurricaneSpeed, hurricaneAngle, hurricaneActive);
            }
        }


        //check if any enemy has been shot
        for(int j = 0; j < enemyCounter; j ++) {
            for (int i = 0; i < bullet.length; i++) {
                //if enemy has been shot spawn new enemy and destroy bullet
                if (bullet[i] != null && enemy != null) {
                    //Log.d("If Check:  " , "Enemy and bullet[" + i + "] do not equal null");

                    if (RectF.intersects(enemy[j].getHitBox(), bullet[i].getRect()) || RectF.intersects(bullet[i].getRect(), enemy[j].getHitBox())) {
                        //Log.d("If Check:  ", "Enemy and bullet[" + i + "] intersect");
                        bloodSplatters[enemiesKilled] = new BloodSplatter(enemy[j].getRect().left, enemy[j].getRect().top, rand.nextInt(4), enemy[j].getAngle());
                        enemiesKilled++;
                        enemy[j] = new Enemy(screenX, screenY, zombieBitMap);
                        bullet[i] = null;
                        playerScore += 10;




                        //increase enemy count by 1 every 5 enemies killed
                        if(playerScore > 0 && (playerScore % 50) == 0){
                            enemy[enemyCounter] = new Enemy(screenX, screenY, zombieBitMap);
                            enemyCounter ++;
                        }

                    }
                }
            }
        }


        for(int i = 0; i < enemyCounter; i ++) {
            Log.d(TAG, "update: enemy " + i);
            if (RectF.intersects(player.getHitBox1(), enemy[i].getHitBox()) || RectF.intersects(player.getHitBox2(), enemy[i].getRect())){


                //this.pause();
                Log.d("Pause", " Finished");

                playing = false;

                ZombieGame game = (ZombieGame)getContext();
                game.stopGame(playerScore);
            }
        }

        updateHurricane();
    }

    public void draw(){
        //Make sure drawing surface is valid or crash
        if(surfaceHolder.getSurface().isValid()) {

            canvas = surfaceHolder.lockCanvas();



           for(int i = 0; i < 1; i ++){
                for(int j = 0; j < 1; j++){
                    //Log.d(TAG, "draw: dayTile width and height: " + dayTile.getWidth() + "  " + dayTile.getHeight());
                    canvas.drawBitmap(dayTile, dayTile.getWidth() * j, dayTile.getHeight() * i, null);
                }
            }

            canvas.drawBitmap(leaf1, 500, 500, null);
            drawBloodSplatters();

            drawHurricaneArrow();


            for(int i = 0; i < bullet.length; i ++){
                if(bullet[i] != null){
                    canvas.save();
                    canvas.rotate((float)(bullet[i].getAngle() * (180.0/Math.PI) + 180) , bullet[i].getRect().centerX(), bullet[i].getRect().centerY() );
                    canvas.drawBitmap(tankshell, bullet[i].getRect().left, bullet[i].getRect().top, null);
                    canvas.restore();
                }
            }
            /*set bullet color and draw
            paint.setColor(Color.GREEN);
            for(int i = 0; i < bullet.length; i ++) {
                if (bullet[i] != null) {
                    canvas.drawRect(bullet[i].getRect(), paint);
                }
            }*/


            //canvas.drawBitmap(dayTile, 0, 0, null);
            //draw background
           // canvas.drawColor(Color.argb(255, 26, 128, 182));

            //set enemy color and draw each enemy
            paint.setColor(Color.MAGENTA);
            //enemySprite = walkAnimation.getSprite(enemy[0].getFrameCounter());
            for(int i = 0; i < enemyCounter; i ++) {
                if (enemy != null) {

                    canvas.save();

                    canvas.rotate((float)enemy[i].getAngle() * (float)(180.0/Math.PI), enemy[i].getRect().left + enemy[i].getWidth()/2, enemy[i].getRect().top + enemy[i].getHeight()/2);
                    canvas.drawBitmap(walkAnimation.getSprite(enemy[i].getFrameCounter()), enemy[i].getRect().left, enemy[i].getRect().top, paint);

                    canvas.restore();

                    //canvas.drawBitmap(enemySprite, null, rectF , paint);
                    //canvas.save(); //save the position of the canvas
                   // canvas.rotate(angle, X + (imageW / 2), Y + (imageH / 2)); //rotate the canvas
                    //canvas.drawBitmap(imageBmp, X, Y, null); //draw the image on the rotated canvas
                    //canvas.restore();  // restore the canvas position.
                    //Matrix matrix = new Matrix();

                   // matrix.setRotate((float)enemy[i].getAngle() * (float)(180/Math.PI));

                    //matrix.postScale(0.5f,0.5f);
                    //matrix.postTranslate(enemy[i].getRect().left - enemy[i].getRect().width() / 2, enemy[i].getRect().top- enemy[i].getRect().height() / 2);
                    //Bitmap rotatedImage = Bitmap.createBitmap(enemySprite, 0, 0, enemySprite.getWidth(), enemySprite.getHeight(), matrix, true );
                    /*RectF rectF = new RectF(enemy[i].getRect().left -((float) enemy[i].getWidth())/2,
                            enemy[i].getRect().top - ((float) enemy[i].getHeight())/2,
                            enemy[i].getRect().right + ((float) enemy[i].getWidth())/2,
                            enemy[i].getRect().bottom + ((float) enemy[i].getHeight())/2);*/
                    //canvas.drawBitmap(rotatedImage, (float)screenX/2 , (float)screenY/2,null);
                    //canvas.drawRect(enemy[i].getRect(),paint);
                    //canvas.drawBitmap(enemySprite, matrix, paint);
                }
            }

            //set player color and draw
            paint.setColor(Color.argb(255, 255, 1, 1));
            //canvas.drawRect(player.getRect(),paint);

            paint.setColor(Color.MAGENTA);
            //canvas.drawRect(player.getHitBox1(), paint);
            //canvas.drawRect(player.getHitBox2(), paint);


            //Draw Tank base
            canvas.save();
            canvas.rotate(((float)player.getAngle() )* (float)(180.0/Math.PI) - 90, player.getRect().left + (float)(player.getWidth()/2.0), player.getRect().top + (float)(player.getHeight()/2.0));
            canvas.drawBitmap(tankBase, player.getRect().left, player.getRect().top, null);
            canvas.restore();

            canvas.save();
            canvas.rotate(((float)player.getTankHeadAngle() ) * (float)(180.0/Math.PI) + 90, player.getRect().centerX(), player.getRect().centerY());
            canvas.drawBitmap(tankHead, player.getRect().left, player.getRect().top - 100, null);
            canvas.restore();



            if(hurricaneActive){
                drawLeaves();
            }



            //choose brush color for drawing
            paint.setColor(Color.argb(255, 249, 129, 0));

            //Increase text size and output fps
            paint.setTextSize(45);
            canvas.drawText("FPS: " + fps, 20, 40, paint);
            //output player score
            canvas.drawText("Score: " + playerScore, (float)screenX/2, 40, paint);


            //Show if player is reloading
            if(reloading){
                canvas.drawText("Reloading..." , (float)screenX/2, (float) screenY/4, paint);
            }


            //Draw everything to the screen and unlock the drawing surface
            surfaceHolder.unlockCanvasAndPost(canvas);
        }


    }

    //If engine is paused, shutdown thread
    public synchronized void pause(){
        playing = false;
        Log.d("fdsfds", "gameView.pause() called");
        try{
            gameThread.join();
        }catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume(){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();

    }

    public void updateHurricaneAngle(float pitch, float roll){
        hurricaneAngle =(float)( Math.atan2(pitch, roll) - (Math.PI/2) );

        //Log.d(TAG, "updateHurricaneAngle: " + (hurricaneAngle *(180/Math.PI)));
    }

    public void drawBloodSplatters(){

        for(int i = 0; i < enemiesKilled; i ++){
            canvas.save();
            canvas.rotate((float)(bloodSplatters[i].angle * 180.0/Math.PI), bloodSplatters[i].location.x + 65, bloodSplatters[i].location.y + 65);
            switch (bloodSplatters[i].splatter){
                case 0:
                    canvas.drawBitmap(bloodsplatter1, bloodSplatters[i].location.x, bloodSplatters[i].location.y, null);
                    break;
                case 1:
                    canvas.drawBitmap(bloodsplatter2, bloodSplatters[i].location.x, bloodSplatters[i].location.y, null);
                    break;
                case 2:
                    canvas.drawBitmap(bloodsplatter3, bloodSplatters[i].location.x, bloodSplatters[i].location.y, null);
                    break;
                case 3:
                    canvas.drawBitmap(bloodsplatter4, bloodSplatters[i].location.x, bloodSplatters[i].location.y, null);
                    break;
            }

            canvas.restore();
        }
    }

    public void drawLeaves(){
        for(int i = 0; i < leaf.length; i ++){
            if(leaf[i] != null){
                canvas.save();
                Log.d(TAG, "drawLeaves: " + leaf[i].getAngle());
                canvas.rotate((float)leaf[i].getAngle(), (float)leaf[i].getxLoc() + 25, (float)leaf[i].getyLoc() + 25);
                canvas.drawBitmap(leaf1, (int)leaf[i].getxLoc(), (int)leaf[i].getyLoc(), null);
                canvas.restore();
            }
        }
    }

    public void drawHurricaneArrow(){
        if(hurricaneAvailable) {
            canvas.save();
            canvas.rotate((float) Math.toDegrees(hurricaneAngle), (float) screenX - 300, (float) screenY - 300);
            //canvas.rotate((float)(hurricaneAngle * (180.0/Math.PI))-90, 250, 250);
            canvas.drawBitmap(hurricaneArrow, (float) screenX - 350, (float) screenY - 350, null);
            canvas.restore();
        }
    }

    public void updateHurricane(){
        if(hurricaneActive){



            for(int i = 0; i < leaf.length; i ++){
                if(leaf[i] != null) {
                    leaf[i].update(fps, hurricaneAngle, hurricaneSpeed);
                }
            }
            //update enemies
            Log.d(TAG, "updateHurricane: if(hurricaneActive): " + (System.nanoTime() - hurricaneTimer)/1000000000.0);
            //check if hurricane has ended
            if((System.nanoTime() - hurricaneTimer)/1000000000.0 > 4){
                if(leaf[1] != null) {
                    for (int i = 0; i < leaf.length; i++) {
                        leaf[i] = null;
                    }
                }
                hurricaneActive = false;
                hurricaneTimer = System.nanoTime();
            }
        }

        if(!hurricaneAvailable && (System.nanoTime() - hurricaneTimer)/1000000000.0 > 7){
            hurricaneAvailable = true;
            Log.d(TAG, "updateHurricane: hurricane on cooldown  " + (System.nanoTime() - hurricaneTimer)/1000000000.0);
        }
    }

    public void activateHurricane(){
        if(hurricaneAvailable){
            hurricaneActive = true;
            hurricaneAvailable = false;

            for(int i = 0; i < leaf.length; i ++){
                leaf[i] = new Leaf(screenX, screenY);
            }

            hurricaneTimer = System.nanoTime();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){

        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();
        switch ( motionEvent.getAction() & MotionEvent.ACTION_MASK){//ask dan about this
            case MotionEvent.ACTION_DOWN:
           // case MotionEvent.ACTION_MOVE:

                if(bulletCounter < 6) {
                    bullet[bulletCounter] = new Bullet(x, y, (int) player.getRect().centerX(), (int) player.getRect().centerY(), tankHead.getHeight());
                    bulletCounter++;
                }
                if(bulletCounter >= 6){
                    reloading = true;
                }
                player.updateTankHeadPosition(x, y);
                break;

            case MotionEvent.ACTION_UP:


                break;
        }

        return true;
    }


    public void onJoystickMoved(float xPercent, float yPercent, int id){
        //Log.e("Joystick:", "xPercent: " + xPercent + "    yPercent: " + yPercent + "    id: " + id);
        joyStickX = xPercent;
        joyStickY = yPercent;
    }


}//end of GameView