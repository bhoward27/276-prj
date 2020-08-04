package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.OptionsManager;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * Activity for different types of pictures and setting the player name.
 */
		public class OptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
			int imageSetPref;
			int minimumReqImages;
			OptionsManager optionsManager;
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

		int numImagesPerCard = optionsManager.getOrder() + 1;
		minimumReqImages = numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;

		createRadioButton();
		setupEntryBox();
		createOrderSpinner();
		createDeckSizeSpinner();
		setupCheckBox();
		setUpFlickrButton();
		updateFlickrAmountText();
		setUpExportCardsButton();
	}



	private void setUpExportCardsButton(){
		Button exportPhotos = findViewById(R.id.btnGenerateCardPhotos);

		exportPhotos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exportCards(v);
			}
		});
	}

	// General code for exporting files to a folder adapted from Coding In Flow
	// @ https://www.youtube.com/watch?v=EcfUkjlL9RI&t=182s
	private void exportCards(View v){

		//getCacheDir() as first parameter?

		// code for creating new folder in internal storage from Android Geek
		//---
		//RunTimeException is thrown when tested... why?
		// @ https://stackoverflow.com/a/54528830
//		File cardPhotoStorage = new File(Environment.getExternalStorageDirectory(), "FindDaMatchCards");
//
//		//If the folder doesn't exist, make it.
//		if(!cardPhotoStorage.exists()) {
//			if (!cardPhotoStorage.mkdirs()) {
//				throw new RuntimeException("ERROR: COULD NOT MAKE DIRECTORY!");
//			}
//		}
//		//Testing
//		Log.e("check_path", "" + cardPhotoStorage.getAbsolutePath());
		//---
		//String fileName = mItems.get(itemPosition).getId() + JPG_EXTENSION;

		// In order to write to storage, permissions defined in AndroidManifest
		// need to be asked for; code for doing this is adapted from Meta Snarf and Atif Mahmood
		// @ https://stackoverflow.com/a/33162451
		while(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
			Log.v("Permission status:","Permission NOT granted. Trying to alleviate...");
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
		}

		Log.v("Permission status:","Permission is granted");

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		//Using
		//In doing this, a copy accessible in the sdcard folder is created.
		//File cardPhotoStorageDir = new File (Environment.getExternalStorageDirectory(), "FindDaMatchPhotos");
		//File cardPhotoStorageDir = new File(getExternalFilesDir(null), "FindDaMatch3");

		// Adapted form Meet @ https://stackoverflow.com/a/59966753 (General File Declaration)
		// Prodev @ https://stackoverflow.com/a/37496736
		// Use of getFilesDir() suggested by raddevus, from
		// @ https://stackoverflow.com/a/29404440

		// File cardPhotoStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "testthing");
		// File cardPhotoStorageDir = new File("/storage", "FindDaMatch");
		File cardPhotoStorageDir = new File(getFilesDir(), "Exported Photos");

		Log.d("App:", "See your files in " + cardPhotoStorageDir.getPath());

		String exportedDeckFolder = cardPhotoStorageDir.getPath();

		if (!cardPhotoStorageDir.exists()) {
			Log.d("App: ", "Hey, the directory doesn't exist! Let's try making it...");
			if (!cardPhotoStorageDir.mkdir()) {
				Log.d("App", "failed to create directory!");
			}else{
				Log.d("App", "Wow, the directory is NOW created atL" + cardPhotoStorageDir.getAbsolutePath());
			}
		}else{
			Log.d("App", "Wow, the directory was already created atL" + cardPhotoStorageDir.getAbsolutePath());
		}

		Toast.makeText(getApplicationContext(), getString(
				R.string.tst_show_exported_card_photos_directory) + cardPhotoStorageDir,
				Toast.LENGTH_LONG).show();

		// creating text file to store files in directory of app adapted from Coding in Flow
		// @
//		String text = "OKAY!";
//		FileOutputStream fos = null;
//		String fileName = "Example" + JPG_EXTENSION;
//		try {
//			fos = openFileOutput(text, MODE_PRIVATE);
//			fos.write(text.getBytes());
//			Toast.makeText(this, "Successfully saved! Find your photos at: " + getFilesDir() + "/" + fileName, Toast.LENGTH_LONG).show();
//		} catch (FileNotFoundException e){
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private void initOptionSet() {
		optionsManager = OptionsManager.getInstance();
		imageSetPref = optionsManager.getImageSet();
		playerNamePlaceholder = getString(R.string.txt_player_name_placeholder);
		manager = ScoreManager.getInstance();
	}

	//https://stackoverflow.com/a/37496736
	private boolean areThereEnoughFlickImages(int currentFlickrPhotos) {
		// (Total number of cards is images^2 - images + 1) ==> number of total images
		int numImagesPerCard = optionsManager.getOrder() + 1;
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
					optionsManager.setImageSet(indexOfButton);
					updateFlickrAmountText();
				});

			} else {
				// for flickr radio button

				button.setOnClickListener(v -> {
					// don't allow the user to play the game with not enough images
					if (areThereEnoughFlickImages(optionsManager.getFlickrImageSetSize())) {
						optionsManager.setImageSet(indexOfButton);
					} else {
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

	private void setupCheckBox() {
		CheckBox chck = findViewById(R.id.chckWordMode);
		if (optionsManager.isWordMode()) {
			chck.setChecked(true);
		}
		if (optionsManager.getImageSet() == FLICKR_IMAGE_SET) {
			optionsManager.setWordMode(false);
			chck.setChecked(false);
			chck.setEnabled(false);
		}
		chck.setOnCheckedChangeListener((buttonView, isChecked) -> {
			// don't let word mode be used if flickr is the image set
			if (optionsManager.getImageSet() != FLICKR_IMAGE_SET) {
				optionsManager.setWordMode(isChecked);
			} else {
				optionsManager.setWordMode(false);
				buttonView.setEnabled(false);
				buttonView.setChecked(false);
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


	private void createDeckSizeSpinner() {
		sendPileSizesToOptionSet();
		updateDeckSizeSpinner();
	}

	// send resource of draw pile options as an ArrayList to options to store.
	private void sendPileSizesToOptionSet() {
		ArrayList<String> allDrawPileSizes = new ArrayList<String>
				(Arrays.asList(getResources().getStringArray(R.array.str_draw_pile_sizes)));
		int maxDeckSize = optionsManager.getMaxDeckSize();
		String allOption = getString(R.string.all_option, maxDeckSize);
		// because options has no access to resources, use the UI class to get the resource and
		// pass it as a string
		//optionsManager.setValidDrawPileSizes(allDrawPileSizes, allOption);
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
				createDeckSizeSpinner();// spinner for pile sizes might now change possible choices
				updateFlickrAmountText();

				if (!areThereEnoughFlickImages(optionsManager.getFlickrImageSetSize())
						&& optionsManager.getImageSet() == FLICKR_IMAGE_SET) {
					optionsManager.setImageSet(DEFAULT_IMAGE_SET);
					Toast.makeText(getApplicationContext(), getString(
							R.string.txt_attempted_leave_with_flickr_photo_amount_not_ok),
							Toast.LENGTH_LONG).show();
				} else if (areThereEnoughFlickImages(optionsManager.getFlickrImageSetSize())
						&& radioButtonList.get(FLICKR_IMAGE_SET).isChecked()) {
					optionsManager.setImageSet(FLICKR_IMAGE_SET);
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
			int currentFlickrPhotos = optionsManager.getFlickrImageSetSize();

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
