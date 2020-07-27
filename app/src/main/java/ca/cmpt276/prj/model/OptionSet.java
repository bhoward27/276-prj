package ca.cmpt276.prj.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import ca.cmpt276.prj.R;

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
    private String playerName;
    private int order;
    private int deckSize;
    private boolean wordMode; // when true, some cards will have words appear instead of images.
    private ArrayList<String> validDrawPileSizes;

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




    //------

    //Are there things I can move to OptionsActivity only...?
    //
    //Valid Draw Pile Sizes are only applicable here, and therefore its getter/setter functions
    //Are not defined in OptionsSet. However, to determine Valid Draw Pile we still need to use an
    //Instance of OptionsSet

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

    //Creation of orderSPinner in UI

//    private void createDeckSizeSpinner(){
//        setValidDrawPileSizes();//call with param using getValidDrawSize
//        updateDeckSizeSpinner();//This function is from the OptionsActivity
//
//    }
////
////    private void updateDeckSizeSpinner(){
////        Spinner deckSizeSpinner = findViewById(R.id.spn_pile_size);
////        ArrayList<String> drawPileSizesOptions = getValidDrawPileSizes();
////        //Code for setting default selected option in Spinner as below adapted from itzhar
////        //@ https://stackoverflow.com/a/29129817
////        int currentDeckSizeNumber = options.getDeckSize();
////        String currentDeckSize = Integer.toString(currentDeckSizeNumber);
////
////        //Dynamic allocation of ArrayAdapter via ArrayLists adapted from Hiral Vadodaria
////        //@ https://stackoverflow.com/a/7818488
////        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
////                android.R.layout.simple_spinner_item, drawPileSizesOptions);
////
////        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////        deckSizeSpinner.setAdapter(adapter);
////
////        //@TODO: Might be nice to change default to be set to "All..." if chosen size now invalid?
////        //@TODO: Or, set choice to the next biggest choice possible instead of all?
////
////        int defaultPosition = adapter.getPosition(currentDeckSize);//Is this the line?
////        //getPosition returns -1 when the specified thing (currentDeckSize) is not found in
////        //The ArrayAdapter. This can only happen if the all_option option from draw_pile_sizes.xml
////        // was selected because currentDeckSize is only a number at this point and all_option
////        //can't match with the number
////        if(defaultPosition == -1){
////            //all_option was selected; make it the parameter for a call to getPosition using
////            //currentDeckSizeNumber to match the exact string value as stored in adapter.
////            //This allows the all_option option to be properly restored as the currently selected
////            //draw pile size if it was selected the last time OptionsActivity was opened.
////            defaultPosition = adapter.getPosition(getString(R.string.all_option, currentDeckSizeNumber));
////        }
////        deckSizeSpinner.setSelection(defaultPosition);
////        deckSizeSpinner.setOnItemSelectedListener(this);
////    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch(parent.getId()){
//            case R.id.spn_order:
//                String orderName = parent.getItemAtPosition(position).toString();
//                int orderNumber = Integer.parseInt(orderName);
//                options.setOrder(orderNumber);//Save selected Order
//                createDeckSizeSpinner();//Spinner for pile sizes might now change possible choices
//                break;
//            case R.id.spn_pile_size:
//                String pileSizeName = parent.getItemAtPosition(position).toString();
//                //Remove any letters part of the chosen option (applicable to all_option)
//                //So parseInt works with just blank spaces and numbers
//                pileSizeName = pileSizeName.replaceAll("\\D","");
//                int pileSizeNumber = Integer.parseInt(pileSizeName);
//                options.setDeckSize(pileSizeNumber);
//                break;
//            default:
//                break;
//        }
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }

}