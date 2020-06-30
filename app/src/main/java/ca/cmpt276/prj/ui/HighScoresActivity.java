package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.SavedData;

import static ca.cmpt276.prj.model.SavedData.PREFS;

public class HighScoresActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_scores);

		// get saved data
		SavedData.getInstance(getSharedPreferences(PREFS, Context.MODE_PRIVATE));


	}

}