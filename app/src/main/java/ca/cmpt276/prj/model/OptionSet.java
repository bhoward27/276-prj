package ca.cmpt276.prj.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;

import static ca.cmpt276.prj.model.Constants.DECK_SIZE_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.DEFAULT_DECK_SIZE;
import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.DEFAULT_ORDER_PREF;
import static ca.cmpt276.prj.model.Constants.DEFAULT_PLAYER_NAME;
import static ca.cmpt276.prj.model.Constants.DEFAULT_WORD_MODE;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_INT_PREF;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.MINIMUM_DECK_SIZE;
import static ca.cmpt276.prj.model.Constants.NAME_PREF;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES_IN_DEFAULT_SETS;
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
public class OptionSet {
    private static OptionSet instance;
    private SharedPreferences prefs;

    private int imageSet;
    private int numImagesInImageSet;
    private String playerName;
    private int order;
    private int deckSize;
    private boolean wordMode; // when true, some cards will have words appear instead of images.

    private static final int ASCII_OFFSET = 97;

    //  Call instantiate on splash screen.
    public static void instantiate(SharedPreferences prefs) {
        if (instance == null) {
            instance = new OptionSet(prefs);
        }
    }

    private OptionSet(SharedPreferences prefs) {
        this.prefs = prefs;
        loadPrefs();
    }

    public static OptionSet getInstance() {
        assertNotNull(instance);
        return instance;
    }

    private void loadPrefs() {
        loadImageSetPref();
        loadPlayerNamePref();
        loadOrderPref();
        loadDeckSizePref();
        loadWordModePref();
        loadNumImages();
    }

    private void loadImageSetPref() {
        imageSet = prefs.getInt(IMAGE_SET_INT_PREF, DEFAULT_IMAGE_SET);
    }

    private void loadPlayerNamePref() {
        //  DEFAULT_PLAYER_NAME is a string in Constants.java.
        //  This is not ideal because it really should come from the txt_placeholder_player_name
        //  from strings.xml; however, getString() cannot be called since OptionSet is not an
        //  activity. If anyone has a better way to do this, please change it to that.
        playerName = prefs.getString(NAME_PREF, DEFAULT_PLAYER_NAME);
    }

    private void loadOrderPref() {
        order = prefs.getInt(ORDER_PREF_KEY, DEFAULT_ORDER_PREF);
    }

    private void loadDeckSizePref() {
        deckSize = prefs.getInt(DECK_SIZE_PREF_KEY, DEFAULT_DECK_SIZE);
    }

    private void loadWordModePref() {
        wordMode = prefs.getBoolean(WORD_MODE_PREF_KEY, DEFAULT_WORD_MODE);
    }

    private void loadNumImages() {
        if (imageSet != FLICKR_IMAGE_SET) {
            numImagesInImageSet = NUM_IMAGES_IN_DEFAULT_SETS;
        } else {
            // TODO: get number of images from downloads folder
            numImagesInImageSet = 31;
        }
    }

    public void setImageSet(int imageSet) {
        if (imageSet < LANDSCAPE_IMAGE_SET || imageSet > FLICKR_IMAGE_SET) {
            throw new IllegalArgumentException("Error: Invalid imageSet argument. ImageSet " +
                    "must be in range [" + LANDSCAPE_IMAGE_SET + ", " + FLICKR_IMAGE_SET + "].");
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(IMAGE_SET_INT_PREF, imageSet);
        editor.apply();

        this.imageSet = imageSet;
    }

    public int getImageSet() {
        return imageSet;
    }

    /**
     * This method maps the image set number to a lowercase letter and returns it as a string.
     * For example, 0 gets mapped to "a", 1 to "b", and so on.
     */
    public String getImageSetPrefix() {
        return String.valueOf((char) (imageSet + ASCII_OFFSET));
    }

    public void setPlayerName(String name) {
        if (name.matches("")) {
            throw new IllegalArgumentException("Error: Invalid argument. playerName must not be " +
                    "a null string.");
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME_PREF, name);
        editor.apply();

        playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setOrder(int order) {
        if (Arrays.binarySearch(SUPPORTED_ORDERS, order) < 0) {
            throw new IllegalArgumentException("Error: Invalid order argument. Only the following"
                    + " order values are currently supported: " + Arrays.toString(SUPPORTED_ORDERS));
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ORDER_PREF_KEY, order);
        editor.apply();

        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setDeckSize(int deckSize) {
        if (deckSize < MINIMUM_DECK_SIZE) {
            throw new IllegalArgumentException("Error: Invalid deckSize argument. deckSize must" +
                    " be at least " + MINIMUM_DECK_SIZE + ".");
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(DECK_SIZE_PREF_KEY, deckSize);
        editor.apply();

        this.deckSize = deckSize;
    }

    public int getDeckSize() {
        return deckSize;
    }

    /**
     * @return the maximum possible deckSize for the current order.
     */
    public int getMaxDeckSize() {
        int numImagesPerCard = order + 1;
        return numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;
    }

    public void setWordMode(boolean wordMode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(WORD_MODE_PREF_KEY, wordMode);
        editor.apply();

        this.wordMode = wordMode;
    }

    public boolean isWordMode() {
        return wordMode;
    }

    public int getNumImagesInImageSet() {
        return numImagesInImageSet;
    }
}