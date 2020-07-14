package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
public class OptionsActivity extends AppCompatActivity {
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

    }

    private void initPrefs() {
        prefsManager = PrefsManager.getInstance();

        String defaultValue = getString(R.string.default_picture_type);
        savedValue = prefsManager.getTypePictureInstalledInt(defaultValue);
        defaultName = getString(R.string.txt_placeholder_name);
    }

    private void createRadioButton() {
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
