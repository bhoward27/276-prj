package ca.cmpt276.prj.model;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ca.cmpt276.prj.model.Constants.DECK_SIZE_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.DEFAULT_DECK_SIZE;
import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.DEFAULT_FLICKR_IMAGE_SET_SIZE;
import static ca.cmpt276.prj.model.Constants.DEFAULT_ORDER_PREF;
import static ca.cmpt276.prj.model.Constants.DEFAULT_PLAYER_NAME;
import static ca.cmpt276.prj.model.Constants.DEFAULT_WORD_MODE;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_INT_PREF;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.MINIMUM_DECK_SIZE;
import static ca.cmpt276.prj.model.Constants.NAME_PREF;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES_IN_DEFAULT_SETS;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET_SIZE_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.ORDER_PREF_KEY;
import static ca.cmpt276.prj.model.Constants.PREDATOR_IMAGE_SET;
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
    private int flickrImageSetSize;
    private String playerName;
    private int order;
    private int deckSize;
    private boolean wordMode; // when true, some cards will have words appear instead of images.
    private ArrayList<String> validDrawPileSizes;
    private List<String> possibleFlickrImageNames = new ArrayList<>();

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

    public void setValidDrawPileSizes(ArrayList<String> allDrawPileSizes, String all_option) {
        //Begin with a populated array;
        //Code for getting ArrayList from string-array adapted from
        //@ https://stackoverflow.com/a/19127223
        int maxDeckSize = getMaxDeckSize();
        validDrawPileSizes = new ArrayList<>(0);
        for (int i = 0; i < allDrawPileSizes.size(); i++) {
            String checkedSize = allDrawPileSizes.get(i);
            //DEBUGGING NOTE; DELETE THIS IN FINAL PRODUCT:
            /*
            As it turns out, I don't need to clean up all non-numerical characters in any of the
            strings so long as they all have just numerical characters. I received a numberformat
            exception at the parseInt line because:
            1. I had once defined one of the elements in the array to be "All". Therefore,
            when the for loop tried to call replaceAll on that element, the entire string was made
            into "   " (\\D replaces all letters with the specified replacement, "" in this caee).
            Doing parseInt on a string like that causes a NumberFormatException because there are no
            numerical characters at all.

            My (temporary) solution? Remove "All" entirely as an option in the string array and manually add it
            to the arraylist of choosable options AFTER all the strings that actually have numbers
            are checked... see a little below for what I mean...
             */
            int checkedSizeNumber = Integer.parseInt(checkedSize);
            if (checkedSizeNumber <= maxDeckSize) {
                validDrawPileSizes.add(allDrawPileSizes.get(i));
            }

            //all_option will be made frull in calling function via getString(R.string.all_option, maxDeckSize));
        }
                /*Programmer's note continued:
        Now, the All option is added, with the added feature of showing the max number of cards!
        This means that there is now an int in the string, so doing parseInt on it should no longer
        cause problems. Case in point, onItemSelected() can do parseInt on this string if that
        option is chosen, with no issues.
         */
        validDrawPileSizes.add(all_option);
    }

    //Return list for the ui to print
    public ArrayList<String> getValidDrawPileSizes(){
        return validDrawPileSizes;
    }



    public int getNumImagesInImageSet() {
        switch(imageSet) {
            case LANDSCAPE_IMAGE_SET:
                //  intentional fall-through
            case PREDATOR_IMAGE_SET:
                return NUM_IMAGES_IN_DEFAULT_SETS;
            case FLICKR_IMAGE_SET:
                return flickrImageSetSize;
            default:
                throw new UnsupportedOperationException("Error: Invalid imageSet.");
        }
    }

    public void setFlickrImageSetSize(int numImages) {
        if (numImages < 0) {
            throw new IllegalArgumentException("Error: Cannot have a negative number of images.");
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(FLICKR_IMAGE_SET_SIZE_PREF_KEY, numImages);
        editor.apply();

        this.flickrImageSetSize = numImages;
    }

    public int getFlickrImageSetSize() {
        return flickrImageSetSize;
    }

    public int incrementFlickrImageSetSize() {
        int newValue = ++flickrImageSetSize;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(FLICKR_IMAGE_SET_SIZE_PREF_KEY, newValue);
        editor.apply();
        return newValue;
    }

    public int decrementFlickrImageSetSize() {
        if (flickrImageSetSize <= 0) {
            return -1;
        }
        else {
            int newValue = --flickrImageSetSize;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FLICKR_IMAGE_SET_SIZE_PREF_KEY, newValue);
            editor.apply();
            return newValue;
        }
    }

    public List<String> getPossibleFlickrImageNames() {
        return possibleFlickrImageNames;
    }

    public void removePossibleFlickrImageNames(String name) {
        possibleFlickrImageNames.remove(name);
    }

    public void addPossibleFlickrImageNames(String name) {
        possibleFlickrImageNames.add(name);
    }

    public void clearPossibleFlickrImageNames() {
        possibleFlickrImageNames.clear();
    }
}