package ca.cmpt276.prj.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.Score;
import ca.cmpt276.prj.model.ScoreManager;

public class HighScoresActivity extends AppCompatActivity {
	ScoreManager manager;

	public static Intent makeIntent(Context context) {
		return new Intent(context, HighScoresActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_scores);

		Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_high_scores_activity));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSavedData();
		// DEBUG
		// manager.addHighScore("William", 60);

		setupButtons();
		registerClickCallback();
		populateListView();

	}

	private void setupButtons() {
		Button btnClearHighScores = findViewById(R.id.btnClearHighScores);
		btnClearHighScores.setOnClickListener(view -> {
			manager.resetToDefaults();
			populateListView();
		});
	}

	private void getSavedData() {
		manager = ScoreManager.getInstance();
	}

	// Helper function for clicking on the list to not crash
	private void registerClickCallback() {
		ListView list = findViewById(R.id.lstvwScores);
		list.setOnItemClickListener((parent, viewClicked, position, id) -> { });
	}

	private void populateListView() {
		ArrayAdapter<Score> adapter = new MyListAdapter();
		ListView list = findViewById(R.id.lstvwScores);
		list.setAdapter(adapter);
	}

	private class MyListAdapter extends ArrayAdapter<Score> {

		public MyListAdapter() {
			super(HighScoresActivity.this, R.layout.item_view, manager.getScores());
		}

		@SuppressLint("SetTextI18n")
		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
			}

			TextView scoreText = itemView.findViewById((R.id.txtPlaceholderScore));
			int scoreRank = position + 1; // +1 accounts of 1st element in list having a position of 0
			String placementText;
			switch(scoreRank){// Assigns different colour to the current text according to its spot on the list
				case 1:
					// Code for setting the color adapted from yfsx and Vasily Kabunov @ /https://stackoverflow.com/a/34487328
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.gold));
					placementText = getString(R.string.txt_first_rank);
					break;
				case 2:
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.silver));
					placementText = getString(R.string.txt_second_rank);
					break;
				case 3:
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.bronze));
					placementText = getString(R.string.txt_third_rank);
					break;
				default:
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.black));
					placementText = getString(R.string.txt_below_top_three_rank);
					break;
			}

			// Code for setting a font already included in Android Studio adapted from Oded @ https://stackoverflow.com/a/31867144
			scoreText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
			scoreText.setTextSize(20);

			Score currScore = manager.getScoreByIndex(position);
			scoreText.setText(scoreRank + placementText + Score.getFormattedTime(currScore.getTime()) +
					getString(R.string.txt_score_by) + currScore.getName() +
					getString(R.string.txt_score_on) + currScore.getDate());

			return itemView;
		}
	}

}
