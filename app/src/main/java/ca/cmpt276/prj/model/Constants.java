package ca.cmpt276.prj.model;

import android.graphics.Color;

/**
 *  Code for storing constants for use by other functions.
 */
public final class Constants {
	private Constants() { }

	public static final boolean DISCARD_PILE = true;
	public static final boolean DRAW_PILE = false;
	public static final int NONE_SELECTED = Integer.MAX_VALUE;
	public static final int NUM_HIGH_SCORES = 5;
	public static final int MAX_NAME_LENGTH = 12;
	public static final String PREFS = "ca.cmpt276.prj.Eleven";
	// TODO: add functionality for more than 3 images (possible future iterations)
	public static final int NUM_IMAGES = 3;
	public static final String IMAGE_SET_INT_PREF = "PICTURE_TYPES_INT";
	public static final String IMAGE_SET_PREFIX_PREF = "PICTURE_TYPES_PREFIX";
	public static final String IMAGE_SET_NAME_PREF = "PICTURE_TYPES_NAME";
	public static final String NAME_PREF = "PLAYER_NAME";
	public static final double IMAGES_RATIOS = 2.01;
	public static final double BUTTON_SPACING_PADDING = 1.05;
	public static final int DEFAULT_IMAGE_SET = 0;
	public static final String IMAGE_FOLDER_NAME = "drawable";
	public static final String RESOURCE_DIVIDER = "_";
	public static final String DEFAULT_IMAGE_SET_PREFIX = "a";
	public static final String SCORE_TIME_KEY_PREFIX = "SCORE_TIME";
	public static final String SCORE_NAME_KEY_PREFIX = "SCORE_NAME";
	public static final String SCORE_DATE_KEY_PREFIX = "SCORE_DATE";
}
