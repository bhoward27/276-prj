package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;

/**
 * This class contains all the functionality of a Score object
 * and a couple helper functions for beautiful formatted output
 * of the score
 */

public class Score {
	private int scoreTime;
	private String scoreName;
	private String scoreDate;

	public Score(int scoreTime, String scoreName, String scoreDate) {
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

	public String getFormattedScore() {
		String formattedTime = getFormattedTime();

		return formattedTime + " by " + scoreName + " on " + scoreDate;
	}

	@SuppressLint("DefaultLocale")
	private String getFormattedTime() {
		int time = getTime();

		int minutes = time / 60;
		int seconds = time % 60;
		return String.format("%d:%02d", minutes, seconds);
	}


}
