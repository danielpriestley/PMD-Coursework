package com.example.tankwars2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import java.util.Random;

public class TankWarView extends SurfaceView implements Runnable {

    private static int score;
    private Tank tank;
    private boolean missileFired;
    private Bitmap shootButton;
    private boolean isTankHit = false;
    public static float screenRatioX, screenRatioY;

    // shoot buttons
    private float shootButtonLength = 300;
    private float shootButtonHeight = 300;
    private int shootButtonX = 750;
    private int shootButtonY = 1230;
    private boolean shootButtonClicked = false;

    // control buttons
    private Bitmap controlUp;
    private Bitmap controlDown;
    private Bitmap controlLeft;
    private Bitmap controlRight;
    private float controlButtonLength = 100;
    private float controlButtonHeight = 100;
    // UP BUTTON
    private int controlUpX = 150;
    private int controlUpY = 1200;
    // DOWN BUTTON
    private int controlDownX = 150;
    private int controlDownY = 1400;
    // LEFT BUTTON
    private int controlLeftX = 40;
    private int controlLeftY = 1300;
    // RIGHT BUTTON
    private int controlRightX = 260;
    private int controlRightY = 1300;

    // ALIENS
    private Alien[] aliens;
    private Random random;
    private Random myRan;

    private Bitmap resetButton;
    private int resetButtonX = 300;
    private int resetButtonY = 600;
    private int resetButtonLength = 400;
    private int resetButtonHeight = 100;

    private List<Missile> missiles;

    private Background background1, background2;


    private Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    private boolean paused = true;
    private Canvas canvas;
    private Paint paint;
    private long fps;
    private long timeThisFrame;
    private int screenX;
    private int screenY;
    private int lives = 5;
    private boolean gameIsOver = false;


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

