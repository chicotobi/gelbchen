package com.hofmannt.gelbchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sp;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = this.getSharedPreferences("DATA_GELBCHEN", Context.MODE_PRIVATE);
        username = sp.getString("username", "Myself");
        EditText editText = findViewById(R.id.name);
        editText.setText(username);
    }

    @Override
    public void onClick(View view) {
        EditText editText = findViewById(R.id.name);
        username = editText.getText().toString();

        SharedPreferences.Editor e = sp.edit();
        e.putString("username",username);
        e.apply();

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("name", username);
        startActivity(intent);
    }

    public void onClickHighscore(View view) {
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }
}
