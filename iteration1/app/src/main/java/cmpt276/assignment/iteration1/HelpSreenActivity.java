package cmpt276.assignment.iteration1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Help Screen for showcasing
 */
public class HelpSreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_sreen);

        TextView helpDescriptionAndCitation = findViewById(R.id.helpDescription);
        //helpDescriptionAndCitation.setText("Optional Features:\n1. Error Checking Input");
        String helpDescriptionText = getString(R.string.helpDescription);
        helpDescriptionAndCitation.setText(helpDescriptionText);
        /*
        Since a lot of text is used here, I wanted to make it scroll. I used code from this link, which allows such scrolling wihtout a Scrollview.
        From https://stackoverflow.com/questions/1748977/making-textview-scrollable-on-android
         */
        helpDescriptionAndCitation.setMovementMethod(new ScrollingMovementMethod());
    }
}
