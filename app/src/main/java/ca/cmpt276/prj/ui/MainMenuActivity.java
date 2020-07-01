package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import ca.cmpt276.prj.R;

public class MainMenuActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context){
        return new Intent(context, MainMenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setupButtons();

    }

    private void setupButtons() {
        Button btnOpenHighScores = findViewById(R.id.btnOpenHighScores);
        btnOpenHighScores.setOnClickListener(view -> startActivity(HighScoresActivity.makeIntent(MainMenuActivity.this)));
    }
}
