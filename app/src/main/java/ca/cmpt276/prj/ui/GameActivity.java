package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.ScoreManager.PREFS;

public class GameActivity extends AppCompatActivity {
    SharedPreferences sharedPrefs = null;
    ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getSavedData();
    }

    private void getSavedData() {
        sharedPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        scoreManager = ScoreManager.getInstance(sharedPrefs);
    }
}