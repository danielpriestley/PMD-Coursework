package com.example.tankwars2;
import android.graphics.Rect;
import android.graphics.RectF;

public class Bullet {
    private float x;
    private float y;
    private RectF rect;

    TankWarView tankWarView;

    // which way it's shooting - always up for our implementation
    public int UP = 0;
    public int DOWN = 1;
    public int RIGHT = 2;
    public int LEFT = 3;


    // Going nowhere
    int heading = -1;
    float speed = 650;
    private int screenY;
    private int screenX;
    private int width;
    private int height;

    private boolean isActive;


    public Bullet(int screenX, int screenY){
        isActive = false;
        this.screenX = screenX;
        this.screenY = screenY;
        this.rect = new RectF();
    }

    public boolean shoot(float startX, float startY, int direction) {
        if (!isActive) {

            x = startX;
            y = startY;
            heading = direction;
            isActive = true;

            if ((direction == RIGHT)||(direction==LEFT))
            {  width = screenX/20;
                height = 1;}

            else{height = screenY/20;
                width = 1;}

            return true;
        }

        // Bullet already active
        return false;

    }

    public void update(long fps) {

        // Just move up or down
        if(heading == UP){
            y = y - speed / fps;
        }else if (heading == DOWN){
            y = y + speed / fps;
        }
        else if (heading == RIGHT){
            x = x + speed / fps;
        }

        else
        { x = x - speed / fps;}

        x = 50;
        // Update rect
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;
    }

    public RectF getRect(){
        return  rect;
    }

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }

    public float getImpactPointY() {
        if (heading == DOWN) {
            return y + height;
        }

        return y;

    }

    public float getImpactPointX() {
        if (heading == RIGHT){
            return  x + width; }

        return x;}
    //

    Rect getCollisionShape () {
        return new Rect((int) x, (int) y, (int) x + width, (int) y + height);
    }



}






