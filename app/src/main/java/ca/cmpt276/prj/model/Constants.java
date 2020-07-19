package ca.cmpt276.prj.model;

import android.graphics.Color;

/**
 *  Code for storing constants for use by other functions.
 */
public final class Constants {
	private Constants() { }

	public static final int LANDSCAPE_SET = 1;
	public static final int PREDATOR_SET = 2;
	public static final boolean DISCARD_PILE = true;
	public static final boolean DRAW_PILE = false;
	public static final int NUM_HIGH_SCORES = 5;
	public static final int MAX_NAME_LENGTH = 12;
	public static final String PREFS = "ca.cmpt276.prj.Eleven";
	// TODO: add functionality for more than 3 images (possible future iterations)
	public static final int NUM_IMAGES = 3;
	public static final String PICTURE_TYPE_INT_PREF = "PICTURE_TYPES_INT";
	public static final String PICTURE_TYPE_STRING_PREF = "PICTURE_TYPES_STRING";
	public static final String NAME_PREF = "PLAYER_NAME";
	public static final double IMAGES_RATIOS = 2.01;
	public static final double BUTTON_SPACING_PADDING = 1.05;
}
