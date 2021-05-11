package com.example.tankwars2;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import static com.example.tankwars2.TankWarView.screenRatioX;
import static com.example.tankwars2.TankWarView.screenRatioY;

public class Tank {

    public int toShoot = 0;
    RectF rect;

    private Bitmap bitmap;
    private Bitmap bitmapup;
    private Bitmap bitmapleft;
    private Bitmap bitmapright;
    private Bitmap bitmapdown;
    public Bitmap currentBitmap;
    public int height;
    public int length;
    public float x;
    public float y;
    public int x2;
    public int y2;
    float missileWidth, missileHeight;
    // BULLET CODE
    Bitmap shoot1, shoot2, shoot3, shoot4, shoot5;
    int shootCounter = 1;

    //    private float tankSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;

    ///maybe more movement than this
    private int tankMoving;
    private int tankSpeed;
    private Bitmap tankHit;


    private TankWarView tankWarView;

    public Tank(TankWarView tankWarView, int screenX, int screenY, Resources res) {

        this.tankWarView = tankWarView;

        rect = new RectF();

        length = screenX / 5;
        height = screenY / 5;

        missileHeight = getHeight();
        missileWidth = getLength();


        missileWidth /= 4;
        missileHeight /= 4;

        x = screenX / 2;
        y = screenY / 2;

        tankSpeed = 350;
        bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot1);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (length),
                (int) (height),
                false);

        bitmapup = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        bitmapup = Bitmap.createScaledBitmap(bitmapup, (int) (length), (int) (height), false);


        bitmapright = BitmapFactory.decodeResource(res, R.drawable.tankright);
        bitmapright = Bitmap.createScaledBitmap(bitmapright, (int) (height), (int) (length), false);

        bitmapleft = BitmapFactory.decodeResource(res, R.drawable.tankleft);
        bitmapleft = Bitmap.createScaledBitmap(bitmapleft, (int) (length), (int) (height), false);

        bitmapdown = BitmapFactory.decodeResource(res, R.drawable.tankdown);
        bitmapdown = Bitmap.createScaledBitmap(bitmapdown, (int) (length), (int) (height), false);

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);

        shoot1 = Bitmap.createScaledBitmap(shoot1, (int) length, (int) height, false);
        shoot2 = Bitmap.createScaledBitmap(shoot2, (int) length, (int) height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, (int) length, (int) height, false);
        shoot4 = Bitmap.createScaledBitmap(shoot4, (int) length, (int) height, false);
        shoot5 = Bitmap.createScaledBitmap(shoot5, (int) length, (int) height, false);

        tankHit = BitmapFactory.decodeResource(res, R.drawable.shoot5);
        tankHit = Bitmap.createScaledBitmap(tankHit, length, height, false );


//        currentBitmap = getBitmap();
    }

    public void setMovementState(int state) {
        tankMoving = state;
    }


    public void update(long fps) {
        if (tankMoving == LEFT) {
            x = x - tankSpeed / fps;
            bitmap = bitmapleft;

        } else if (tankMoving == RIGHT) {
            x = x + tankSpeed / fps;
            bitmap = bitmapright;

        } else if (tankMoving == UP) {
            y = y - tankSpeed / fps;
            bitmap = bitmapdown;
        } else if (tankMoving == DOWN) {
            y = y + tankSpeed / fps;
            bitmap = bitmapdown;

        } else {
            bitmap = bitmapup;
        }

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;

    }


    public RectF getRect() {
        return rect;
    }

    Bitmap getBitmap() {
        if (toShoot != 0) {
            if (shootCounter == 1) {
                shootCounter++;

                return shoot1;
            }

            if (shootCounter == 2) {
                shootCounter++;

                return shoot2;
            }

            if (shootCounter == 3) {
                shootCounter++;

                return shoot3;
            }

            if (shootCounter == 4) {
                shootCounter++;

                return shoot4;
            }

            shootCounter = 1;
            toShoot--;
            tankWarView.newMissile();

            return shoot5;
        }
        return bitmap;
    }

    Rect getCollisionShape () {
        return new Rect((int) x, (int) y, (int) x + length, (int) y + height);
    }

    Bitmap tankHit () {
        return tankHit;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return height;
    }


}
