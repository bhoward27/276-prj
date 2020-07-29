package ca.cmpt276.prj.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import ca.cmpt276.prj.R;

/**
 * This class shows the Credits screen, which clickable hyperlinks to all images not created by the
 * dev team that did not come prepacked in Android Studio. It also shows some info on the dev team.
 */
public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        setupHyperlinks();
    }

    private void setupHyperlinks() {
        //Links for private
        TextView caveLink = (TextView) findViewById(R.id.textViewCaveSource);
        TextView desertLink = (TextView) findViewById(R.id.textViewDesertSource);
        TextView forestLink = (TextView) findViewById(R.id.textViewForestSource);
        TextView oceanLink = (TextView) findViewById(R.id.textViewOceanSource);
        TextView stormLink = (TextView) findViewById(R.id.textViewStormSource);
        TextView sunriseLink = (TextView) findViewById(R.id.textViewSunriseSource);
        TextView volcanoLink = (TextView) findViewById(R.id.textViewVolcanoSource);
        TextView rainbowLink = (TextView) findViewById(R.id.textViewRainbowSource);

        TextView bearLink = (TextView) findViewById(R.id.textViewBearSource);
        TextView eagleLink = (TextView) findViewById(R.id.textViewEagleSource);
        TextView leopardLink = (TextView) findViewById(R.id.textViewLeopardSource);
        TextView lionLink = (TextView) findViewById(R.id.textViewLionSource);
        TextView sharkLink = (TextView) findViewById(R.id.textViewSharkSource);
        TextView snakeLink = (TextView) findViewById(R.id.textViewSnakeSource);
        TextView spiderLink = (TextView) findViewById(R.id.textViewSpiderSource);
        TextView orcaLink = (TextView) findViewById(R.id.textViewOrcaSource);

        TextView appIconLink = (TextView) findViewById(R.id.textViewAppIconSource);

        TextView[] links = { caveLink, desertLink, forestLink, oceanLink,
                stormLink, sunriseLink, volcanoLink, rainbowLink,
                bearLink, eagleLink, leopardLink, lionLink,
                sharkLink, snakeLink, spiderLink, orcaLink, appIconLink };

        //  The code below is adapted from:
        //  https://stackoverflow.com/questions/2734270/how-to-make-links-in-a-textview-clickable
        for (TextView link : links) {
            //  This makes it so that when a hyperlink is tapped, it opens the link in a
            //  web browser.
            link.setMovementMethod(LinkMovementMethod.getInstance());
            Log.d("test", "setupHyperlinks: " + link);
        }
    }

    public static Intent makeIntent(Context c) {
         return new Intent(c, CreditsActivity.class);
    }
}