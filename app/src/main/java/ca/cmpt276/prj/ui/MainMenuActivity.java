package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.OptionsManager;

import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET_PREFIX;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.IMAGE_FOLDER_NAME;
import static ca.cmpt276.prj.model.Constants.PREFS;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

/**
 * Activity for showing the Game's Main Menu, where players can click buttons to go to a variety of Activities
 */
public class MainMenuActivity extends AppCompatActivity {
	OptionsManager optionsManager;

	public static Intent makeIntent(Context context) {
		return new Intent(context, MainMenuActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		OptionsManager.instantiate(this);
		optionsManager = OptionsManager.getInstance();

		setUpImages();
		setUpButtons();
		//setUpStorageFolder();

	}

	private void setUpImages() {//Images change depending on the currently selected deck in the Options screen
		List<ImageView> views = new ArrayList<>();
		views.add(findViewById(R.id.img_1));
		views.add(findViewById(R.id.img_2));
		views.add(findViewById(R.id.img_3));
		views.add(findViewById(R.id.img_4));
		views.add(findViewById(R.id.img_5));
		views.add(findViewById(R.id.img_6));
		views.add(findViewById(R.id.img_7));
		// randomize images? we can remove this, just an idea
		Collections.shuffle(views);

		String imageSetPrefix = optionsManager.getImageSetPrefix();

		for (ImageView view : views) {
			if (optionsManager.getImageSet() != FLICKR_IMAGE_SET) {
				int resourceID = getResources().getIdentifier(imageSetPrefix + RESOURCE_DIVIDER
						+ views.indexOf(view), IMAGE_FOLDER_NAME, getPackageName());
				view.setImageResource(resourceID);
			} else {
				int resourceID = getResources().getIdentifier(DEFAULT_IMAGE_SET_PREFIX + RESOURCE_DIVIDER
						+ views.indexOf(view), IMAGE_FOLDER_NAME, getPackageName());
				view.setImageResource(resourceID);
			}

		}
	}

	private void setUpButtons() {

		Button btnPlay = findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);//Change when Activity for the actual game is added
			startActivity(intent);
		});

		Button btnOpenHelp = findViewById(R.id.btnOpenHelp);
		btnOpenHelp.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, HelpActivity.class);
			startActivity(intent);
		});

		Button btnOpenOptions = findViewById(R.id.btnOpenOptions);
		btnOpenOptions.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
			startActivity(intent);
		});

		Button btnOpenHighScores = findViewById(R.id.btnOpenHighScores);
		btnOpenHighScores.setOnClickListener(v -> {
			Intent intent = new Intent(MainMenuActivity.this, HighScoresActivity.class);
			startActivity(intent);
		});

	}

	@Override
	public void onResume() {
		setUpImages();
		super.onResume();

	}
}
