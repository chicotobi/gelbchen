package com.hofmannt.gelbchen;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HighscoreActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        sharedPreferences  = getSharedPreferences("DATA_GELBCHEN", Context.MODE_PRIVATE);
        updateTextFromSharedPreferences();
    }

    private void updateTextFromSharedPreferences() {
        TextView textView = findViewById(R.id.textHighscore);
        StringBuilder str = new StringBuilder();
        if(!sharedPreferences.contains("name0")) {
            resetHighscore();
        }
        for(int i=0;i<5;i++) {
            str.append(sharedPreferences.getString("name" + i, "default")).append(" - ").append(sharedPreferences.getInt("score" + i, 0)).append("\n");
        }
        textView.setText(str.toString());
    }

    public void resetHighscore(View view) {
        resetHighscore();
    }

    private void resetHighscore() {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString("name0", "Robb");
        e.putString("name1", "Sansa");
        e.putString("name2", "Bran");
        e.putString("name3", "Arya");
        e.putString("name4", "Rickon");
        for (int i = 0; i < 5; i++) {
            e.putInt("score"+i, 25-i*i);
        }
        e.apply();
        updateTextFromSharedPreferences();
    }
}
