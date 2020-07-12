package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ca.cmpt276.prj.R;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        //setupHyperlinks();
    }

    private void setupHyperlinks() {
        //  Create the textviews with findViewByID

        TextView links[] = {/*the textViews with links*/};
        //  CITATION: the below code in this method is based off of an answer on StackOverflow:
        //  https://stackoverflow.com/questions/2734270/how-to-make-links-in-a-textview-clickable
        for (TextView link : links) {
            //  This makes it so that when a hyperlink is tapped, it presents an option
            //  to the user to open the link in a web browser.
            link.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}