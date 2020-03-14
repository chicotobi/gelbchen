package com.hofmannt.gelbchen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private String username;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = this.getSharedPreferences("DATA_GELBCHEN", Context.MODE_PRIVATE);
        username = sp.getString("username", "Myself");
        EditText editText = findViewById(R.id.name);
        editText.setText(username);
        level = sp.getInt("level",1);
    }

    public void onClickStart(View view) {
        EditText editText = findViewById(R.id.name);
        username = editText.getText().toString();

        RadioGroup radioGroup = findViewById(R.id.radioGroup2);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String text = (String) radioButton.getText();
        switch (text) {
            case "Easy":
                level = 1;
                break;
            case "Moderate":
                level = 2;
                break;
            case "Hard":
                level = 3;
                break;
        }

        SharedPreferences.Editor e = sp.edit();
        e.putString("username",username);
        e.putInt("level",level);
        e.apply();

        Switch myswitch;
        myswitch = findViewById(R.id.classic_switch);
        Boolean classic = myswitch.isChecked();

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("name", username);
        intent.putExtra("classic", classic);
        startActivity(intent);
    }

    public void onClickHighscore(View view) {
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }
}
