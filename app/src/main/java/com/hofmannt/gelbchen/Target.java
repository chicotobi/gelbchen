package com.hofmannt.gelbchen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.Random;

enum Type {
    Gelbchen, Enemy
}

public class Target {

    private Bitmap bitmap[];
    private int x[];
    private int y[];
    private float lifetime[];

    private Bitmap plus1,plus2,plus3,minus1;

    private int offsetX;
    private int offsetY;
    private int shiftX;
    private int shiftY;

    private Type type[];
    Context context;

    int FRAMES = 60;
    int nx;
    int ny;

    SoundPool soundPool;
    int good_sound;
    int bad_sound;

    private int targetCount;

    public Target(Context context, int targetCount, int screenX, int screenY, int level, Boolean classic) {

        this.targetCount = targetCount;
        this.context = context;
        nx = level + 2;
        ny = level + 3;

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        good_sound = soundPool.load(this.context, R.raw.sound_1, 1);
        bad_sound = soundPool.load(this.context, R.raw.sound_2, 1);


        Bitmap p1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gelbchen);
        Bitmap p2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gelbchen);
        Bitmap p3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gelbchen);
        Bitmap m1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        if(!classic) {
            if(level==1) {
                p1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.forest_trash_1);
                p2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.forest_trash_2);
                p3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.forest_trash_3);
                m1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.forest_good_1);
            } else if(level==2) {
                p1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.city_trash_1);
                p2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.city_trash_2);
                p3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.city_trash_3);
                m1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.city_good_1);
            } else if(level==3) {
                p1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.beach_trash_1);
                p2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.beach_trash_2);
                p3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.beach_trash_3);
                m1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.beach_good_1);
            }
        }

        shiftX = (int) (screenX / (float)nx);
        shiftY = (int) ((screenY - 50) / (float)ny);

        plus1 = Bitmap.createScaledBitmap(p1, shiftX, shiftY, false);
        plus2 = Bitmap.createScaledBitmap(p2, shiftX, shiftY, false);
        plus3 = Bitmap.createScaledBitmap(p3, shiftX, shiftY, false);
        minus1 = Bitmap.createScaledBitmap(m1, shiftX, shiftY, false);

        int bitmapX = plus1.getWidth();
        int bitmapY = plus1.getHeight();

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
                int type = randType.nextInt(3);
                if(type==0) {
                    bitmap[i] = plus1;
                } else if(type==1) {
                    bitmap[i] = plus2;
                } else if(type==2) {
                    bitmap[i] = plus3;
                }
                break;
            case Enemy:
                bitmap[i] = minus1;
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
            int idx_x = generator.nextInt(nx);
            int idx_y = generator.nextInt(ny);
            newx = offsetX + idx_x * shiftX;
            newy = offsetY + idx_y * shiftY;
            collision = false;
            for(int j=0;j<x.length;j++) {
                if(newx == x[j] && newy == y[j]) {
                    collision = true;
                }
                if(idx_x == 0 && idx_y == ny - 1) {
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
                soundPool.play(good_sound, 1, 1, 0, 0, 1);
            } else {
                value = -10;
                soundPool.play(bad_sound, 1, 1, 0, 0, 1);
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
