package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.OptionSet;

/**
 * Activity for different types of pictures and setting the player name.
 */
public class OptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int imageSetPref;
    OptionSet options;
    String playerNamePlaceholder;
    ArrayList<String> validDrawPileSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initOptionSet();

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_options_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createRadioButton();
        setupEntryBox();
        createOrderSpinner();
        createDeckSizeSpinner();
    }

    private void initOptionSet() {
        String defaultValue = getString(R.string.default_picture_type);

        options = OptionSet.getInstance();
        imageSetPref = options.getImageSet();
        playerNamePlaceholder = getString(R.string.txt_player_name_placeholder);
    }

    private void createRadioButton() {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        List<String> deckThemeNames = new ArrayList<>(Arrays.asList(getResources()
                                                            .getStringArray(R.array.str_pic_types)));

        for (String imageSetName : deckThemeNames) {
            int indexOfButton = deckThemeNames.indexOf(imageSetName);

            RadioButton button = new RadioButton(this);
            button.setText(imageSetName);
            button.setOnClickListener(v -> options.setImageSet(indexOfButton));
            radioGroup.addView(button);

            // Select default button:
            if (deckThemeNames.indexOf(imageSetName) == imageSetPref) {
                button.setChecked(true);
            }
        }
    }

    //Valid Draw Pile Sizes are only applicable here, and therefore its getter/setter functions
    //Are not defined in OptionsSet. However, to determine Valid Draw Pile we still need to use an
    //Instance of OptionsSet
    private void setValidDrawPileSizes(){
        //Begin with a populated array;
        //Code for getting ArrayList from string-array adapted from
        //@ https://stackoverflow.com/a/19127223
        ArrayList<String> allDrawPileSizes = new ArrayList<String>
                (Arrays.asList(getResources().getStringArray(R.array.str_draw_pile_sizes)));
        validDrawPileSizes = new ArrayList<>(0);
        int maxDeckSize = options.getMaxDeckSize();

        for(int i = 0; i < allDrawPileSizes.size(); i++){
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
//            checkedSize = checkedSize.replaceAll("\\D","");

            int checkedSizeNumber = Integer.parseInt(checkedSize);
            if(checkedSizeNumber <= maxDeckSize){
                validDrawPileSizes.add(allDrawPileSizes.get(i));
            }
        }
        /*Programmer's note continued:
        Now, the All option is added, with the added feature of showing the max number of cards!
        This means that there is now an int in the string, so doing parseInt on it should no longer
        cause problems. Case in point, onItemSelected() can do parseInt on this string if that
        option is chosen, with no issues.
         */
        validDrawPileSizes.add(getString(R.string.all_option, maxDeckSize));
}

    public ArrayList<String> getValidDrawPileSizes(){
        return validDrawPileSizes;
    }

    private void createOrderSpinner(){
        Spinner orderSpinner = findViewById(R.id.spn_order);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.str_deck_order, android.R.layout.simple_spinner_item);
        int currentOrderNumber = options.getOrder();//Pre-select an option based on saved prefs


        //Code for setting default selected option in Spinner as below adapted from itzhar
        //@ https://stackoverflow.com/a/29129817
        //Conversion from int to string necessary to use with adapter.getPosition
        String order = Integer.toString(currentOrderNumber);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapter);

        int defaultPosition = adapter.getPosition(order);

        orderSpinner.setSelection(defaultPosition);
        orderSpinner.setOnItemSelectedListener(this);
    }

    private void createDeckSizeSpinner(){
        setValidDrawPileSizes();
        updateDeckSizeSpinner();

    }

    private void updateDeckSizeSpinner(){
        Spinner deckSizeSpinner = findViewById(R.id.spn_pile_size);
        ArrayList<String> drawPileSizesOptions = getValidDrawPileSizes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, drawPileSizesOptions);
        //deckSizeSpinner.setAdapter(adapter);
        for(int i = 0; i < drawPileSizesOptions.size(); i++){
            Log.d("updateDeckSizeSpinner option before parseInt: ", drawPileSizesOptions.get(i));
        }
        //Code for setting default selected option in Spinner as below adapted from itzhar
        //@ https://stackoverflow.com/a/29129817
        int currentDeckSizeNumber = options.getDeckSize();
        String currentDeckSize = Integer.toString(currentDeckSizeNumber);
        //Remove all stuff
        //currentDeckSize = currentDeckSize.replaceAll("\\D","");//if All is chosen
        Log.d("CurrentDeckSize String is: ", currentDeckSize);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deckSizeSpinner.setAdapter(adapter);

          //@TODO: Might be nice to change default to be set to "All..." if chosen size now invalid?
          //@TODO: Or, set choice to the next biggest choice possible instead of all?
