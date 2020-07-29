package ca.cmpt276.prj.model;

import android.graphics.Color;

/**
 * Code for storing constants for use by other functions.
 */
public final class Constants {
	private Constants() {
	}

	public static final boolean DISCARD_PILE = true;
	public static final boolean DRAW_PILE = false;
	public static final int NONE_SELECTED = Integer.MAX_VALUE;
	public static final int NUM_HIGH_SCORES = 5;
	public static final int MAX_NAME_LENGTH = 12;
	public static final String PREFS = "ca.cmpt276.prj.Eleven";
	public static final String IMAGE_SET_INT_PREF = "PICTURE_TYPES_INT";
	public static final String IMAGE_SET_PREFIX_PREF = "PICTURE_TYPES_PREFIX";
	public static final String IMAGE_SET_NAME_PREF = "PICTURE_TYPES_NAME";
	public static final String ORDER_PREF = "ORDER_PREF";
	public static final String DRAW_PILE_SIZE_PREF = "DRAW_PILE_SIZE_PREF";
	public static final String DIFFERENT_CARDS_AMOUNT_PREF = "DIFFERENT_CARDS_AMOUNT_PREF";
	public static final String NAME_PREF = "PLAYER_NAME";
	public static final String DEFAULT_PLAYER_NAME = "[Your name]";
	public static final double BUTTON_SPACING_PADDING = 10;
	public static final int LANDSCAPE_IMAGE_SET = 0;
	public static final int PREDATOR_IMAGE_SET = 1;
	public static final int FLICKR_IMAGE_SET = 2;
	public static final int DEFAULT_IMAGE_SET = LANDSCAPE_IMAGE_SET;
	public static final String DEFAULT_IMAGE_SET_PREFIX = "a_";
	//public static final int DEFAULT_DIFFERENT_CARDS_AMOUNT = 7;
	public static final String DEFAULT_ORDER = "Order 2";
	public static final String DEFAULT_DRAW_PILE_SIZE = "All";
	public static final String IMAGE_FOLDER_NAME = "drawable";
	public static final String RESOURCE_DIVIDER = "_";
	public static final String SCORE_TIME_KEY_PREFIX = "SCORE_TIME";
	public static final String SCORE_NAME_KEY_PREFIX = "SCORE_NAME";
	public static final String SCORE_DATE_KEY_PREFIX = "SCORE_DATE";
	public static final String WORD_MODE_PREF_KEY = "WORD_MODE_PREF";
	public static final String FLICKR_IMAGE_SET_SIZE_PREF_KEY = "NUM_IMAGES_IN_IMAGE_SET_PREF";
	public static final int DEFAULT_FLICKR_IMAGE_SET_SIZE = 0;
	public static final boolean DEFAULT_WORD_MODE = false;
	public static final String ORDER_PREF_KEY = "ORDER_PREF";
	public static final int DEFAULT_ORDER_PREF = 2;
	public static final String DECK_SIZE_PREF_KEY = "DECK_SIZE_PREF";
	public static final int DEFAULT_DECK_SIZE = 7;
	public static final String SETTING_NUM_IMAGES_IN_IMAGE_SET_ERROR_MSG = "Error: Can only manually set numImagesInImageSet" +
			" when the Flickr image set is the current image set.";
	//	Orders must appear in ascending order in the below array for binary search to work on
	//	it.
	public static final int[] SUPPORTED_ORDERS = {2, 3, 5};
	public static final int NUM_IMAGES_IN_DEFAULT_SETS = 31;
	public static final int MINIMUM_DECK_SIZE = 5;
	public static final String FLICKR_SAVED_DIR = "flickr_user_images";
	public static final String FLICKR_PENDING_DIR = "flickr_pending_images";
	public static final String JPG_EXTENSION = ".jpg";
	public static final String FLICKR_PREFIX = "c";
	public static final String FLICKR_IMAGE_NAME_PREFIX = FLICKR_PREFIX + RESOURCE_DIVIDER;
}
