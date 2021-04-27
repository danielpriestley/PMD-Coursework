package com.example.tankwars2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

//import androidx.constraintlayout.widget.ConstraintSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TankWarView extends SurfaceView implements Runnable {

    private static int score;
    private Tank tank;
    private Bullet bullet;
    private boolean missileFired;
    private Bitmap shootButton;
    Bitmap shell;
    public static float screenRatioX, screenRatioY;

    private float shootButtonLength = 300;
    private float shootButtonHeight = 300;
    private int shootButtonX = 750;
    private int shootButtonY = 1230;
    private boolean shootButtonClicked = false;

    private List<Missile> missiles;

    private Background background1, background2;


    private Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    private boolean paused = false;
    private Canvas canvas;
    private Paint paint;
    private long fps;
    private long timeThisFrame;
    private int screenX;
    private int screenY;
    //    private int score = 0;
    private int lives = 5;
//    private int tankDirection = 0;


    // this is the constructor
    public TankWarView(Context context, int x, int y) {

        super(context);
        this.context = context;

        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;


        missiles = new ArrayList<>();

        tank = new Tank(this, screenX, screenY, getResources());
        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.y = screenY;


        initLevel();
    }

    public void newMissile() {
        // Creates a missile
        Missile missile = new Missile(getResources());
        // Assigns missile the X + Y values of the tank
        missile.x = (int) (tank.getX() + (tank.getLength() / 4));
        missile.y = (int) (tank.getY() - (tank.getHeight() / 2));
        // score for testing purposes to ensure this runs
        score = score + 10;
        // adds the newly created missile to the missiles ArrayList
        missiles.add(missile);
    }


//    private void prepareLevel(){
//        tank = new Tank(context, screenX, screenY);
//    }

    public void initLevel() {

//        tank = new Tank(this, screenX, screenY);
        bullet = new Bullet(screenY, screenX);

    }

    public void addScore() {

//        tank = new Tank(this, screenX, screenY);
        score = score + 100;
    }

    private void CheckCollisions() {

        boolean tankCollision = false;
        boolean missileCollision;

        // tank collision code

        if (tank.x < 0) {
            tank.x = 0;
        }

        if (tank.x >= screenX - tank.getLength()) {
            tank.x = screenX - tank.getLength();
        }
    }


    @Override
    public void run() {
        while (playing) {

            long startFrameTime = System.currentTimeMillis();

            if (!paused) {
                update();
            }

            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }


    private void update() {
        tank.update(fps);



        List<Missile> trash = new ArrayList<>();

        for (Missile missile : missiles) {
            if (missile.y > screenY)
                trash.add(missile);

            // defines speed of missile
            missile.y -= 100 * screenRatioY;
        }

        for (Missile missile : trash)
            missiles.remove(missile);


        CheckCollisions();




        // code for animated backdrop
//        background1.y = background1.y + 20;
//        background2.y = background1.y + 20;
//
//        if (background1.y + background1.background.getHeight() < 0) {
//            background1.y = screenY;
//        }
//
//        if (background2.y + background2.background.getHeight() < 0) {
//            background2.y = screenY;
//        }

//        if (bullet.getStatus()){
//            score = score + 10000;
//            bullet.update(fps);
//        }


    }


    private void draw() {

        if (ourHolder.getSurface().isValid()) {

            shootButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.testpng);
            shootButton = Bitmap.createScaledBitmap(shootButton, (int) (shootButtonLength), (int) (shootButtonHeight), false);

            canvas = ourHolder.lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawBitmap(tank.getBitmap(), tank.getX(), tank.getY(), paint);
            canvas.drawBitmap(shootButton, shootButtonX, shootButtonY, paint);
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

            if (bullet.getStatus()) {
                canvas.drawRect(bullet.getRect(), paint);
                shell = BitmapFactory.decodeResource(context.getResources(), R.drawable.shell);
                shell = Bitmap.createScaledBitmap(shell, (int) (100), (int) (30), false);
                canvas.drawBitmap(shell, 0, 0, paint);
            }

            for (Missile missile : missiles)
                // draws the image for every missile fired, draws where the tank is
                canvas.drawBitmap(missile.missile, missile.x, missile.y, paint);

            ourHolder.unlockCanvasAndPost(canvas);


        }


    }





    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }


    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                // if the finger tap is within the parameters of the shootButton
                if (x > shootButtonX && x < shootButtonX + shootButtonLength &&
                    y > shootButtonY && y < shootButtonY + shootButtonHeight) {
                    // shootButtonClicked is used to prevent tank moving when shoot is clicked
                    shootButtonClicked = true;
                    // adds 1 to toShoot within the tank class
                    tank.toShoot++;
                }

                // if the tap's X value is MORE than HALF of the screens X value
                if (event.getX() > screenX / 2 && !shootButtonClicked) {
                    tank.setMovementState((tank.RIGHT));
                } else if (event.getX() < screenX / 2 && !shootButtonClicked) {
                    tank.setMovementState(tank.LEFT);
                }


                break;

            case MotionEvent.ACTION_UP:
                shootButtonClicked = false;
                tank.setMovementState(tank.STOPPED);
                break;
        }
        return true;
    }


}  // end class
