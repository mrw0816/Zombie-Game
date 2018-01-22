package com.vogella.android.zombiegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by Ryan on 4/3/2017.
 */

public class Animation {


    private Bitmap sheet;
    private int numberOfFrames;
    private int startColumn;
    private int startRow;
    private int width;
    private int height;
    private ArrayList<Bitmap> bitmapArray;

    private long frameTime;
    private int frameCounter;

    public Animation(Bitmap s, int n, int w, int h, int row, int column){
        startColumn = column;
        startRow = row;
        sheet = s;
        numberOfFrames = n;
        width = w;
        height = h;
        frameTime = System.currentTimeMillis();
        bitmapArray = new ArrayList<Bitmap>();
        frameCounter = 0;
        createAnimationArray();
    }

    private void createAnimationArray(){
        Bitmap temp = Bitmap.createBitmap(sheet, 0 , 0, width, height );
        //row 6 column 5
        for(int i = 0; i < numberOfFrames; i ++){
            temp = Bitmap.createBitmap(sheet, (startColumn - 1) * (width) + i*width, (startRow - 1) * height, width, height );

            bitmapArray.add(Bitmap.createScaledBitmap(temp, 130, 130, false));
        }
    }

    public Bitmap getSprite(int frameCounter){
        return bitmapArray.get(frameCounter);
    }
}
