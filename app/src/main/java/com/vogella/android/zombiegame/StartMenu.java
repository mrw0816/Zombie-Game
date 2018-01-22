package com.vogella.android.zombiegame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class StartMenu extends AppCompatActivity {

    private static final String TAG = "StartMenu";

    private int score = 0;
    private TextView scoreText;
    private int[] highScores = new int[5];
    private SharedPreferences sharePref;
    private SharedPreferences.Editor editor;


    private static final int REQUEST_CODE_CAPTURE_PERM = 1234;


    TextView score1;
    TextView score2;
    TextView score3;
    TextView score4;
    TextView score5;

    TextView hsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);
        score4 = (TextView) findViewById(R.id.score4);
        score5 = (TextView) findViewById(R.id.score5);

        hsTextView = (TextView) findViewById(R.id.highScores);

        sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharePref.edit();

        for(int i = 0; i < highScores.length; i ++) {
            Log.d(TAG, "onCreate: " + sharePref.getInt("score" + i , 0));
            highScores[i] = sharePref.getInt("score" + i , 0);
            Log.d(TAG, "onCreate: highscore[" + i + "]: " + highScores[i]);
        }

        hsTextView.setText("High Scores");



        scoreText = (TextView) findViewById(R.id.scoreText);
        scoreText.setText("Last Score: " + score);


        setHighScore();




    }

    protected void setHighScore(){
        score1.setText(highScores[0] + "");
        score2.setText(highScores[1] + "");
        score3.setText(highScores[2] + "");
        score4.setText(highScores[3] + "");
        score5.setText(highScores[4] + "");
    }

    protected void startGame(View v){



        final AlertDialog.Builder record = new AlertDialog.Builder(this);
        record.setMessage("Are you recording currently?");

        record.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivityForResult(new Intent(StartMenu.this, ZombieGame.class), 1);
                Log.d(TAG, "onClick: Player already recording gameplay.");
            }
        });

        record.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                record.setMessage("Would you like to record gameplay?");
                record.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openApp(getBaseContext(), "com.hecorat.screenrecorder.free");

                        record.setMessage("Start Recording! Are you ready to die?");
                        record.setPositiveButton("Ready", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d(TAG, "onClick: woohoo");
                                startActivityForResult(new Intent(StartMenu.this, ZombieGame.class), 1);
                            }
                        });
                        record.setNegativeButton(null, null);
                        record.create().show();
                    }
                });

                record.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(StartMenu.this, ZombieGame.class), 1);
                    }
                });
                record.create().show();
            }
        });

        record.create().show();





       /* Intent intent = new Intent("com.hecorat.screenrecorder.free.app.AzRecorderApp");
        String title = "Choose and app to record game With.";

        Intent chooser = Intent.createChooser(intent, title);
        Log.d(TAG, "startGame: " + (intent.resolveActivity(getPackageManager()) != null));
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(chooser);
            //startActivityForResult(new Intent(StartMenu.this, ZombieGame.class), 1);
            Log.d(TAG, "startGame: if");
        }else{
            //startActivityForResult(new Intent(StartMenu.this, ZombieGame.class), 1);
            Log.d(TAG, "startGame: else");
        }
        */


    }
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new PackageManager.NameNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 1) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

            ///editor.clear();
        /*for(int i = 0; i < highScores.length; i ++){

                editor.putInt("score" + i, )

        }*/

            //check if current score beats a highscore

            score = data.getIntExtra("score", 0);

            scoreText.setText("Last Score: " + score);

            for (int i = 0; i < highScores.length; i++) {
                if (score > highScores[i]) {
                    for (int j = highScores.length - 1; j > i; j--) {
                        highScores[j] = highScores[j - 1];
                    }

                    highScores[i] = score;
                    break;
                }
            }

            //store highscores
            for (int i = 0; i < highScores.length; i++) {
                sp.edit().putInt("score" + i, highScores[i]).commit();
            }


            setHighScore();

        }
    }

}
