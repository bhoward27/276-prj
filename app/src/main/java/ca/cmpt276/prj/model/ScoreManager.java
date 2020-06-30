package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ca.cmpt276.prj.model.Score.MAX_NAME_LENGTH;
import static ca.cmpt276.prj.model.Score.NUM_HIGH_SCORES;

public class ScoreManager {
	public static final String PREFS = "ca.cmpt276.prj.Eleven";

	private List<Score> scores = new ArrayList<>();

	private int[] defaultScoreTimes;
	private String[] defaultScoreNames;
	private String[] defaultScoreDates;

	private SharedPreferences prefs;

	// Singleton setup
	private static ScoreManager instance;

	private ScoreManager(SharedPreferences sharedPrefs) {
		this.prefs = sharedPrefs;

		this.defaultScoreTimes = new int[NUM_HIGH_SCORES];
		this.defaultScoreNames = new String[NUM_HIGH_SCORES];
		this.defaultScoreDates = new String[NUM_HIGH_SCORES];

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

	private void loadScores() {
		for (int scoreKey = 0; scoreKey < NUM_HIGH_SCORES; scoreKey++) {
			int currentTime = prefs.getInt("SCORE_TIME" + scoreKey, defaultScoreTimes[scoreKey]);
			String currentName = prefs.getString("SCORE_NAME" + scoreKey, defaultScoreNames[scoreKey]);
			String currentDate = prefs.getString("SCORE_DATE" + scoreKey, defaultScoreDates[scoreKey]);

			scores.add(new Score(currentTime, currentName, currentDate));
		}
	}

	private void saveScores() {
		SharedPreferences.Editor editPrefs = prefs.edit();

		for (Score score : scores) {
			editPrefs.putInt("SCORE_TIME" + scores.indexOf(score), score.getTime());
			editPrefs.putString("SCORE_NAME" + scores.indexOf(score), score.getName());
			editPrefs.putString("SCORE_DATE" + scores.indexOf(score), score.getDate());
		}

		editPrefs.apply();
	}

	public void resetToDefaults() {
		scores.clear();

		for (int scoreKey = 0; scoreKey < NUM_HIGH_SCORES; scoreKey++) {
			scores.add(new Score(defaultScoreTimes[scoreKey], defaultScoreNames[scoreKey], defaultScoreDates[scoreKey]));
		}

		saveScores();
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
			defaultScoreTimes[key] = times[key];
			defaultScoreNames[key] = names[key];
			defaultScoreDates[key] = dates[key];
		}

		if (NUM_HIGH_SCORES > 5) {
			for (; key <= NUM_HIGH_SCORES; key++) {
				defaultScoreTimes[key] = 1800; // 30 minutes if overflow
				defaultScoreNames[key] = String.valueOf(key);
				defaultScoreDates[key] = "DATE";
			}
		}
	}

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
		// Log.d("%%%%: ", "position: " + position);
		// do not set a high score if the score didn't beat anything in the current high scores
		if (position >= NUM_HIGH_SCORES){
			return;
		}

		scores.add(position, new Score(inputScore, formattedName, formattedDate));

		saveScores();
	}


	public int getHighScoreTime(int key) {
		return scores.get(key).getTime();
	}

	@SuppressLint("DefaultLocale")
	public String getFormattedHighScoreTime(int key) {
		int time = scores.get(key).getTime();

		int minutes = time / 60;
		int seconds = time % 60;
		return String.format("%d:%02d", minutes, seconds);
	}

	public String getHighScoreName(int key) {
		return scores.get(key).getName();
	}

	public String getHighScoreDate(int key) {
		return scores.get(key).getDate();
	}

	public List<Score> getScores() {
		return scores;
	}

	public Score getScoreByIndex(int index) {
		return scores.get(index);
	}

}
