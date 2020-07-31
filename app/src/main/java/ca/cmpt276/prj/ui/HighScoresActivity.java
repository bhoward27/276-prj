package ca.cmpt276.prj.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.OptionsManager;
import ca.cmpt276.prj.model.Score;
import ca.cmpt276.prj.model.ScoreManager;

/**
 * Activity for showing the High Scores for a specific set of options in the game.
 */

public class HighScoresActivity extends AppCompatActivity {
	ScoreManager scoreManager;
	OptionsManager optionsManager;
	int currentOrder;
	int currentDrawPileSize;

	public static Intent makeIntent(Context context) {
		return new Intent(context, HighScoresActivity.class);
	}

	//Make a score list of stuff
	//Make an ArrayList of an ArrayList of scores 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_scores);

		Objects.requireNonNull(getSupportActionBar())
				.setTitle(getString(R.string.title_high_scores_activity));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		scoreManager = ScoreManager.getInstance();

		//Updates loaded scores with the current options
		scoreManager.loadScores();

		initOptionSet();
		setupButtons();
		registerClickCallback();
		populateListView();
		printChosenOptions();
	}

	private void initOptionSet() {
		optionsManager = OptionsManager.getInstance();
		currentOrder = optionsManager.getOrder();
		currentDrawPileSize = optionsManager.getDeckSize();
	}

	private void printChosenOptions() {
		TextView textCurrentOrder = findViewById(R.id.txt_order);
		TextView textCurrentDrawPileSize = findViewById(R.id.txt_draw_pile_size);
		String chosenOrder = getString(R.string.txt_selected_order, currentOrder);
		String chosenDrawPileSize = getString(R.string.txt_selected_pile_size, currentDrawPileSize);
		//If deck = max
		textCurrentOrder.setText(chosenOrder);
		textCurrentDrawPileSize.setText(chosenDrawPileSize);
	}

	//String.format("%s%d",R.string.txt_selected_order, currentOrder))
	private void setupButtons() {
		Button btnClearHighScores = findViewById(R.id.btnClearHighScores);
		btnClearHighScores.setOnClickListener(view -> {
			scoreManager.resetToDefaults();
			populateListView();
		});
	}

	// Helper function for clicking on the list to not crash
	private void registerClickCallback() {
		ListView list = findViewById(R.id.lstvwScores);
		list.setOnItemClickListener((parent, viewClicked, position, id) -> {
		});
	}

	private void populateListView() {
		ArrayAdapter<Score> adapter = new MyListAdapter();
		ListView list = findViewById(R.id.lstvwScores);
		list.setAdapter(adapter);
	}

	private class MyListAdapter extends ArrayAdapter<Score> {

		public MyListAdapter() {
			super(HighScoresActivity.this, R.layout.item_view, scoreManager.getScores());
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

			// +1 accounts for first element in list having a position of 0.
			int scoreRank = position + 1;
			String placementText;

			// Assigns different colour to the current text according to its spot on the list.
			switch (scoreRank) {
				case 1:
					// Code for setting the color adapted from yfsx and Vasily Kabunov
					// @ https://stackoverflow.com/a/34487328
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this,
							R.color.gold));
					placementText = getString(R.string.txt_first_rank);
					break;
				case 2:
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this,
							R.color.silver));
					placementText = getString(R.string.txt_second_rank);
					break;
				case 3:
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this,
							R.color.bronze));
					placementText = getString(R.string.txt_third_rank);
					break;
				default:
					scoreText.setTextColor(ContextCompat.getColor(HighScoresActivity.this,
							R.color.black));
					placementText = getString(R.string.txt_below_top_three_rank);
			}

			// Code for setting a font already included in Android Studio adapted from Oded
			// @ https://stackoverflow.com/a/31867144
			scoreText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
			scoreText.setTextSize(20);

			Score currScore = scoreManager.getScoreByIndex(position);
			scoreText.setText(scoreRank + placementText + Score.getFormattedTime(currScore.getTime()) +
					getString(R.string.txt_score_by) + currScore.getName() +
					getString(R.string.txt_score_on) + currScore.getDate());

			return itemView;
		}
	}
}
