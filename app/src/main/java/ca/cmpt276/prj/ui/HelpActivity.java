package ca.cmpt276.prj.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import ca.cmpt276.prj.R;


/**
 * This class shows the Help screen, where the player can learn the rules of the game and some
 * info about the options available on the Options screen.
 */

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        setupCreditsButton();
    }

    private void setupCreditsButton() {
        Button button = findViewById(R.id.btnCredits);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreditsActivity();
            }
        });
    }

    private void launchCreditsActivity() {
        Intent intent = CreditsActivity.makeIntent(HelpActivity.this);
        startActivity(intent);
    }
}