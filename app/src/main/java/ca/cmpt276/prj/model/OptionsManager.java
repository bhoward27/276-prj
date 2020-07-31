package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;

import static ca.cmpt276.prj.model.Constants.ASCII_OFFSET;
import static ca.cmpt276.prj.model.Constants.DECK_SIZE_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.DEFAULT_DECK_SIZE;
import static ca.cmpt276.prj.model.Constants.DEFAULT_FLICKR_IMAGE_SET_SIZE;
import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.DEFAULT_ORDER_PREF;
import static ca.cmpt276.prj.model.Constants.DEFAULT_PLAYER_NAME;
import static ca.cmpt276.prj.model.Constants.DEFAULT_WORD_MODE;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_INT_PREF;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.MINIMUM_DECK_SIZE;
import static ca.cmpt276.prj.model.Constants.NAME_PREF;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES_IN_DEFAULT_SETS;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET_SIZE_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.ORDER_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.PREDATOR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.PREFS;
import static ca.cmpt276.prj.model.Constants.SUPPORTED_ORDERS;
import static ca.cmpt276.prj.model.Constants.WORD_MODE_PREF_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class is a singleton that interacts with SharedPreferences to retrieve and save options.
 * Whenever a setter is called, the preference is automatically saved.
 */
public class OptionsManager {
	private static final String TAG = "OptionsManager";

	/**
	 * This class contains everything that is an "option", and loads them on construction.
	 */
	private class Option {
		int imageSet;
		int numFlickrImages;
		int order;
		int deckSize;
		boolean wordMode; // when true, some cards will have words appear instead of images.
		String playerName;

		Option(){
			this.imageSet = prefsManager.loadInt(IMAGE_SET_INT_PREF, // key
					DEFAULT_IMAGE_SET); // default key
			this.numFlickrImages = prefsManager.loadInt(FLICKR_IMAGE_SET_SIZE_PREF_KEY,
					DEFAULT_FLICKR_IMAGE_SET_SIZE);
			this.order = prefsManager.loadInt(ORDER_PREF_KEY,
					DEFAULT_ORDER_PREF);
			this.deckSize = prefsManager.loadInt(DECK_SIZE_PREF_KEY,
					DEFAULT_DECK_SIZE);
			this.wordMode = prefsManager.loadBool(WORD_MODE_PREF_KEY,
					DEFAULT_WORD_MODE);
			this.playerName = prefsManager.loadString(NAME_PREF,
					DEFAULT_PLAYER_NAME);
		}
	}

	private static OptionsManager instance;
	private PrefsManager prefsManager;
	private Option options;

	//  Call instantiate on splash screen. Can instantiate multiple times.
	public static void instantiate(Context ctx) {
		if (instance == null) {
			instance = new OptionsManager(ctx);
		}
	}

	private OptionsManager(Context ctx) {
		this.prefsManager = new PrefsManager(ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE));
		this.options = new Option();

	}

	public static OptionsManager getInstance() {
		assertNotNull(instance);
		return instance;
	}

	public void setImageSet(int imageSet) {
		options.imageSet = imageSet;
		prefsManager.saveInt(IMAGE_SET_INT_PREF, imageSet);
	}

	public int getImageSet() {
		return options.imageSet;
	}

	/**
	 * This method maps the image set number to a lowercase letter and returns it as a string.
	 * For example, 0 gets mapped to "a", 1 to "b", and so on.
	 */
	public String getImageSetPrefix() {
		return String.valueOf((char) (options.imageSet + ASCII_OFFSET));
	}

	public void setPlayerName(String name) {
		if (name.matches("")) {
			name = DEFAULT_PLAYER_NAME;
		}

		options.playerName = name;
		prefsManager.saveString(NAME_PREF, name);
	}

	public String getPlayerName() {
		return options.playerName;
	}

	public void setOrder(int order) {
		if (IntStream.of(SUPPORTED_ORDERS).noneMatch(x -> x == order)) {
			Log.d(TAG, String.format("setOrder: order %d not supported", order));
			return;
		}

		options.order = order;
		prefsManager.saveInt(ORDER_PREF_KEY, order);
	}

	public int getOrder() {
		return options.order;
	}

	public void setDeckSize(int deckSize) {
		if (deckSize < MINIMUM_DECK_SIZE) {
			Log.d(TAG, String.format("setDeckSize: deck size %d not supported", deckSize));
			return;
		}

		options.deckSize = deckSize;
		prefsManager.saveInt(DECK_SIZE_PREF_KEY, deckSize);
	}

	public int getDeckSize() {
		return options.deckSize;
	}

	/**
	 * @return the maximum possible deckSize for the current order.
	 */
	public int getMaxDeckSize() {
		int numImagesPerCard = options.order + 1;
		return numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;
	}

	public void setWordMode(boolean wordMode) {
		options.wordMode = wordMode;
		prefsManager.saveBool(WORD_MODE_PREF_KEY, wordMode);
	}

	public boolean isWordMode() {
		return options.wordMode;
	}

	//Return list for the ui to print
	@SuppressLint("DefaultLocale")
	public ArrayList<String> getValidDrawPileSizes() {
		// adds 5, 10, 15, 20 and maxDeckSize
		ArrayList<String> validSizes = new ArrayList<>();
		for (int i = 5; i < getMaxDeckSize() && i <= 20; i += 5) {
			validSizes.add(Integer.toString(i));
		}
		validSizes.add(String.format("%d (All)", getMaxDeckSize()));
		return validSizes;
	}

	public int getNumImagesInImageSet() {
		switch (options.imageSet) {
			case LANDSCAPE_IMAGE_SET:
			case PREDATOR_IMAGE_SET:
			default:
				return NUM_IMAGES_IN_DEFAULT_SETS;
		}
	}

	public void setFlickrImageSetSize(int size) {
		options.numFlickrImages = size;
		prefsManager.saveInt(FLICKR_IMAGE_SET_SIZE_PREF_KEY, size);
	}

	public int getFlickrImageSetSize() {
		return options.numFlickrImages;
	}

}