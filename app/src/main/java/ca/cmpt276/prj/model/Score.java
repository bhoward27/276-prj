package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;

/**
 * This class contains all the functionality of a Score object and a couple helper functions for
 * beautiful formatted output of the score.
 */
public class Score {
	int scoreTime;
	String scoreName;
	String scoreDate;

	Score(int scoreTime, String scoreName, String scoreDate) {
		this.scoreTime = scoreTime;
		this.scoreName = scoreName;
		this.scoreDate = scoreDate;
	}

	public int getTime() {
		return scoreTime;
	}

	public String getName() {
		return scoreName;
	}

	public String getDate() {
		return scoreDate;
	}


	@SuppressLint("DefaultLocale")
	public static String getFormattedTime(int time) {

		int minutes = time / 60;
		int seconds = time % 60;
		return String.format("%d:%02d", minutes, seconds);
	}
}

