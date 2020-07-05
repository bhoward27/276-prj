package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ca.cmpt276.prj.model.Constants.MAX_NAME_LENGTH;
import static ca.cmpt276.prj.model.Constants.NUM_HIGH_SCORES;

/**
 * This class provides an interface for the system's shared preferences and
 * allows us to interact with the high scores list
 */

public class ScoreManager {
	private List<Score> scores;
	private List<Score> defaultScores;

	private SharedPreferences prefs;

	// Singleton setup
	private static ScoreManager instance;

	private ScoreManager(SharedPreferences sharedPrefs) {
		this.prefs = sharedPrefs;
		this.scores = new ArrayList<>();
		this.defaultScores = new ArrayList<>();

		setDefaultScores();
		loadScores();
	}

	public static ScoreManager getInstance(SharedPreferences prefs) {
		if (instance == null) {
			instance = new ScoreManager(prefs);
		}
		return instance;
	}
	// End singleton setup

	public void addHighScore(String name, int inputScore) {
		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
		Date date = new Date();

		String formattedDate = formatter.format(date);
		String formattedName = name.substring(0, Math.min(name.length(), MAX_NAME_LENGTH));

		int position = NUM_HIGH_SCORES;
		for (Score score : scores){
			if (inputScore <= score.getTime()){
				position = scores.indexOf(score);
				break;
			}
		}

		if (position >= NUM_HIGH_SCORES){
			return;
		}

		scores.add(position, new Score(inputScore, formattedName, formattedDate));
		// purge overflowed score
		scores.remove(NUM_HIGH_SCORES);

		saveScores();
	}

	public List<Score> getScores() {
		return scores;
	}

	// may be useful for the GameActivity to display current high score
	public int getHighScoreTime(int key) {
		return scores.get(key).getTime();
	}

	public Score getScoreByIndex(int index) {
		return scores.get(index);
	}

	public void resetToDefaults() {
		scores.clear();

		scores = new ArrayList<>(defaultScores);

		saveScores();
	}

	private void loadScores() {
		for (Score score : defaultScores) {
			int key = defaultScores.indexOf(score);

			int currentTime = prefs.getInt("SCORE_TIME" + key, score.getTime());
			String currentName = prefs.getString("SCORE_NAME" + key, score.getName());
			String currentDate = prefs.getString("SCORE_DATE" + key, score.getDate());

			scores.add(new Score(currentTime, currentName, currentDate));
		}
	}

	private void saveScores() {
		SharedPreferences.Editor editPrefs = prefs.edit();

		for (Score score : scores) {
			int key = scores.indexOf(score);

			editPrefs.putInt("SCORE_TIME" + key, score.getTime());
			editPrefs.putString("SCORE_NAME" + key, score.getName());
			editPrefs.putString("SCORE_DATE" + key, score.getDate());
		}

		editPrefs.apply();
	}

	private void setDefaultScores() {
		int[] times = {30,
				45,
				90,
				150,
				180};

		String[] names = {"Joann",
				"Ethel",
				"Terrence",
				"Ian",
				"Glen"};

		String[] dates = {"June 28, 2020",
				"June 17, 2020",
				"June 9, 2020",
				"June 15, 2020",
				"May 30, 2020"};

		int key;
		for (key = 0; key < 5; key++) {
			defaultScores.add(new Score(times[key], names[key], dates[key]));
		}

		// placeholder default scores if more than 5 high scores are needed
		if (NUM_HIGH_SCORES > 5) {
			for (; key <= NUM_HIGH_SCORES; key++) {
				defaultScores.add(new Score(1800, String.valueOf(key), "DATE"));
			}
		}
	}
}
