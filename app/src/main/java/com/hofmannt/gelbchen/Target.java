package com.hofmannt.gelbchen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

enum Type {
    Gelbchen, Enemy
}

public class Target {

    private Bitmap bitmap[];
    private int x[];
    private int y[];
    private float lifetime[];

    private Bitmap gelbchen;
    private Bitmap enemy;

    private int offsetX;
    private int offsetY;
    private int shiftX;
    private int shiftY;

    private Type type[];

    int FRAMES = 60;

    private int targetCount;

    public Target(Context context, int targetCount, int screenX, int screenY) {

        gelbchen = BitmapFactory.decodeResource(context.getResources(), R.drawable.gelbchen);
        enemy = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);

        int bitmapX = gelbchen.getWidth();
        int bitmapY = gelbchen.getHeight();

        this.targetCount = targetCount;

        shiftX = (int) (screenX / 3.);
        shiftY = (int) ((screenY - 50) / 4.);
        offsetX = (int) ((shiftX - bitmapX) / 2.);
        offsetY = (int) ((shiftY - bitmapY) / 2. + 50);

        x = new int[targetCount];
        y = new int[targetCount];
        lifetime = new float[targetCount];
        type = new Type[targetCount];
        bitmap = new Bitmap[targetCount];

        for (int i = 0; i < targetCount;i++) {
            x[i] = -1;
            y[i] = -1;
            reset(i);
        }
    }

    public void update() {
        for(int i=0;i<targetCount;i++) {
            lifetime[i] -= 1;
            if (lifetime[i] <= 0) {
                reset(i);
            }
        }
    }

    private void reset(int i) {

        Random randType = new Random();
        if(randType.nextInt(10)==0) {
            type[i] = Type.Enemy;
        } else {
            type[i] = Type.Gelbchen;
        }

        switch (type[i]) {
            case Gelbchen:
                bitmap[i] = gelbchen;
                break;
            case Enemy:
                bitmap[i] = enemy;
                break;
        }

        float minLifetime = 1;
        float maxLifetime = 3;

        Random generator = new Random();
        lifetime[i] =  minLifetime + (maxLifetime-minLifetime) * generator.nextFloat();
        lifetime[i] *= FRAMES;

        int newx,newy;
        boolean collision;
        do {
            newx = offsetX + generator.nextInt(3) * shiftX;
            newy = offsetY + generator.nextInt(4) * shiftY;
            collision = false;
            for(int j=0;j<x.length;j++) {
                if(newx == x[j] && newy == y[j]) {
                    collision = true;
                }
            }
        } while(collision);
        x[i] = newx;
        y[i] = newy;
    }

    public int checkHit(int i, float xx, float yy) {
        int value = 0;
        if(x[i] < xx & xx < x[i]+bitmap[i].getWidth() & y[i] < yy & yy < y[i]+bitmap[i].getHeight()) {
            if(type[i] == Type.Gelbchen) {
                value =  1;
            } else {
                value = -10;
            }
            reset(i);
        }
        return value;
    }

    //getters
    public Bitmap getBitmap(int i) {
        return bitmap[i];
    }

    public int getX(int i) {
        return x[i];
    }

    public int getY(int i) {
        return y[i];
    }

}
