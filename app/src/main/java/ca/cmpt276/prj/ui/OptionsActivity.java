package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.PrefsManager;

/**
 * Activity for different types of pictures and setting the player name
 */
public class OptionsActivity extends AppCompatActivity {
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
        createOrderRadioButtons();
        createDrawPileSizeRadioButtons();
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

    private void createOrderRadioButtons(){
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
    }
    private void createDrawPileSizeRadioButtons(){

        RadioGroup radioDrawPileSize = findViewById(R.id.radio_draw_pile_size);
        List<String> allPileSizes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_draw_pile_sizes)));
        int index = 0;
        // Create the radio buttons:
        for (String str : allPileSizes) {

            RadioButton button = new RadioButton(this);
            button.setText(str);

            // Set on-click callbacks
            button.setOnClickListener(v -> prefsManager.saveDrawPileSize(str));

            // Add to radio group:
            radioDrawPileSize.addView(button);

            // Select default button:
            //if(allPileSizes.indexOf(str)+1 == savedValue){
            if(allPileSizes.get(index).equals(savedDrawPileSize)){
                button.setChecked(true);
            }
            index++;
        }
    }
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
