package com.vogella.android.zombiegame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class ZombieGame extends Activity implements JoystickView.JoystickListener {

    /*NOTES
    use tilt to create a hurricane!!
     */

    private static final String TAG = "ZombieGame";
    private GameView gameView;
    private JoystickView joystick;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;

    private float gravity[];
    private float magnetic[];
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float values[] = new float[3];

    private float azimuth;
    private float pitch;
    private float roll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zombie_game);
        gameView = (GameView) findViewById(R.id.gameView);
        joystick = (JoystickView) findViewById(R.id.joystickRight);
        joystick.setZOrderMediaOverlay(true);
        joystick.setZOrderOnTop(true);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                switch(event.sensor.getType()){
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        mags = event.values.clone();
                        break;
                    case Sensor.TYPE_ACCELEROMETER:
                        accels = event.values.clone();
                        break;
                }

                if(mags != null && accels != null){
                    gravity = new float[9];
                    magnetic = new float[9];

                    SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
                    float[] outGravity = new float[9];

                    SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X, SensorManager.AXIS_Y, outGravity);
                    SensorManager.getOrientation(outGravity, values);
                    azimuth = values[0];
                    pitch = values[1];
                    roll = values[2];
                    mags = null;
                    accels = null;

                    //1Log.d(TAG, "onSensorChanged: azimuth: " + (azimuth*(180/Math.PI)));
                    //Log.d(TAG, "onSensorChanged: ptich: " + pitch);
                    //Log.d(TAG, "onSensorChanged: roll: " + roll);

                    double norm = Math.sqrt(azimuth*azimuth + pitch*pitch + roll*roll);

                    pitch = (float)(pitch/norm);
                    roll = (float)(roll/norm);
                    pitch = -pitch;

                    gameView.updateHurricaneAngle(pitch, roll);


                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME );
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);

    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (parent != null) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void stopGame(int score) {
        sensorManager.unregisterListener(sensorEventListener);
        Log.d("ZombieGame", " stopGame() called");
        Intent result = new Intent();
        result.putExtra("score", score);
        setResult(1, result);

        finish();
    }


    protected void onResume() {
        gameView.resume();
        super.onResume();


    }

    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        gameView.pause();
        super.onPause();


    }

  /*  protected void onStop(){
        Log.d("sdfds", "onStop() called");
        gameView.pause();
        super.onStop();


    }
    protected void onRestart(){
        Log.d("sdfsd", "onRestart() called");
        gameView.resume();
        super.onRestart();


    }*/


    public void onJoystickMoved(float xPercent, float yPercent, int id) {

        switch (id) {
            case R.id.joystickRight:
                //Log.d("Right Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
                if (xPercent != 0) {
                    gameView.player.setXPercent(xPercent);
                }
                if (yPercent != 0) {
                    gameView.player.setYPercent(yPercent);
                }
                break;

        }
    }

    public void activateHurricane(View view){
        gameView.activateHurricane();
    }
}


