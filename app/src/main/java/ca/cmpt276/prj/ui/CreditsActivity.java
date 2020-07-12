package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ca.cmpt276.prj.R;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        setupHyperlinks();
    }

    private void setupHyperlinks() {
        //  Create the textviews with findViewByID
        TextView caveLink = (TextView) findViewById(R.id.textViewCaveSource);
        TextView desertLink = (TextView) findViewById(R.id.textViewDesertSource);
        TextView forestLink = (TextView) findViewById(R.id.textViewForestSource);
        TextView oceanLink = (TextView) findViewById(R.id.textViewOceanSource);
        TextView stormLink = (TextView) findViewById(R.id.textViewStormSource);
        TextView sunriseLink = (TextView) findViewById(R.id.textViewSunriseSource);
        TextView volcanoLink = (TextView) findViewById(R.id.textViewVolcanoSource);



        TextView links[] = { caveLink, desertLink, forestLink, oceanLink, stormLink, sunriseLink,
                                volcanoLink };
        //  CITATION: the below code in this method is based off of an answer on StackOverflow:
        //  https://stackoverflow.com/questions/2734270/how-to-make-links-in-a-textview-clickable
        for (TextView link : links) {
            //  This makes it so that when a hyperlink is tapped, it presents an option
            //  to the user to open the link in a web browser.
            link.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static Intent makeIntent(Context c) {
         return new Intent(c, CreditsActivity.class);
    }
}