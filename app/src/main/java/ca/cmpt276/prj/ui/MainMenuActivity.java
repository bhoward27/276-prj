package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import ca.cmpt276.prj.R;

/**
 * Activity for showing the Game's Main Menu, where players can click buttons to go to a variety of Activities
 */
public class MainMenuActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        getSupportActionBar().setTitle("FindDaMatch");

        setupButtons();

    }

    private void setupButtons() {

        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);//Change when Activity for the actual game is added
                startActivity(intent);
            }
        });

        Button btnOpenHelp = findViewById(R.id.btnOpenHelp);
        btnOpenHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        Button btnOpenOptions = findViewById(R.id.btnOpenOptions);
        btnOpenOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

        Button btnOpenHighScores = findViewById(R.id.btnOpenHighScores);
        btnOpenHighScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, HighScoresActivity.class);
                startActivity(intent);
            }
        });

        //btnOpenHighScores.setOnClickListener(view -> startActivity(HighScoresActivity.makeIntent(MainMenuActivity.this)));
    }
}