//        int maxDeckSize = options.getMaxDeckSize();
//
//        for(int i = 0; i < maxDeckSize; i++){
//
//        }
        //Do I need to parse a steing first?

        //I get it. It tries to look for 31, but there is not just any 31
        int defaultPosition = adapter.getPosition(currentDeckSize);//Is this the line?
        if(defaultPosition == -1){
            defaultPosition = adapter.getPosition(getString(R.string.all_option, currentDeckSizeNumber));
        }
        Log.d("Just before the for loop, defaultPosition is....", "" + defaultPosition);
//        if(options.getDeckSize() <= options.getMaxDeckSize()){
//        Use String.format here instead?
//            defaultPosition = adapter.getPosition(getString(R.string.all_option, options.getDeckSize()));

//        }else{
//            defaultPosition = adapter.getPosition(currentDeckSize);
//        }
        for(int i = 0; i < validDrawPileSizes.size(); i++){
            String checkedSize = validDrawPileSizes.get(i);
            checkedSize  = checkedSize.replaceAll("\\D","");//if All is chosen
            Log.d("The thing", "THe thing is:" + checkedSize);
            int pileSizeNumber = Integer.parseInt(checkedSize);
            //Get it, parse it,
            //if the same, set as selection.
        }
        deckSizeSpinner.setSelection(defaultPosition);
        Log.d("AFter ParseInt, defaul position is:", "..." + defaultPosition);//Prints out -1, when all was seleted, meaning that at this time, all wasn't found.
        deckSizeSpinner.setOnItemSelectedListener(this);
        //Return string to be in spinner
        // String.format("%s%d", getString(--id of the all string--), deckSizeNumber)
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spn_order:
                String orderName = parent.getItemAtPosition(position).toString();
                int orderNumber = Integer.parseInt(orderName);
                options.setOrder(orderNumber);//Save selected Order
                createDeckSizeSpinner();//Spinner for pile sizes might now change possible choices
                break;
            case R.id.spn_pile_size:
                String pileSizeName = parent.getItemAtPosition(position).toString();
                pileSizeName = pileSizeName.replaceAll("\\D","");//if All is chosen
                int pileSizeNumber = Integer.parseInt(pileSizeName);
                options.setDeckSize(pileSizeNumber);//Can set deckSize with All, no worries
                Log.d("okay ", "okay: " + pileSizeNumber);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void setupEntryBox() {
        EditText playerNameEntryBox = findViewById(R.id.editTextPlayerNameEntryBox);
        String playerNamePref = options.getPlayerName();

        if (!playerNamePref.matches(playerNamePlaceholder)) {
            playerNameEntryBox.setText(playerNamePref);
        }

        playerNameEntryBox.addTextChangedListener(mTextWatcher);
    }

    // Make it so that the name only saves when the user is finished typing.
    public TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            return;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            return;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            savePlayerName();
        }
    };

    private void savePlayerName() {
        EditText playerNameEntryBox = findViewById(R.id.editTextPlayerNameEntryBox);
        String enteredPlayerName = playerNameEntryBox.getText().toString();

        if (enteredPlayerName.matches("")) {
            options.setPlayerName(playerNamePlaceholder);
        } else {
            options.setPlayerName(enteredPlayerName);
        }
    }

}
