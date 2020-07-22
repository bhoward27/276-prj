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
 * Activity for different types of pictures and setting the player name.
 */
public class OptionsActivity extends AppCompatActivity {
    PrefsManager prefsManager;
    int imageSetIndexPref;
    String playerNamePlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initPrefs();

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_options_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createRadioButton();
        setupEntryBox();
    }

    private void initPrefs() {
        String defaultValue = getString(R.string.default_picture_type);

        prefsManager = PrefsManager.getInstance();
        imageSetIndexPref = prefsManager.getImageSetSelected();
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
            button.setOnClickListener(v -> prefsManager.saveImageSetSelected(indexOfButton,
                                                                                imageSetName));
            radioGroup.addView(button);

            // Select default button:
            if (deckThemeNames.indexOf(imageSetName) == imageSetIndexPref) {
                button.setChecked(true);
            }
        }
    }

    private void setupEntryBox() {
        EditText playerNameEntryBox = findViewById(R.id.editTextPlayerNameEntryBox);
        String playerNamePref = prefsManager.getName(playerNamePlaceholder);

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
            prefsManager.saveName(playerNamePlaceholder);
        } else {
            prefsManager.saveName(enteredPlayerName);
        }
    }
}
