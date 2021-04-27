package com.example.tankwars2;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

public class Missile {
    int x, y, width, height;
    Bitmap missile;

    Missile (Resources res) {
        missile = BitmapFactory.decodeResource(res, R.drawable.shell);

        // defines the size of the missile
        width = 130;
        height = 250;

        missile = Bitmap.createScaledBitmap(missile,(int) width, (int) height,false);
    }

}

