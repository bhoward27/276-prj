package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import ca.cmpt276.prj.model.CardExporter;
import ca.cmpt276.prj.model.OptionsManager;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.CUSTOM_IMAGE_SET;

/**
 * Activity for different types of pictures and setting the player name.
 */
		public class OptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
			public static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
			CardExporter exporter; //Only instantiated when export button works
			int imageSetPref;
			int minimumReqImages;
			OptionsManager optionsManager;
			String playerNamePlaceholder;
			ScoreManager manager;
			List<RadioButton> radioButtonList = new ArrayList<>();
			Boolean storagePermissionGranted;
			Boolean isWordsModeDisabled;

			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_options);

		initOptionsManager();

		Objects.requireNonNull(getSupportActionBar()).setTitle(getString(
				R.string.title_options_activity));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int numImagesPerCard = optionsManager.getOrder() + 1;
		minimumReqImages = numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;

		//True if Permission was granted
		storagePermissionGranted = checkSelfPermission(
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED;

		updateDisablingWordMode();
		setupImageSetRadioButtons();
		setupDifficultyRadioButtons();
		setupEntryBox();
		createOrderSpinner();
		updateDeckSizeSpinner();
		setupWordModeCheckbox();
		setUpFlickrButton();
		updateFlickrAmountText();
		setUpExportCardsButton();
	}

	private void updateDisablingWordMode(){
		if(optionsManager.getImageSet() == CUSTOM_IMAGE_SET) {
			isWordsModeDisabled = true;
		} else {
			isWordsModeDisabled = false;
		}
	}

	private void setUpExportCardsButton(){
		Button exportPhotos = findViewById(R.id.btnGenerateCardPhotos);
		exportPhotos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(storagePermissionGranted){
					exportCards();
				}else{
					//RequestPermissions to export cards
					ActivityCompat.requestPermissions(OptionsActivity.this,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							STORAGE_PERMISSION_REQUEST_CODE);
				}
			}
		});
	}

	// Code adapted from Android Developer's site
	// @ https://developer.android.com/training/permissions/requesting#java
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions,
										   int[] grantResults) {
		 if(requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 &&
						grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					exportCards();
				} else {
					// user of shouldShowRequestPermissionRationale to check if the user
					// selected "Never Ask Again" and denied permssion adapted from Emanuel Moecklin
					// @ https://stackoverflow.com/a/31925748
					boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
					if (!showRationale) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.tst_user_refused_storage_permission_with_never_ask_again),
								Toast.LENGTH_LONG).show();
					} else {
						//Tell user they can't save unless they give permission.
						Toast.makeText(getApplicationContext(),
								getString(R.string.tst_user_refused_storage_permission),
								Toast.LENGTH_LONG).show();
					}
				}
		}
	}

	private void exportCards(){
		Log.v("Ya got to exportCards!","Woohoo!");
			Context context = OptionsActivity.this;
			//In CardExporter's constructor, saveBitMaps() saves files to the Pictures Folder
			exporter = new CardExporter(context);
			Toast.makeText(getApplicationContext(), getString(
			R.string.tst_show_exported_card_photos_directory)
					, Toast.LENGTH_LONG).show();
	}

	private void initOptionsManager() {
		OptionsManager.instantiate(this);
		optionsManager = OptionsManager.getInstance();
		imageSetPref = optionsManager.getImageSet();
		playerNamePlaceholder = getString(R.string.txt_player_name_placeholder);
		manager = ScoreManager.getInstance();
	}

	// Adapted from https://stackoverflow.com/a/37496736
	private boolean areThereEnoughFlickImages(int currentFlickrPhotos) {
		// (Total number of cards is images^2 - images + 1) ==> number of total images
		int numImagesPerCard = optionsManager.getOrder() + 1;
		minimumReqImages = numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;
		if (currentFlickrPhotos < minimumReqImages) {
			return false;
		}
		return true;
	}

	private void setupImageSetRadioButtons() {

		RadioGroup radioGroup = findViewById(R.id.rgImageSet);
		List<String> deckThemeNames = new ArrayList<>(Arrays.asList(getResources()
				.getStringArray(R.array.str_pic_types)));
		for (String imageSetName : deckThemeNames) {
			int indexOfButton = deckThemeNames.indexOf(imageSetName);

			RadioButton button = new RadioButton(this);
			CheckBox chck = findViewById(R.id.chckWordMode);
			button.setText(imageSetName);
			if (deckThemeNames.indexOf(imageSetName) != CUSTOM_IMAGE_SET) {
				button.setOnClickListener(v -> {
					isWordsModeDisabled = false;
					chck.setEnabled(true);
					optionsManager.setImageSet(indexOfButton);
					updateFlickrAmountText();
				});
			} else {

				// for flickr radio button
				button.setOnClickListener(v -> {

					isWordsModeDisabled = true;

					// don't allow the user to play the game with not enough images
					if (areThereEnoughFlickImages(optionsManager.getFlickrImageSetSize())) {
						optionsManager.setImageSet(indexOfButton);
						optionsManager.setWordMode(false);
					} else {
						//Still have to tell difficulty to prevent words mode from happening
						Toast.makeText(getApplicationContext(), getString(
								R.string.txt_attempted_leave_with_flickr_photo_amount_not_ok),
								Toast.LENGTH_LONG).show();
					}
					chck.setChecked(false);
					chck.setEnabled(false);
					optionsManager.setWordMode(false);
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

	private void setupDifficultyRadioButtons() {
		RadioGroup radioGroup = findViewById(R.id.rgDifficulty);
		List<String> difficultyNames = new ArrayList<>(Arrays.asList(getResources()
				.getStringArray(R.array.str_difficulty_names)));
		for (String difficultyName : difficultyNames) {
			int indexOfButton = difficultyNames.indexOf(difficultyName);

			RadioButton button = new RadioButton(this);
			CheckBox chck = findViewById(R.id.chckWordMode);
			button.setText(difficultyName);
			button.setOnClickListener(v -> {
				Log.d("THe thing is:", optionsManager.getImageSet() + "");
				chck.setEnabled(true);
				optionsManager.setDifficulty(indexOfButton);
				setupWordModeCheckbox();
			});

			radioButtonList.add(button);
			radioGroup.addView(button);

			// select default button:
			if (difficultyNames.indexOf(difficultyName) == optionsManager.getDifficulty()) {
				button.setChecked(true);
			}
		}
		setupWordModeCheckbox();
	}

	private void setupWordModeCheckbox() {
		CheckBox chck = findViewById(R.id.chckWordMode);
		if (optionsManager.isWordMode()) {
			chck.setChecked(true);
		}
		if (optionsManager.getImageSet() == CUSTOM_IMAGE_SET) {
			optionsManager.setWordMode(false);
			chck.setEnabled(false);
			chck.setChecked(false);
		}
		chck.setOnCheckedChangeListener((buttonView, isChecked) -> {
			// don't let word mode be used if custom is the image set
			if (optionsManager.getImageSet() != CUSTOM_IMAGE_SET) {
				optionsManager.setWordMode(isChecked);
			}

			//For if Custom IS checked; disable the button entirely
			if (isWordsModeDisabled == true){
				optionsManager.setWordMode(false);
				buttonView.setEnabled(false);
				buttonView.setChecked(false);
			}else{
				buttonView.setEnabled(true);
			}
		});
	}

	private void createOrderSpinner() {
		Spinner orderSpinner = findViewById(R.id.spn_order);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.str_deck_order, android.R.layout.simple_spinner_item);
		int currentOrderNumber = optionsManager.getOrder();//Pre-select an option based on saved prefs
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

	private void updateDeckSizeSpinner() {
		Spinner deckSizeSpinner = findViewById(R.id.spn_pile_size);
		// ArrayList of valid pile sizes are stored; just get them from options now.
		ArrayList<String> drawPileSizesOptions = optionsManager.getValidDrawPileSizes();

		// code for setting default selected option in Spinner as below adapted from itzhar
		// @ https://stackoverflow.com/a/29129817
		int currentDeckSizeNumber = optionsManager.getDeckSize();
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
				optionsManager.setOrder(orderNumber);// save selected Order
				updateDeckSizeSpinner();// spinner for pile sizes might now change possible choices
				updateFlickrAmountText();

				if (!areThereEnoughFlickImages(optionsManager.getFlickrImageSetSize())
						&& optionsManager.getImageSet() == CUSTOM_IMAGE_SET) {
					optionsManager.setImageSet(DEFAULT_IMAGE_SET);
					Toast.makeText(getApplicationContext(), getString(
							R.string.txt_attempted_leave_with_flickr_photo_amount_not_ok),
							Toast.LENGTH_LONG).show();
				} else if (areThereEnoughFlickImages(optionsManager.getFlickrImageSetSize())
						&& radioButtonList.get(CUSTOM_IMAGE_SET).isChecked()) {
					optionsManager.setImageSet(CUSTOM_IMAGE_SET);
				}
				break;
			case R.id.spn_pile_size:
				String pileSizeName = parent.getItemAtPosition(position).toString();
				// remove any letters part of the chosen option (applicable to all_option)
				// so parseInt works with just blank spaces and numbers
				pileSizeName = pileSizeName.replaceAll("\\D", "");
				int pileSizeNumber = Integer.parseInt(pileSizeName);
				optionsManager.setDeckSize(pileSizeNumber);
				break;
			default:
				break;
		}

		// change prefix which identifies options associated with a score
		manager.setScorePrefix(optionsManager.getOrder(), optionsManager.getDeckSize());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}


	private void setupEntryBox() {
		EditText playerNameEntryBox = findViewById(R.id.editTextPlayerNameEntryBox);
		String playerNamePref = optionsManager.getPlayerName();

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

		optionsManager.setPlayerName(enteredPlayerName);

	}

	private void setUpFlickrButton() {
		Button button = findViewById(R.id.btnCustomImageSet);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchPhotoGalleryActivity();
			}
		});
	}

	private void updateFlickrAmountText() {
		// turn on Flickr mode to see/change the number of flickr images.
		TextView currentFlickrPhotoCount = findViewById(R.id.txt_custom_image_number);
		// the user is only allowed to see/set Flickr images if the Flickr image set is selected
		if (radioButtonList.get(CUSTOM_IMAGE_SET).isChecked()) {

			String flickrPhotoCountText;
			// get the number of currently selected things; that will be displayed
			int currentFlickrPhotos = optionsManager.getFlickrImageSetSize();

			if (areThereEnoughFlickImages(currentFlickrPhotos)) {
				flickrPhotoCountText = String.format(getString(
						R.string.txt_flickr_photo_amount_ok), currentFlickrPhotos);
				currentFlickrPhotoCount.setTextColor(ContextCompat.getColor(
						OptionsActivity.this, R.color.blue));
				optionsManager.setImageSet(CUSTOM_IMAGE_SET);
			} else {
				flickrPhotoCountText = String.format(getString(
						R.string.txt_flickr_photo_amount_not_ok), currentFlickrPhotos,
						minimumReqImages);
				currentFlickrPhotoCount.setTextColor(ContextCompat.getColor(
						OptionsActivity.this, R.color.red));
				optionsManager.setImageSet(DEFAULT_IMAGE_SET);
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