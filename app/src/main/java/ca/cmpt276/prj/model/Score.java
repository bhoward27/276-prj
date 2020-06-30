package ca.cmpt276.prj.model;

public class Score {
	public static final int NUM_HIGH_SCORES = 5;
	public static final int MAX_NAME_LENGTH = 12;

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
}
