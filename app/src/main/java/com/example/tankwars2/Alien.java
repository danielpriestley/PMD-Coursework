package com.example.tankwars2;
import android.content.res.Resources;
import android.graphics.Bitmap;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.tankwars2.TankWarView.screenRatioX;
import static com.example.tankwars2.TankWarView.screenRatioY;

public class Alien {
    public int speed = 30;
    public boolean wasHit = true;
    int x = 200, y = 0, length, height, alienCounter = 1;
    Bitmap alien1, alien2, alien3, alien4, alien5, alien6, alien7;

    Alien (Resources res) {
        alien1 = BitmapFactory.decodeResource(res, R.drawable.alien);
        alien2 = BitmapFactory.decodeResource(res, R.drawable.alien);
        alien3 = BitmapFactory.decodeResource(res, R.drawable.alien);
        alien4 = BitmapFactory.decodeResource(res, R.drawable.alien);
        alien5 = BitmapFactory.decodeResource(res, R.drawable.alien);
        alien6 = BitmapFactory.decodeResource(res, R.drawable.alien);
        alien7 = BitmapFactory.decodeResource(res, R.drawable.alien);

        length = 200;
        height = 200;

        alien1 = Bitmap.createScaledBitmap(alien1, length, height, false);
        alien2 = Bitmap.createScaledBitmap(alien2, length, height, false);
        alien3 = Bitmap.createScaledBitmap(alien3, length, height, false);
        alien4 = Bitmap.createScaledBitmap(alien4, length, height, false);
        alien5 = Bitmap.createScaledBitmap(alien5, length, height, false);
        alien6 = Bitmap.createScaledBitmap(alien6, length, height, false);
        alien7 = Bitmap.createScaledBitmap(alien7, length, height, false);

        y = +height;
    }

    Bitmap getAlien () {
        if (alienCounter == 1) {
            alienCounter ++;
            return alien1;
        }

        if (alienCounter == 2) {
            alienCounter ++;
            return alien2;
        }

        if (alienCounter == 3) {
            alienCounter ++;
            return alien3;
        }

        if (alienCounter == 4) {
            alienCounter ++;
            return alien4;
        }

        if (alienCounter == 5) {
            alienCounter ++;
            return alien5;
        }

        if (alienCounter == 6) {
            alienCounter ++;
            return alien6;
        }

        if (alienCounter == 7) {
            alienCounter ++;
            return alien7;
        }

        alienCounter = 1;

        return alien7;
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + 150, y + (height / 2));
    }
}
