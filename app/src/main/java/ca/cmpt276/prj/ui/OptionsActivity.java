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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initPrefs();

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_options_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createRadioButton();
        createNameChangeFields();
        createOrderSpinner();
        createDrawPileSizeSpinner();

    }

    private void initPrefs() {
        String defaultValue = getString(R.string.default_picture_type);

        prefsManager = PrefsManager.getInstance();
        savedValue = prefsManager.getImageSetSelected();
        defaultName = getString(R.string.txt_placeholder_name);
    }

    private void createOrderSpinner(){
        Spinner orderSpinner = findViewById(R.id.spn_order);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.str_deck_order, android.R.layout.simple_spinner_item);
        String currentOrder = prefsManager.getOrderSelected();
        int currentDifferentCardsAmount = prefsManager.getDifferentCardsAmountSelected();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapter);
        int defaultPosition = adapter.getPosition(currentOrder);
        orderSpinner.setSelection(defaultPosition);
        orderSpinner.setOnItemSelectedListener(this);
    }

    private void createDrawPileSizeSpinner(){
        Spinner orderDrawPileSize = findViewById(R.id.spn_draw_pile);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.str_draw_pile_sizes, android.R.layout.simple_spinner_item);
        String currentDrawPileSize = prefsManager.getDrawPileSizeSelected();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderDrawPileSize.setAdapter(adapter);
        int defaultPosition = adapter.getPosition(currentDrawPileSize);
        orderDrawPileSize.setSelection(defaultPosition);
        orderDrawPileSize.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Multiple spinners exist in this activity, and they must all use the same
        //overriden onItemSelected/onNothingSelected functions. a switch statement
        //will identify which spinner is using the function via parent ID.
        switch(parent.getId()) {
            case R.id.spn_order:
                String selectedOrder = parent.getItemAtPosition(position).toString();
                prefsManager.saveOrderSelcted(selectedOrder);
                break;
            case R.id.spn_draw_pile:
                String selectedDrawPileSize = parent.getItemAtPosition(position).toString();
                prefsManager.saveDrawPileSizeSelected(selectedDrawPileSize);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void createRadioButton() {
        RadioGroup group = findViewById(R.id.radioGroup);
        List<String> defStringArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.str_pic_types)));

        // Create the radio buttons:
        for (String imageSetName : defStringArray) {
            int indexOfButton = defStringArray.indexOf(imageSetName);

            RadioButton button = new RadioButton(this);
            button.setText(imageSetName);

            // Set on-click callbacks
            button.setOnClickListener(v -> prefsManager.saveImageSetSelected(indexOfButton, imageSetName));

            // Add to radio group:
            group.addView(button);

            // Select default button:
            if(defStringArray.indexOf(imageSetName) == savedValue){
                button.setChecked(true);
            }

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
