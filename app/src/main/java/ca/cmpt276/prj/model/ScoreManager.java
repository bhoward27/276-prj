package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ca.cmpt276.prj.model.Constants.NUM_HIGH_SCORES;
import static ca.cmpt276.prj.model.Constants.SCORE_DATE_KEY_PREFIX;
import static ca.cmpt276.prj.model.Constants.SCORE_NAME_KEY_PREFIX;
import static ca.cmpt276.prj.model.Constants.SCORE_TIME_KEY_PREFIX;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class provides an interface for the system's shared preferences and allows us to interact
 * with different high scores lists depending on the chosen options in OptionsActivity.
 */

//List of scores

	//Add parameters involving the user's options in the Score
	//Now, make a new arrayList of Lists
	//For every list:
	//Check first element in list
	//If that element has the same order and draw pile size,
	//Add the new score to the list
	//If not, go to the next list and check its first element.
	//If the process repeats such that not list of high scores with that option configuation exists
	//Make a new list and store the score in that list.
	/*
	Make a new ArrayList of Lists. These Lists hold scores
		When the game is won:

//Add parameters involving the user's options in the Score
	//Now, make a new arrayList of Lists upon instantiating ScoreManager
    //When saving a high score...
	//For every list:
	//Check first element in list
	//If that element has the same order and draw pile size,
	//Add the new score to the list
	//If not, go to the next list and check its first element.
	//If the process repeats such that no list of high scores with that option configuration exists
	//Add a new list to the ArrayList and store the score in that list.
	/*
	 */
public class ScoreManager {
	private List<Score> scores;
	private List<Score> defaultScores;
	private SharedPreferences prefs;
	//private ArrayList<List> optionBasedScores;
	private String scorePrefix;

	// Singleton setup
	private static ScoreManager instance;

	//  Call instantiate on splash screen.
	public static void instantiate(SharedPreferences prefs) {
		if (instance == null) {
		instance = new ScoreManager(prefs);
	}
}

	//Assemble a string to identify scores by prepending it to their key
	//This will change everytime the oeder and deck size from OptionsActivity changes.
	public void setScorePrefix(int order, int deckSize){
		scorePrefix = order + "_"+ deckSize + "_";
	}

	//Was meant for comparing two scores together to see if their options matched
//	private boolean areScoreOptionsSame(Score newHighScore, Score oldHighScore){
//		if((newHighScore.getOrder() == oldHighScore.getOrder()) &&
//		newHighScore.getDrawPileSize() == oldHighScore.getDrawPileSize()){
//			return true;
//		}
//		return false;
//	}

	private ScoreManager(SharedPreferences prefs) {
		this.prefs = prefs;
		this.scores = new ArrayList<>();
		this.defaultScores = new ArrayList<>();
		//optionBasedScores = new ArrayList<>(0);
		setDefaultScores();
		loadScores();
	}


	public static ScoreManager getInstance() {
		assertNotNull(instance);
		return instance;
	}
	// End singleton setup

	public int addHighScore(String name, int inputScore) {
		@SuppressLint("SimpleDateFormat")
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
		Date date = new Date();

		String formattedDate = formatter.format(date);

		int position = NUM_HIGH_SCORES;
		for (Score score : scores){
			if (inputScore <= score.getTime()){
				position = scores.indexOf(score);
				break;
			}
		}

		if (position >= NUM_HIGH_SCORES){
			return 0;
		}

		scores.add(position, new Score(inputScore, name, formattedDate));
		// Purge overflowed score
		scores.remove(NUM_HIGH_SCORES);

		saveScores();

		return position+1;
	}

	//Parameters, chosen order and deckPileSize. Will then be compared to every existing list

	public List<Score> getScores() {
		return scores;
	}

	public Score getScoreByIndex(int index) {
		return scores.get(index);
	}

	public void resetToDefaults() {
		scores.clear();

		scores = new ArrayList<>(defaultScores);

		saveScores();
	}

	public void loadScores() {
		scores.clear();
		for (Score score : defaultScores) {
			int key = defaultScores.indexOf(score);
			int currentTime = prefs.getInt(scorePrefix + SCORE_TIME_KEY_PREFIX + key, score.getTime());
			String currentName = prefs.getString(scorePrefix + SCORE_NAME_KEY_PREFIX + key, score.getName());
			String currentDate = prefs.getString(scorePrefix + SCORE_DATE_KEY_PREFIX + key, score.getDate());

			scores.add(new Score(currentTime, currentName, currentDate));
		}
	}

	public void saveScores() {
		SharedPreferences.Editor editPrefs = prefs.edit();

		for (Score score : scores) {
			int key = scores.indexOf(score);

			editPrefs.putInt(scorePrefix + SCORE_TIME_KEY_PREFIX + key, score.getTime());
			editPrefs.putString(scorePrefix + SCORE_NAME_KEY_PREFIX + key, score.getName());
			editPrefs.putString(scorePrefix + SCORE_DATE_KEY_PREFIX + key, score.getDate());
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
			for (; key < NUM_HIGH_SCORES; key++) {
				defaultScores.add(new Score(1800, String.valueOf(key), "DATE"));
			}
		}
	}
}
