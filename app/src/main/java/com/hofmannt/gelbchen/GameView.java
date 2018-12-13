package com.hofmannt.gelbchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Target targets;
    private int targetCount;

    private int time;
    private int points;

    private String username;

    boolean evaluate;

    String names[] = new String[5];
    int highScore[] = new int[5];

    Context context;

    SharedPreferences sharedPreferences;

    int FRAMES = 60;

    public GameView(Context context, int screenX, int screenY, String username) {
        super(context);

        this.context = context;

        surfaceHolder = getHolder();
        paint = new Paint();

        time = FRAMES * 20;

        targetCount = 2;

        evaluate = true;

        this.username = username;

        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setAntiAlias(true);

        targets = new Target(context, targetCount, screenX, screenY);

        sharedPreferences = context.getSharedPreferences("DATA_GELBCHEN", Context.MODE_PRIVATE);
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
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            if (time > 0) {
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
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
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
                for (int i = 0; i < targetCount; i++) {
                    points += targets.checkHit(i, x, y);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return true;
    }

}
