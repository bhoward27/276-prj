package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.PrefsManager;

/**
 * Activity for different types of pictures and setting the player name
 */
public class OptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    PrefsManager prefsManager;
    int savedValue;
    String defaultName;
    String defaultDrawPileSize;
    String defaultOrder;
    String savedOrder;
    String savedDrawPileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initPrefs();

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_options_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createThemeRadioButtons();
        initializeOrderSpinner();
        initializeDrawPileSpinner();
      //  createDrawPileSizeRadioButtons();
        createNameChangeFields();

    }

    private void initPrefs() {
        String defaultValue = getString(R.string.default_picture_type);
        String defaultDrawPileSize = getString(R.string.default_draw_pile_size);//in draw_pile_sizes.xml
        prefsManager = PrefsManager.getInstance();
        savedValue = prefsManager.getTypePictureInstalledInt(defaultValue);//This seems to be for setting default?
        savedDrawPileSize = prefsManager.getDrawPileSize(defaultDrawPileSize);
        defaultName = getString(R.string.txt_placeholder_name);
    }

    private void createThemeRadioButtons() {
        RadioGroup group = findViewById(R.id.radioGroup);
        List<String> defStringArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_pic_types)));


        // Create the radio buttons:
        for (String str : defStringArray) {
            RadioButton button = new RadioButton(this);
            button.setText(str);

            // Set on-click callbacks
            button.setOnClickListener(v -> prefsManager.saveStrTypeInstalled(str));

            // Add to radio group:
            group.addView(button);

            // Select default button:
            if(defStringArray.indexOf(str)+1 == savedValue){
                button.setChecked(true);
            }

        }
    }
/*
        RadioGroup radioOrder = findViewById(R.id.radio_order);
        List<String> orders = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_deck_order)));
        // Create the radio buttons:
        for (String str : orders) {
            RadioButton button = new RadioButton(this);
            button.setText(str);

            // Set on-click callbacks
            button.setOnClickListener(v -> prefsManager.saveStrTypeInstalled(str));

            // Add to radio group:
            radioOrder.addView(button);

            // Select default button:
            if(orders.indexOf(str)+1 == savedValue){
                button.setChecked(true);
            }
        }

    */
    private void initializeOrderSpinner(){
        Spinner orderSpinner = findViewById(R.id.spn_order);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.str_deck_order, android.R.layout.simple_spinner_item);
        List<String> allOrders = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_deck_order)));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapter);
        orderSpinner.setOnItemSelectedListener(this);

        selectDefaultSpinnerOption(orderSpinner, allOrders);

    }

    private void initializeDrawPileSpinner() {
        Spinner drawPileSpinner = findViewById(R.id.spn_draw_pile);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.str_draw_pile_sizes, android.R.layout.simple_spinner_item);
        List<String> allPileSizes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_draw_pile_sizes)));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drawPileSpinner.setAdapter(adapter);
        drawPileSpinner.setOnItemSelectedListener(this);

            selectDefaultSpinnerOption(drawPileSpinner, allPileSizes);
    }

    private void selectDefaultSpinnerOption(Spinner spinner, List<String>options){
        int index = 0;
        for(String str : options){
            if(str.equals(savedOrder)){
                spinner.setSelection(index);
            }
            index++;
        }
    }

//    private void updateDrawPileSpinnreOptions(String chosenOrder){
//        int numberToCompare = Integer.parseInt(chosenOrder);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Multiple spinners are used in the same activity; a switch statement
        //Will diferentiate which spinner is being used.
        switch(parent.getId()){
            case R.id.spn_order:
                String chosenOrder = parent.getItemAtPosition(position).toString();
                prefsManager.saveDrawPileSize(chosenOrder);
               // updateDrawPileSpinnreOptions(chosenOrder);
                //Toast.makeText(parent.getContext(), text, Toast.LENGTH_LONG).show();
                    break;
            case R.id.spn_draw_pile:
                prefsManager.saveOrder(parent.getItemAtPosition(position).toString());
                break;
            default:
                break;
        }
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch(parent.getId()){
            case R.id.spn_order:
                break;
            default:
                break;
        }
    }
    //private void createDrawPileSizeRadioButtons() {
      //  String currentOrder = prefsManager.getOrder("Order 2");
//        RadioGroup orders = findViewById(R.id.radio_order);
//        int currentOrderID = orders.getCheckedRadioButtonId();
//        RadioButton currentOrder = orders.findViewById(currentOrderID);
//        String orderName = (String) currentOrder.getText();
        //String Radio button
//        RadioGroup radioDrawPileSize = findViewById(R.id.radio_draw_pile_size);
//        List<String> allPileSizes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_draw_pile_sizes)));
//        int index = 0;
//        // Create the radio buttons:
//        for (String str : allPileSizes) {
//
//            RadioButton button = new RadioButton(this);
//            button.setText(str);
//            // Set on-click callbacks
//            button.setOnClickListener(v -> prefsManager.saveDrawPileSize(str));
//            String drawSizeName = (String) button.getText();
//            // Add to radio group:
//            radioDrawPileSize.addView(button);
//
//            // Select default button:
//            //if(allPileSizes.indexOf(str)+1 == savedValue){
//            if ((Integer.parseInt(currentOrder) + 1) > Integer.parseInt(drawSizeName)){//If the button cannot be clicked due to the currently selected order...
//                //Code adapted from crocboy
//                //@ https://stackoverflow.com/a/11213814
//                button.setEnabled(false);
//            } else {
//                if (allPileSizes.get(index).equals(savedDrawPileSize)) {
//                    button.setChecked(true);
//                }
//            }
//            index++;
//        }
//    }
    private void createNameChangeFields() {
        EditText edtName = findViewById(R.id.editTextTextPersonName);
        String nameFromPrefs = prefsManager.getName(defaultName);

        if (!nameFromPrefs.matches(defaultName)) {
            edtName.setText(nameFromPrefs);
        }

        edtName.addTextChangedListener(mTextWatcher);
    }

    // Make it so that the name only saves when the user is finished typing
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
            saveName();
        }
    };

    private void saveName() {
        EditText edtName = findViewById(R.id.editTextTextPersonName);
        String nameFromEdt = edtName.getText().toString();

        if (nameFromEdt.matches("")) {
            prefsManager.saveName(defaultName);
        } else {
            prefsManager.saveName(nameFromEdt);
        }

    }


}
