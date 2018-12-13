package com.hofmannt.gelbchen;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HighscoreActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        sharedPreferences  = getSharedPreferences("DATA_GELBCHEN", Context.MODE_PRIVATE);
        updateTextFromSharedPreferences();
    }

    public void updateTextFromSharedPreferences() {
        TextView textView = findViewById(R.id.textHighscore);
        String str = "";
        if(!sharedPreferences.contains("name0")) {
            resetHighscore();
        }
        for(int i=0;i<5;i++) {
            str = str + sharedPreferences.getString("name"+i,"default") + " - " + sharedPreferences.getInt("score"+i,0) + "\n";
        }
        textView.setText(str);
    }

    public void resetHighscore(View view) {
        resetHighscore();
    }

    public void resetHighscore() {
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
