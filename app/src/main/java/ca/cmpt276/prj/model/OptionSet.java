package ca.cmpt276.prj.model;

import android.content.Context;
import android.content.SharedPreferences;

import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.DEFAULT_PLAYER_NAME;
import static ca.cmpt276.prj.model.Constants.DEFAULT_WORD_MODE;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_INT_PREF;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.NAME_PREF;
import static ca.cmpt276.prj.model.Constants.PREDATOR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.PREFS;
import static ca.cmpt276.prj.model.Constants.WORD_MODE_PREF_KEY;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OptionSet {
    private static OptionSet instance;
    private SharedPreferences prefs;

    private int imageSet;
    private String playerName;
    private int order;
    private int deckSize;
    private boolean wordMode;

    public static final int ASCII_OFFSET = 97;

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

    //  Wait for Michael's reply to implement.
    private void loadOrderPref() {
        
    }

    //  Wait for Michael's reply to implement.
    private void loadDeckSizePref() {

    }

    private void loadWordModePref() {
        wordMode = prefs.getBoolean(WORD_MODE_PREF_KEY, DEFAULT_WORD_MODE);
    }

    public void setImageSet(int imageSet) {
        if (imageSet < LANDSCAPE_IMAGE_SET || imageSet > FLICKR_IMAGE_SET) {
            throw new IllegalArgumentException("Error: Invalid imageSet argument. ImageSet " +
                    "must be in range [" + LANDSCAPE_IMAGE_SET + ", " + FLICKR_IMAGE_SET + "].");
        }
        //  This ensures that the imageSetPrefix works.
        boolean canBeMappedToALowercaseLetter = (imageSet >= 0 && imageSet < 26);
        if (!canBeMappedToALowercaseLetter) {
            throw new IllegalArgumentException("Error: Invalid imageSet argument. ImageSet " +
                    "must at least be in range [0, 26).");
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(IMAGE_SET_INT_PREF, imageSet);
        editor.apply();

        this.imageSet = imageSet;
    }

    /**
     * This method maps the image set number to a lowercase letter and returns it as a string.
     * For example, 0 gets mapped to "a", 1 to "b", and so on.
     */
    public String getImageSetPrefix() {
         return String.valueOf((char) (imageSet + ASCII_OFFSET));
    }

    //  Not sure whether this is needed or would be used at all.
//    public String getImageSetName() {
//        String imageSetName;
//        switch(imageSet) {
//            case LANDSCAPE_IMAGE_SET:
//                imageSetName =
//                break;
//            case PREDATOR_IMAGE_SET:
//
//                break;
//            case FLICKR_IMAGE_SET:
//
//                break;
//            default:
//                throw new UnsupportedOperationException("Error: imageSet must be in range [0, 2].");
//        }
//    }
}