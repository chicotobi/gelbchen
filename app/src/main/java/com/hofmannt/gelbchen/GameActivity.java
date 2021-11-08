package com.hofmannt.gelbchen;

import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;

import static java.lang.Boolean.FALSE;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        String username = intent.getStringExtra("name");
        Boolean classic = intent.getBooleanExtra("classic", FALSE);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        gameView = new GameView(this, size.x, size.y, username, classic);

        setContentView(gameView);
    }
}
