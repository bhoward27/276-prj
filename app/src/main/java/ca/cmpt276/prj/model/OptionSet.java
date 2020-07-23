package ca.cmpt276.prj.model;

import android.content.Context;
import android.content.SharedPreferences;

import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.PREDATOR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.PREFS;
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

    }

    private void loadPlayerNamePref() {

    }

    private void loadOrderPref() {

    }

    private void loadDeckSizePref() {

    }

    private void loadWordModePref() {

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

    public void setImageSet(int imageSet) {
        //  assert that imageSet >= 0 && imageSet < 26. This ensures that the imageSetPrefix works
    }
}