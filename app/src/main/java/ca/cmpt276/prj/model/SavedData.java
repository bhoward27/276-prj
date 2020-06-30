package ca.cmpt276.prj.model;

import android.content.SharedPreferences;

public class SavedData {
	public static final String PREFS = "ca.cmpt276.prj.Eleven";
	public static final int NUM_HIGH_SCORES = 5;

	private SharedPreferences prefs = null;

	private String[] highScores;
	private String[] defaultScores;

	// Singleton setup
	private static SavedData instance;

	private SavedData(SharedPreferences sharedPrefs) {
		this.prefs = sharedPrefs;
		setDefaultScores();
		loadScores();
	}

	public static SavedData getInstance(SharedPreferences prefs) {
		if (instance == null) {
			instance = new SavedData(prefs);
		}
		return instance;
	}

	private void loadScores() {

		for (int scoreKey = 1; scoreKey <= NUM_HIGH_SCORES; scoreKey++) {
			highScores[scoreKey] = prefs.getString("SCORE" + scoreKey, defaultScores[scoreKey]);
		}
		/*
		rows = prefs.getInt("SAVED_ROWS", DEFAULT_ROWS);
		cols = prefs.getInt("SAVED_COLUMNS", DEFAULT_COLS);
		numMines = prefs.getInt("SAVED_MINES", DEFAULT_MINES);
		numGames = prefs.getInt("SAVED_NUM_GAMES" + " " + rows + " " + cols + " " + numMines, DEFAULT_NUM_GAMES);
		bestScore = prefs.getInt("SAVED_BEST_SCORE" + " " + rows + " " + cols + " " + numMines, DEFAULT_BEST_SCORE);
		rowsColsListPos = prefs.getInt("SAVED_CL_POS", DEFAULT_ROWS_COLS_LIST_POS);
		minesListPos = prefs.getInt("SAVED_ML_POS", DEFAULT_MINES_LIST_POS);
		 */
	}

	private void saveScore(int scoreKey) {
		SharedPreferences.Editor editPrefs = prefs.edit();

		/*
		editPrefs.putInt("SAVED_ROWS", rows);
		editPrefs.putInt("SAVED_COLUMNS", cols);
		editPrefs.putInt("SAVED_MINES", numMines);
		editPrefs.putInt("SAVED_NUM_GAMES", numGames);
		editPrefs.putInt("SAVED_CL_POS", rowsColsListPos);
		editPrefs.putInt("SAVED_ML_POS", minesListPos);
		*/

		editPrefs.apply();
	}

	private void setDefaultScores() {
		String[] times = {"0:30",
				"0:45",
				"1:30",
				"2:30",
				"3:00"};

		String[] names = {"Joann",
				"Ethel",
				"Terrence",
				"Ian",
				"Glen"};

		String[] dates = {"June 28, 2020",
				"June 17, 2020",
				"June 10, 2020",
				"June 15, 2020",
				"May 30, 2020"};

		int key;
		for (key = 1; key <= 5; key++) {
			defaultScores[key] = times[key] + "by" + names[key] + "on" + dates[key];
		}

		if (NUM_HIGH_SCORES > 5) {
			for (; key <= NUM_HIGH_SCORES; key++) {
				defaultScores[key] = "AB:XY" + "by" + key + "on" + "DATE";
			}
		}
	}

	public String[] getHighScores() {
		return highScores;
	}

	public void setHighScore(String name, int score) {
		this.highScores = highScores;
	}

}
