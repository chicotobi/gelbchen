package com.hofmannt.gelbchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private final int bin_x;
    private Thread gameThread = null;

    private Bitmap background;

    private Bitmap bin_green;
    private Bitmap bin_red;
    private final int bin_y;
    private final Paint paint;
    private final SurfaceHolder surfaceHolder;
    private final Target targets;
    private final String username;
    private int targetCount;

    private int time;
    private int points;
    private final String[] names = new String[5];
    private final int[] highScore = new int[5];
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final int FRAMES = 60;
    private final boolean classic;
    private final int shiftX;
    private final int shiftY;
    private volatile boolean playing;
    private int bin_wiggle_counter;
    private boolean evaluate;

    public GameView(Context context, int screenX, int screenY, String username, Boolean classic_) {
        super(context);
        classic = classic_;

        this.context = context;

        surfaceHolder = getHolder();
        paint = new Paint();

        time = FRAMES * 20;

        evaluate = true;

        this.username = username;

        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setAntiAlias(true);

        bin_wiggle_counter = 0;

        sharedPreferences = context.getSharedPreferences("DATA_GELBCHEN", Context.MODE_PRIVATE);

        int level = sharedPreferences.getInt("level", 1);

        if(level==1) {
            targetCount = 2;
        } else if(level==2) {
            targetCount = 3;
        } else if(level==3) {
            targetCount = 5;
        }

        if(classic) {
            background = Bitmap.createBitmap(screenX, screenY, Bitmap.Config.ARGB_8888);
        } else {
            if(level==1) {
                background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_forest);
            } else if(level==2) {
                background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_beach);
            } else if(level==3) {
                background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_city);
            }
        }
        background = Bitmap.createScaledBitmap(background, screenX, screenY,false);

        targets = new Target(context, targetCount, screenX, screenY, level, classic);
        highScore[0] = sharedPreferences.getInt("score0", 0);
        highScore[1] = sharedPreferences.getInt("score1", 0);
        highScore[2] = sharedPreferences.getInt("score2", 0);
        highScore[3] = sharedPreferences.getInt("score3", 0);
        highScore[4] = sharedPreferences.getInt("score4", 0);
        names[0] = sharedPreferences.getString("name0", "");
        names[1] = sharedPreferences.getString("name1", "");
        names[2] = sharedPreferences.getString("name2", "");
        names[3] = sharedPreferences.getString("name3", "");
        names[4] = sharedPreferences.getString("name4", "");

        // Set values for bin_green
        bin_green = BitmapFactory.decodeResource(context.getResources(), R.drawable.bin_green);
        bin_red = BitmapFactory.decodeResource(context.getResources(), R.drawable.bin_red);

        shiftX = (int) (screenX / (float)(level+2));
        shiftY = (int) ((screenY - 50) / (float)(level+3));
        bin_green = Bitmap.createScaledBitmap(bin_green, shiftX, shiftY,false);
        bin_red = Bitmap.createScaledBitmap(bin_red, shiftX, shiftY,false);
        bin_x = 0;
        bin_y = 50 + (level+2) * shiftY;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
            time -= 1;
            if (time < (-1)*FRAMES) {
                updateHighScore();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }
    }

    private void update() {
        targets.update();
    }

    private void updateHighScore() {
        if (evaluate) {
            evaluate = false;


            for (int i = 0; i < 5; i++) {
                if (highScore[i] < points) {
                    for (int j = 4; j > i; j--) {
                        names[j] = names[j - 1];
                        highScore[j] = highScore[j - 1];
                    }
                    names[i] = username;
                    highScore[i] = points;
                    break;
                }
            }

            SharedPreferences.Editor e = sharedPreferences.edit();
            for (int i = 0; i < 5; i++) {
                e.putString("name" + i, names[i]);
                e.putInt("score" + i, highScore[i]);
            }
            e.apply();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (time > 0) {
                if(classic) {
                    canvas.drawColor(Color.WHITE);
                } else {
                    canvas.drawBitmap(background,0,0,paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.WHITE);
                    canvas.drawRect(0,0,500,50,paint);
                    paint.setColor(Color.BLACK);
                }
                for (int i = 0; i < targetCount; i++) {
                    canvas.drawBitmap(
                            targets.getBitmap(i),
                            targets.getX(i),
                            targets.getY(i),
                            paint
                    );
                }
                canvas.drawText("Name:" + username + "  Score: " + points + "  Time: " + time/FRAMES, 0, 40, paint);
                canvas.drawLine(0, 50, 500, 50, paint);
                if (bin_wiggle_counter > 0) {
                    bin_wiggle_counter--;
                    Matrix matrix = new Matrix();

                    float[] degrees = {355, 350, 345, 350, 355, 0, 5, 10, 15, 20};

                    matrix.setTranslate(bin_green.getWidth(), bin_green.getHeight());
                    matrix.postRotate(degrees[bin_wiggle_counter], bin_green.getWidth(), bin_green.getHeight());
                    Bitmap rotated_bin = Bitmap.createBitmap(bin_green, 0, 0, bin_green.getWidth(), bin_green.getHeight(), matrix, true);
                    Bitmap rescaled_bin = Bitmap.createScaledBitmap(rotated_bin, shiftX, shiftY, false);
                    canvas.drawBitmap(rescaled_bin, bin_x, bin_y, paint);
                } else if (bin_wiggle_counter < 0) {
                    bin_wiggle_counter++;
                    Matrix matrix = new Matrix();

                    float[] degrees = {355, 350, 345, 350, 355, 0, 5, 10, 15, 20};

                    matrix.setTranslate(bin_red.getWidth(), bin_red.getHeight());
                    matrix.postRotate(degrees[-bin_wiggle_counter], bin_red.getWidth(), bin_red.getHeight());
                    Bitmap rotated_bin = Bitmap.createBitmap(bin_red, 0, 0, bin_red.getWidth(), bin_red.getHeight(), matrix, true);
                    Bitmap rescaled_bin = Bitmap.createScaledBitmap(rotated_bin, shiftX, shiftY, false);
                    canvas.drawBitmap(rescaled_bin, bin_x, bin_y, paint);
                } else {
                    canvas.drawBitmap(bin_green, bin_x, bin_y, paint);
                }
            } else {
                paint.setTextSize(60);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Final score: " + points, canvas.getWidth() / 2, canvas.getHeight() / 2, paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException ignored) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (time < 0) {
            return true;
        }
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                int old_points = points;
                for (int i = 0; i < targetCount; i++) {
                    points += targets.checkHit(i, x, y);
                }
                if(points>old_points) {
                    bin_wiggle_counter = 10;
                } else if(points < old_points) {
                    bin_wiggle_counter = -10;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }
}
