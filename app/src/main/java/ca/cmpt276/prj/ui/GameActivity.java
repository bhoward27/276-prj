package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.ScoreManager;


public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    private void getSavedData() {
        ScoreManager scoreManager = ScoreManager.getInstance();
    }
}