        initLevel();
    }

    public void newMissile() {
        // Creates a missile
        Missile missile = new Missile(getResources());

        // Assigns missile the X + Y values of the tank
        missile.x = (int) (tank.getX() + (tank.getLength() / 4));
        missile.y = (int) (tank.getY() - (tank.getHeight() / 2));

        // adds the newly created missile to the missiles ArrayList
        missiles.add(missile);
    }


    public void initLevel() {

        // alien code
        aliens = new Alien[7];
        for (int i = 0; i < 7; i++) {
            myRan = new Random();
            int bound = (int) (1800);
            int randX = myRan.nextInt(bound);
            Alien alien = new Alien(getResources());
            aliens[i] = alien;
            aliens[i].x = randX + 150;
        }

        random = new Random();


        missiles = new ArrayList<>();

        tank = new Tank(this, screenX, screenY, getResources());
        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.y = screenY;

    }



    private void CheckCollisions() {

        // tank collision code

        if (tank.x < 0) {
            tank.x = 0;
        }

        if (tank.x >= screenX - tank.getLength()) {
            tank.x = screenX - tank.getLength();
        }

        if (tank.y >= screenY - tank.getHeight()) {
            tank.y = screenY - tank.getHeight();
        }

        if (tank.y < 0) {
            tank.y = 0;
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

        if (lives <= 0) {
            restart();
        }

        // Missile code
        List<Missile> trash = new ArrayList<>();

        // Missiles added to trash when off screen
        for (Missile missile : missiles) {
            if (missile.y > screenY)
                trash.add(missile);

            // defines speed of missile
            missile.y -= 120 * screenRatioY;

            for(Alien alien : aliens) {
                if (Rect.intersects(alien.getCollisionShape(), missile.getCollisionShape())) {
                    alien.y = screenY + 500;
                    trash.add(missile);
                    alien.wasHit = true;
                    score = score + 10;
                }
            }
        }

        for (Missile missile : trash)
            missiles.remove(missile);

//         Alien Code
        for (Alien alien : aliens) {
            alien.y += alien.speed;


            if (alien.y + alien.height > 1600) {

                int bound = (int) (90 * screenRatioY);
                alien.speed = random.nextInt(bound);

                if (alien.speed < 12 * screenRatioY) {
                    alien.speed = (int) (50 * screenRatioY);
                }

                alien.y = 30;
                alien.x = random.nextInt(screenX - alien.length);

                alien.wasHit = false;
            }

            // alien collision code
            if (Rect.intersects(alien.getCollisionShape(), tank.getCollisionShape())) {
                isTankHit = true;
                reset();
                return;
            }
        }


        CheckCollisions();


    }


    private void draw() {

        if (ourHolder.getSurface().isValid()) {

            canvas = ourHolder.lockCanvas();

            shootButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.testpng);
            shootButton = Bitmap.createScaledBitmap(shootButton, (int) (shootButtonLength), (int) (shootButtonHeight), false);

            resetButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.reset);
            resetButton = Bitmap.createScaledBitmap(resetButton, resetButtonLength, resetButtonHeight, false);


            // control buttons
            controlUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.controlup);
            controlUp = Bitmap.createScaledBitmap(controlUp, (int) (controlButtonLength), (int) (controlButtonHeight), false);
            controlDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.controldown);
            controlDown = Bitmap.createScaledBitmap(controlDown, (int) (controlButtonLength), (int) (controlButtonHeight), false);
            controlLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.controlleft);
            controlLeft = Bitmap.createScaledBitmap(controlLeft, (int) (controlButtonLength), (int) (controlButtonHeight), false);
            controlRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.conrolright);
            controlRight = Bitmap.createScaledBitmap(controlRight, (int) (controlButtonLength), (int) (controlButtonHeight), false);

            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawBitmap(tank.getBitmap(), tank.getX(), tank.getY(), paint);
            canvas.drawBitmap(shootButton, shootButtonX, shootButtonY, paint);
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);


            for (Alien alien : aliens)
                canvas.drawBitmap(alien.getAlien(), alien.x, alien.y, paint);

            // control buttons
            canvas.drawBitmap(controlUp, controlUpX, controlUpY, paint);
            canvas.drawBitmap(controlDown, controlDownX, controlDownY, paint);
            canvas.drawBitmap(controlLeft, controlLeftX, controlLeftY, paint);
            canvas.drawBitmap(controlRight, controlRightX, controlRightY, paint);

            for (Missile missile : missiles)
                // draws the image for every missile fired, draws where the tank is
                canvas.drawBitmap(missile.missile, missile.x, missile.y, paint);

            // isTankHit
            if (isTankHit) {
                canvas.drawBitmap(tank.tankHit(), tank.x, tank.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                isTankHit = false;
                return;
            }
            
            if (gameIsOver) {
                canvas.drawText("GAME OVER", 300, 500, paint);
                canvas.drawText("YOUR SCORE WAS: " + score, 300, 550, paint);
                canvas.drawBitmap(resetButton, resetButtonX, resetButtonY, paint);
            }


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

    public void reset() {
        playing = false;
        lives--;

        try {
            Thread.sleep(500);
            for (Alien alien : aliens) {
                alien.y = 0;
                tank.y = (screenY / 2);
                tank.x = (screenX / 2);
                myRan = new Random();
                int bound = (int) (700);
                int randX = myRan.nextInt(bound);
                alien.x = randX + 150;


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        resume();
    }


    public void restart() {
        playing = false;
        gameIsOver = true;

        try {
            Thread.sleep(10000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void newGame() {
        playing = true;
        gameIsOver = false;
        lives = 5;
        score = 0;
        resume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if (gameIsOver = true && lives == 0) {
                    if (x > resetButtonX && x < resetButtonX + resetButtonLength &&
                            y > resetButtonY && y < resetButtonY + resetButtonHeight) {
                        newGame();
                    }
                }


                // if the finger tap is within the parameters of the shootButton
                if (x > shootButtonX && x < shootButtonX + shootButtonLength &&
                    y > shootButtonY && y < shootButtonY + shootButtonHeight) {
                    // shootButtonClicked is used to prevent tank moving when shoot is clicked
                    shootButtonClicked = true;
                    // adds 1 to toShoot within the tank class
                    tank.toShoot++;
                }


                // CODE FOR CONTROL BUTTONS

                // UP BUTTON
                if (x > controlUpX && x < controlUpX + controlButtonLength &&
                        y > controlUpY && y < controlUpY + controlButtonHeight) {
                    // tank moves up when button clicked
                    tank.setMovementState(tank.UP);
                }
                // DOWN BUTTON
                if (x > controlDownX && x < controlDownX + controlButtonLength &&
                        y > controlDownY && y < controlDownY + controlButtonHeight) {
                    // tank moves up when button clicked
                    tank.setMovementState(tank.DOWN);
                }
                // LEFT BUTTON
                if (x > controlLeftX && x < controlLeftX + controlButtonLength &&
                        y > controlLeftY && y < controlLeftY + controlButtonHeight) {
                    // tank moves up when button clicked
                    tank.setMovementState(tank.LEFT);
                }
                // RIGHT BUTTON
                if (x > controlRightX && x < controlRightX + controlButtonLength &&
                        y > controlRightY && y < controlRightY + controlButtonHeight) {
                    // tank moves up when button clicked
                    tank.setMovementState(tank.RIGHT);
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
