package com.vogella.android.zombiegame;

import android.graphics.Point;

/**
 * Created by Ryan on 4/24/2017.
 */

public class BloodSplatter {

    Point location;
    int splatter;
    double angle;

    public BloodSplatter(double x, double y, int splat, double ang){
        location = new Point((int)x, (int)y);
        splatter = splat;
        angle = ang;
    }

}
