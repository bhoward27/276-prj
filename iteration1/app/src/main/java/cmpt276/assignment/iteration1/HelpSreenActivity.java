package cmpt276.assignment.iteration1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Help Screen for showcasing instructions on how to play the game.
 * @Author Michael Mora
 */
public class HelpSreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sreen);

        TextView helpDescriptionAndCitation = findViewById(R.id.helpDescription);
        String helpDescriptionText = getString(R.string.helpDescription);
        helpDescriptionAndCitation.setText(helpDescriptionText);
        /*
        ScrollingMovementMethod used here due to lots of scrolling text.
        Reference: https://stackoverflow.com/questions/1748977/making-textview-scrollable-on-android
         */
        helpDescriptionAndCitation.setMovementMethod(new ScrollingMovementMethod());
    }
}
