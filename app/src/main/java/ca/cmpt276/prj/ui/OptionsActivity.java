package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.OptionSet;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;

/**
 * Activity for different types of pictures and setting the player name.
 */
public class OptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
	int imageSetPref;
	int minimumReqImages;
	OptionSet options;
	String playerNamePlaceholder;
	ScoreManager manager;
	List<RadioButton> radioButtonList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		initOptionSet();

		Objects.requireNonNull(getSupportActionBar()).setTitle(getString(
				R.string.title_options_activity));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int numImagesPerCard = options.getOrder() + 1;
		minimumReqImages = numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;

		createRadioButton();
		setupEntryBox();
		createOrderSpinner();
		createDeckSizeSpinner();
		setupCheckBox();
		setUpFlickrButton();
		updateFlickrAmountText();
	}

	private void initOptionSet() {
		options = OptionSet.getInstance();
		imageSetPref = options.getImageSet();
		playerNamePlaceholder = getString(R.string.txt_player_name_placeholder);
		manager = ScoreManager.getInstance();
	}

	private boolean areThereEnoughFlickImages(int currentFlickrPhotos) {
		// (Total number of cards is images^2 - images + 1) ==> number of total images
		int numImagesPerCard = options.getOrder() + 1;
		minimumReqImages = numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;
		if (currentFlickrPhotos < minimumReqImages) {
			return false;
		}
		return true;
	}

	private void createRadioButton() {

		RadioGroup radioGroup = findViewById(R.id.radioGroup);
		List<String> deckThemeNames = new ArrayList<>(Arrays.asList(getResources()
				.getStringArray(R.array.str_pic_types)));
		for (String imageSetName : deckThemeNames) {
			int indexOfButton = deckThemeNames.indexOf(imageSetName);

			RadioButton button = new RadioButton(this);
			CheckBox chck = findViewById(R.id.chckWordMode);
			button.setText(imageSetName);
			if (deckThemeNames.indexOf(imageSetName) != FLICKR_IMAGE_SET) {
				button.setOnClickListener(v -> {
					chck.setEnabled(true);
					options.setImageSet(indexOfButton);
					updateFlickrAmountText();
				});

			} else {
				// for flickr radio button

				button.setOnClickListener(v -> {
					// don't allow the user to play the game with not enough images
					if (areThereEnoughFlickImages(options.getNumFlickrImages())) {
						options.setImageSet(indexOfButton);
					} else {
						Toast.makeText(getApplicationContext(), getString(
								R.string.txt_attempted_leave_with_flickr_photo_amount_not_ok),
								Toast.LENGTH_LONG).show();
					}
					chck.setChecked(false);
					chck.setEnabled(false);
					options.setWordMode(false);
					updateFlickrAmountText();
				});
			}
			radioButtonList.add(button);
			radioGroup.addView(button);

			// select default button:
			if (deckThemeNames.indexOf(imageSetName) == imageSetPref) {
				button.setChecked(true);
			}

		}
	}

	private void setupCheckBox() {
		CheckBox chck = findViewById(R.id.chckWordMode);
		if (options.isWordMode()) {
			chck.setChecked(true);
		}
		if (options.getImageSet() == FLICKR_IMAGE_SET) {
			options.setWordMode(false);
			chck.setChecked(false);
			chck.setEnabled(false);
		}
		chck.setOnCheckedChangeListener((buttonView, isChecked) -> {
			// don't let word mode be used if flickr is the imageset
			if (options.getImageSet() != FLICKR_IMAGE_SET) {
				options.setWordMode(isChecked);
			} else {
				options.setWordMode(false);
				buttonView.setEnabled(false);
				buttonView.setChecked(false);
			}
		});
	}

	private void createOrderSpinner() {
		Spinner orderSpinner = findViewById(R.id.spn_order);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.str_deck_order, android.R.layout.simple_spinner_item);
		int currentOrderNumber = options.getOrder();//Pre-select an option based on saved prefs
		// code for setting default selected option in Spinner as below adapted from itzhar
		// @ https://stackoverflow.com/a/29129817
		// conversion from int to string necessary to use with adapter.getPosition
		String order = Integer.toString(currentOrderNumber);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		orderSpinner.setAdapter(adapter);

		int defaultPosition = adapter.getPosition(order);

		orderSpinner.setSelection(defaultPosition);
		orderSpinner.setOnItemSelectedListener(this);
	}


	private void createDeckSizeSpinner() {
		sendPileSizesToOptionSet();
		updateDeckSizeSpinner();
	}

	// send resource of draw pile options as an ArrayList to options to store.
	private void sendPileSizesToOptionSet() {
		ArrayList<String> allDrawPileSizes = new ArrayList<String>
				(Arrays.asList(getResources().getStringArray(R.array.str_draw_pile_sizes)));
		int maxDeckSize = options.getMaxDeckSize();
		String allOption = getString(R.string.all_option, maxDeckSize);
		// because options has no access to resources, use the UI class to get the resource and
		// pass it as a string
		options.setValidDrawPileSizes(allDrawPileSizes, allOption);
	}

	private void updateDeckSizeSpinner() {
		Spinner deckSizeSpinner = findViewById(R.id.spn_pile_size);
		// ArrayList of valid pile sizes are stored; just get them from options now.
		ArrayList<String> drawPileSizesOptions = options.getValidDrawPileSizes();

		// code for setting default selected option in Spinner as below adapted from itzhar
		// @ https://stackoverflow.com/a/29129817
		int currentDeckSizeNumber = options.getDeckSize();
		String currentDeckSize = Integer.toString(currentDeckSizeNumber);

		// dynamic allocation of ArrayAdapter via ArrayLists adapted from Hiral Vadodaria
		//@ https://stackoverflow.com/a/7818488
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, drawPileSizesOptions);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		deckSizeSpinner.setAdapter(adapter);

		int defaultPosition = adapter.getPosition(currentDeckSize);//Returns -1 when not found
		if (defaultPosition == -1) {
			// all_option was selected; make it the parameter for a call to getPosition using
			// currentDeckSizeNumber to match the exact string value as stored in adapter.
			// This allows the all_option option to be properly restored as the currently selected
			// draw pile size if it was selected the last time OptionsActivity was opened.
			defaultPosition = adapter.getPosition(getString(R.string.all_option,
					currentDeckSizeNumber));
		}
		deckSizeSpinner.setSelection(defaultPosition);
		deckSizeSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
			case R.id.spn_order:
				String orderName = parent.getItemAtPosition(position).toString();
				int orderNumber = Integer.parseInt(orderName);
				options.setOrder(orderNumber);// save selected Order
				createDeckSizeSpinner();// spinner for pile sizes might now change possible choices
				updateFlickrAmountText();

				if (!areThereEnoughFlickImages(options.getFlickrImageSetSize())
						&& options.getImageSet() == FLICKR_IMAGE_SET) {
					options.setImageSet(DEFAULT_IMAGE_SET);
					Toast.makeText(getApplicationContext(), getString(
							R.string.txt_attempted_leave_with_flickr_photo_amount_not_ok),
							Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.spn_pile_size:
				String pileSizeName = parent.getItemAtPosition(position).toString();
				// remove any letters part of the chosen option (applicable to all_option)
				// so parseInt works with just blank spaces and numbers
				pileSizeName = pileSizeName.replaceAll("\\D", "");
				int pileSizeNumber = Integer.parseInt(pileSizeName);
				options.setDeckSize(pileSizeNumber);
				break;
			default:
				break;
		}

		// change prefix which identifies options associated with a score
		manager.setScorePrefix(options.getOrder(), options.getDeckSize());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}


	private void setupEntryBox() {
		EditText playerNameEntryBox = findViewById(R.id.editTextPlayerNameEntryBox);
		String playerNamePref = options.getPlayerName();

		// match the "regex"
		String pattern = Pattern.quote(playerNamePlaceholder);

		if (!playerNamePref.matches(pattern)) {
			playerNameEntryBox.setText(playerNamePref);
		}

		playerNameEntryBox.addTextChangedListener(mTextWatcher);
	}

	// make it so that the name only saves when the user is finished typing.
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

	private void setUpFlickrButton() {
		Button button = findViewById(R.id.btnFlickrPhotos);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchPhotoGalleryActivity();
			}
		});
	}

	private void updateFlickrAmountText() {
		// turn on Flickr mode to see/change the number of flickr images.
		TextView currentFlickrPhotoCount = findViewById(R.id.txt_flickr_number);
		// the user is only allowed to see/set Flickr images if the Flickr image set is selected
		if (radioButtonList.get(FLICKR_IMAGE_SET).isChecked()) {

			String flickrPhotoCountText;
			// get the number of currently selected things; that will be displayed
			int currentFlickrPhotos = options.getNumFlickrImages();

			if (areThereEnoughFlickImages(currentFlickrPhotos)) {
				flickrPhotoCountText = String.format(getString(
						R.string.txt_flickr_photo_amount_ok), currentFlickrPhotos);
				currentFlickrPhotoCount.setTextColor(ContextCompat.getColor(
						OptionsActivity.this, R.color.blue));
			} else {
				flickrPhotoCountText = String.format(getString(
						R.string.txt_flickr_photo_amount_not_ok), currentFlickrPhotos,
						minimumReqImages);
				currentFlickrPhotoCount.setTextColor(ContextCompat.getColor(
						OptionsActivity.this, R.color.red));
				options.setImageSet(DEFAULT_IMAGE_SET);
			}
			currentFlickrPhotoCount.setText(flickrPhotoCountText);
		} else {
			currentFlickrPhotoCount.setText(getString(R.string.txt_flickr_photo_not_set));
			currentFlickrPhotoCount.setTextColor(ContextCompat.getColor(
					OptionsActivity.this, R.color.grey));
		}
	}

	private void launchPhotoGalleryActivity() {
		Intent intent = new Intent(OptionsActivity.this, EditImageSetActivity.class);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();

		updateFlickrAmountText();
	}
}
