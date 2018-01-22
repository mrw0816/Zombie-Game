package com.vogella.android.zombiegame;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Ryan on 4/19/2017.
 */

public class HitBox {
    private static final String TAG = "HitBox";
    private Point tl, tr, bl, br;
    Path box;

    public HitBox(RectF rect){

        tl = new Point((int)rect.left, (int)rect.top);
        tr = new Point((int)rect.right, (int)rect.top);
        bl = new Point((int)rect.left, (int)rect.bottom);
        br = new Point((int)rect.right, (int)rect.bottom);
        box = new Path();
    }

    public Path drawBox(){
        box.reset();
        box.moveTo(tl.x, tl.y);
        box.lineTo(tr.x, tr.y);
        box.lineTo(br.x, br.y);
        box.lineTo(bl.x, bl.y);
        box.lineTo(tl.x, tl.y);

        return box;
    }

    public void updatePoints(double angle, RectF playerRect) {
        Log.d(TAG, "updatePoints: angle: " + (angle * (float) (180.0 / Math.PI)));
        angle = angle - (Math.PI / 2.0);
        int x = (int) playerRect.left;
        int y = (int) playerRect.top;

        tl.x = (int) ((Math.cos(angle) * (x - playerRect.centerX())) - (Math.sin(angle) * (y - playerRect.centerY())) + playerRect.centerX());
        tl.y = (int) ((Math.sin(angle) * (x - playerRect.centerX())) + (Math.cos(angle) * (y - playerRect.centerY())) + playerRect.centerY());

        x = (int) playerRect.right;
        y = (int) playerRect.top;

        tr.x = (int) ((Math.cos(angle) * (x - playerRect.centerX())) - (Math.sin(angle) * (y - playerRect.centerY())) + playerRect.centerX());
        tr.y = (int) ((Math.sin(angle) * (x - playerRect.centerX())) + (Math.cos(angle) * (y - playerRect.centerY())) + playerRect.centerY());

        x = (int) playerRect.right;
        y = (int) playerRect.bottom;

        br.x = (int) ((Math.cos(angle) * (x - playerRect.centerX())) - (Math.sin(angle) * (y - playerRect.centerY())) + playerRect.centerX());
        br.y = (int) ((Math.sin(angle) * (x - playerRect.centerX())) + (Math.cos(angle) * (y - playerRect.centerY())) + playerRect.centerY());

        x = (int) playerRect.left;
        y = (int) playerRect.bottom;

        bl.x = (int) ((Math.cos(angle) * (x - playerRect.centerX())) - (Math.sin(angle) * (y - playerRect.centerY())) + playerRect.centerX());
        bl.y = (int) ((Math.sin(angle) * (x - playerRect.centerX())) + (Math.cos(angle) * (y - playerRect.centerY())) + playerRect.centerY());
    }

    public double playerEnemyArea(RectF enemy){
        double enemyX = enemy.centerX();
        double enemyY = enemy.centerY();

        double area = 0;

        area = area + 0.5*( bl.x*br.y + br.x*enemyY + enemyX*bl.y - bl.y*br.x - br.y*enemyX - enemyY*bl.x);
        area = area + 0.5*( br.x*tr.y + tr.x*enemyY + enemyX*br.y - br.y*tr.x - tr.y*enemyX - enemyY*br.x);
        area = area + 0.5*(tr.x*tl.y + tl.x*enemyY + enemyX*tr.y - tr.y*tl.x - tl.y*enemyX - enemyY*tr.x);
        area = area + 0.5*(tl.x*bl.y + bl.x*enemyY + enemyX*tl.y - tl.y*bl.x - bl.y*enemyX - enemyY*tl.x);

        return area;
    }
}
