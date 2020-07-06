package ca.cmpt276.prj.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

		getSavedData();
		// DEBUG
		manager.addHighScore("William", 60);

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

	// helper function for clicking on the list to not crash
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

			TextView ScoreText = itemView.findViewById((R.id.txtPlaceholderScore));
			ScoreText.setText(manager.getScoreByIndex(position).getFormattedScore());
			return itemView;
		}
	}

}
