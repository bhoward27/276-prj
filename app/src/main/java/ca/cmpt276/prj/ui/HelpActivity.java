package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import ca.cmpt276.prj.R;


/**
 * Help Screen for shoing instructions on how to play the game.
 */

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_help_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView helpDescriptionAndCitation = findViewById(R.id.helpDescription);
        String helpDescriptionText = getString(R.string.txt_help_description);
        helpDescriptionAndCitation.setText(helpDescriptionText);

        //Since a lot of text is used here, I wanted to make it scroll. I used code from this link, which allows such scrolling wihtout a Scrollview.
        //Code adapted from Amit Chintawar @ https://stackoverflow.com/a/3256305
        helpDescriptionAndCitation.setMovementMethod(new ScrollingMovementMethod());
        setupCreditsButton();
    }

    private void setupCreditsButton() {
        Button button = (Button) findViewById(R.id.btnCredits);
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