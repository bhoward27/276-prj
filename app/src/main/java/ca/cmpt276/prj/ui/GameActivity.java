package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.Card;
import ca.cmpt276.prj.model.Game;
import ca.cmpt276.prj.model.GenRand;
import ca.cmpt276.prj.model.ImageNameMatrix;
import ca.cmpt276.prj.model.LocalFiles;
import ca.cmpt276.prj.model.OptionsManager;
import ca.cmpt276.prj.model.Score;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.*;

/**
 * Class for displaying the game to the player, including game over messages.
 */
public class GameActivity extends AppCompatActivity {
	private static final String TAG = "GameActivity";
	private SoundPool soundPool;
	private int startGameSound, winSound, winHighScoreSound, correctChoiceSound, incorrectChoiceSound;

	public enum GameSoundEffects{
		BEGIN_GAME, WIN, WIN_WITH_HIGH_SCORE, CORRECT_CHOICE, INCORRECT_CHOICE
	}

	List<PicButton> discPileButtons;
	List<PicButton> drawPileButtons;
	List<PicButton> allButtons;
	int buttonCount;
	int imageSet;
	int numImagesPerCard;

	ImageNameMatrix imageNames;
	String imageSetPrefix;
	ScoreManager scoreManager;
	OptionsManager optionsManager;
	Game gameInstance;
	String resourcePrefix;
	Chronometer scoreTimer;
	Resources globalResources;
	LocalFiles localFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


			// Code for playing songs via soundpool adapted from Coding In Flow
			// @ https://www.youtube.com/watch?v=fIWPSni7kUk&t=347s
			// This includes playSound(), onDestroy(), and use of AudioAttributes/SoundPool

			AudioAttributes audioAttributes = new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.build();

			soundPool = new SoundPool.Builder()
					.setMaxStreams(5)
							.setAudioAttributes(audioAttributes)
							.build();

		loadSounds();

		// without waiting for startGameSound to load, it will not play when the game starts.
		// therefore, setOnLoadCompleteListener is used to ensure that the game starts when
		// startGameSound is loaded and startGameSound plays.
		// code NOT from Coding In Flow; instead adapted from Jason and BT643
		// @https://stackoverflow.com/a/3908804
		soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				if(sampleId == startGameSound && status == 0) {
					initGame();
				}
			}
		});
	}

	private void loadSounds(){
		startGameSound = soundPool.load(this, R.raw.startgame, 1);
		winSound = soundPool.load(this, R.raw.win,1);
		winHighScoreSound = soundPool.load(this, R.raw.winhighscore,1);
		correctChoiceSound = soundPool.load(this, R.raw.correct,1);
		incorrectChoiceSound = soundPool.load(this, R.raw.incorrect,1);
	}

	private void initGame() {
		optionsManager = OptionsManager.getInstance();

		discPileButtons = new ArrayList<>();
		drawPileButtons = new ArrayList<>();
		allButtons = new ArrayList<>();
		buttonCount = 0;

		scoreManager = ScoreManager.getInstance();
		imageSet = optionsManager.getImageSet();
		imageSetPrefix = optionsManager.getImageSetPrefix();
		imageNames = ImageNameMatrix.getInstance();
		resourcePrefix = imageSetPrefix + RESOURCE_DIVIDER;
		globalResources = getResources();
		numImagesPerCard = optionsManager.getOrder() + 1;

		// TODO: get imageset directory from options
		localFiles = new LocalFiles(this, FLICKR_SAVED_DIR);

		if (optionsManager.getImageSet() == FLICKR_IMAGE_SET && optionsManager.isWordMode()) {
			throw new Error("Flickr image set does not support word mode.");
		}

		gameInstance = new Game();
		playSound(GameSoundEffects.BEGIN_GAME);
		updateRemainingCardsText();
		setupButtons();
		setupTimer();
	}

	private void setupTimer() {
		scoreTimer = findViewById(R.id.chrnTimerForScoring);
		scoreTimer.setBase(SystemClock.elapsedRealtime());
		scoreTimer.start();
	}

	private void setButtonParameters(PicButton button, boolean pile) {
		button.setVisibility(View.VISIBLE);
		button.setForegroundGravity(Gravity.CENTER);
		button.setTextSize(globalResources.getDimensionPixelSize(R.dimen.button_text_size));
		button.setAllCaps(false);
		button.setStateListAnimator(null);
		button.setBackground(null);

		button.setTag(R.string.tag_btn_bg, button.getBackground());
		button.setTag(R.string.tag_btn_key, pile);
	}

	private void getButtons() {
		RelativeLayout discCard = findViewById(R.id.lytDisc);
		RelativeLayout drawCard = findViewById(R.id.lytDraw);

		for (int i = 0; i < numImagesPerCard; i++) {
			PicButton button = new PicButton(this);
			setButtonParameters(button, DISCARD_PILE);
			discCard.addView(button);
			discPileButtons.add(button);

			button = new PicButton(this);
			setButtonParameters(button, DRAW_PILE);
			drawCard.addView(button);
			drawPileButtons.add(button);
		}

		allButtons.addAll(discPileButtons);
		allButtons.addAll(drawPileButtons);
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setupButtons() {
		// Get global access to button ids
		getButtons();

		generateRandomPositions();
		// This function adds images and tags to the buttons
		refreshButtons();

		for (PicButton button : drawPileButtons) {
			button.setOnTouchListener((ignored, motionEvent) -> {
				if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					tapUpdateGameState(button);
				}

				return false;
			});
		}
	}

	@SuppressLint("SetTextI18n")
	private void updateRemainingCardsText() {
		TextView txtRemaining = findViewById(R.id.txtCardsRemaining);
		if (gameInstance.getRemainingCards() < 4) {
			txtRemaining.setTextColor(ContextCompat.getColor(GameActivity.this, R.color.green));
		} else {
			txtRemaining.setTextColor(ContextCompat.getColor(GameActivity.this, R.color.black));
		}
		txtRemaining.setText(getString(R.string.txt_cards_remaining) + gameInstance.getRemainingCards());
	}

	private void refreshButtons() {
		List<Integer> discPileImages = gameInstance.getDiscardPileImages();
		List<Integer> drawPileImages = gameInstance.getDrawPileImages();

		Card currDiscardCard = gameInstance.getDeck().getTopDiscard();
		Card currDrawCard = gameInstance.getDeck().getTopDraw();

		for (PicButton button : allButtons) {
			// this index is used for accessing the random number for this image out of all total images
			// and also for getting the image for the button from the gameInstance piles
			int index = allButtons.indexOf(button);
			int modIndex = index % numImagesPerCard;
			boolean pile = (boolean) button.getTag(R.string.tag_btn_key);

			List<Integer> currPileImages;
			Card currCard;

			if (pile == DISCARD_PILE) {
				currPileImages = discPileImages;
				currCard = currDiscardCard;
			} else {
				currPileImages = drawPileImages;
				currCard = currDrawCard;
			}

			int imageNum = currPileImages.get(modIndex);

			button.setTag(imageNum);

			// set size & random position
			int currButtonWidth = (int) Math.round(currCard.imageWidths.get(modIndex));
			int currButtonHeight = (int) Math.round(currCard.imageHeights.get(modIndex));

			button.setRotation(0);

			// set the image or word
			if (!currCard.isWord.get(modIndex)) {
				// creates a string such as a_0 if the imageSet is 0 and imageNum is 0
				button.setText("");
				currButtonWidth *= currCard.randScales.get(modIndex);
				currButtonHeight *= currCard.randScales.get(modIndex);
				if (imageSet != FLICKR_IMAGE_SET) {
					String resourceName = resourcePrefix + imageNum;
					int resourceID = globalResources.getIdentifier(resourceName, IMAGE_FOLDER_NAME,
							getPackageName());
					Picasso.get()
							.load(resourceID)
							//.rotate(currCard.randRotations.get(modIndex).floatValue())
							.into(button);
				} else {
					Picasso.get()
							.load(localFiles.getFile(imageNum))
							//.rotate(currCard.randRotations.get(modIndex).floatValue())
							.into(button);

				}
			} else {
				button.setTextSize(globalResources.getDimension(R.dimen.button_text_size) *
						currCard.randScales.get(modIndex).floatValue());
				button.setBackground((Drawable) button.getTag(R.string.tag_btn_bg));
				button.setText(imageNames.getName(imageSet, imageNum));
				// rotating text isn't supported, button bg may overlap with buttons with images on them
				//button.setRotation(currCard.randRotations.get(modIndex).floatValue());
			}

			button.setRotation(currCard.randRotations.get(modIndex).floatValue());

			RelativeLayout.LayoutParams buttonLayoutParams =
					(RelativeLayout.LayoutParams) button.getLayoutParams();
			buttonLayoutParams.leftMargin = currCard.leftMargins.get(modIndex);
			buttonLayoutParams.topMargin = currCard.topMargins.get(modIndex);

			buttonLayoutParams.width = currButtonWidth;
			buttonLayoutParams.height = currButtonHeight;

			button.setLayoutParams(buttonLayoutParams);
		}
	}

	private void generateRandomPositions() {
		// START GETTING CARDVIEW WIDTH AND HEIGHT
		int cardViewMarginSize = globalResources.getDimensionPixelSize(R.dimen.cardview_margins);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;

		TypedValue tv = new TypedValue();

		// remove the action bar size from height: https://stackoverflow.com/a/13216807
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			height -= TypedValue.complexToDimensionPixelSize(tv.data, globalResources.getDisplayMetrics());
		}

		// remove status bar from height: https://gist.github.com/hamakn/8939eb68a920a6d7a498
		int resourceId = globalResources.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			height -= globalResources.getDimensionPixelSize(resourceId);
		}

		int cardWidth = width - cardViewMarginSize;
		// percentage of height which is the cardview, and remove the margins
		globalResources.getValue(R.fraction.disc_guideline_pct, tv, true);
		int cardHeight = (int) Math.round(height * tv.getFloat() - cardViewMarginSize);
		// END GETTING CARDVIEW WIDTH AND HEIGHT

		// generate the random positions for the images on each card
		GenRand rand = new GenRand(this, cardWidth, cardHeight);
		for (Card c : gameInstance.getDeck().getAllCards()) {
			rand.gen(c);
		}
	}

	private void tapUpdateGameState(PicButton pressedButton) {
		// If there was a match
		if (gameInstance.tappedUpdateState((int) pressedButton.getTag())) {
			if (!gameInstance.isGameOver()) {
				// Then change the images and remove all overlays to signify no card being selected

				// Move index of random positions for card images
				buttonCount += numImagesPerCard;
				playSound(GameSoundEffects.CORRECT_CHOICE);
				updateRemainingCardsText();
				updateShadowsAndMargins();
				refreshButtons();
			} else {
				finishGame();
			}
		} else {
			playSound(GameSoundEffects.INCORRECT_CHOICE);
		}
	}

	private void updateShadowsAndMargins() {
		ConstraintLayout.LayoutParams discCardView =
				(ConstraintLayout.LayoutParams) findViewById(R.id.crdDiscPile).getLayoutParams();
		ConstraintLayout.LayoutParams drawCardView =
				(ConstraintLayout.LayoutParams) findViewById(R.id.crdDrawPile).getLayoutParams();

		int shiftAmt = globalResources.getDimensionPixelSize(R.dimen.cardview_margins) / optionsManager.getDeckSize();

		discCardView.leftMargin -= shiftAmt;
		discCardView.topMargin -= shiftAmt;
		discCardView.rightMargin += shiftAmt;
		discCardView.bottomMargin += shiftAmt;

		drawCardView.leftMargin += shiftAmt;
		drawCardView.topMargin += shiftAmt;
		drawCardView.rightMargin -= shiftAmt;
		drawCardView.bottomMargin -= shiftAmt;
	}

	private void finishGame() {
		scoreTimer.stop();
		int time = (int) (SystemClock.elapsedRealtime() - scoreTimer.getBase()) / 1000;
		int playerRank = scoreManager.addHighScore(optionsManager.getPlayerName(),
				time);
		congratulationsDialog(time, playerRank);
	}

	private void congratulationsDialog(int time, int playerRank) {
		// Code adapted from Miguel @ https://stackoverflow.com/a/18898412
		ImageView congratsImage = new ImageView(this);
		int winImageID = globalResources.getIdentifier(imageSetPrefix + RESOURCE_DIVIDER +
				"end", IMAGE_FOLDER_NAME, getPackageName());
		congratsImage.setImageResource(winImageID);
		congratsImage.setAdjustViewBounds(true);
		congratsImage.setMaxHeight(400);
		String winMessage = getString(R.string.txt_win_message, Score.getFormattedTime(time));
		if (playerRank != 0) {// different win sound depending on if player got a high score
			winMessage += getString(R.string.txt_player_place, playerRank);
			playSound(GameSoundEffects.WIN_WITH_HIGH_SCORE);
		} else {
			playSound(GameSoundEffects.WIN);
		}

		String returnAfterWinMessage = getString(R.string.btn_return_after_win);
		AlertDialog.Builder builder =
				new AlertDialog.Builder(this).
						setMessage(winMessage).
						setPositiveButton(returnAfterWinMessage, (dialog, which) -> {
							this.finish();
							dialog.dismiss();
						}).
						setView(congratsImage);

		Dialog alert = builder.create();
		alert.show();

		// Changing font to casual adapted from mikeswright49 @ https://stackoverflow.com/a/13052057
		// With the suggestion to place it after alert.show() adapted from Cerlin
		// @ https://stackoverflow.com/a/43536704
		TextView dialogMessages = alert.findViewById(android.R.id.message);
		assert dialogMessages != null;
		dialogMessages.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
		dialogMessages.setTextSize(26);
		dialogMessages.setGravity(Gravity.CENTER);

		// Code adapted from DimitrisCBR @ https://stackoverflow.com/a/29912304
		Button btnReturnToMainMenu = alert.findViewById(android.R.id.button1);
		assert btnReturnToMainMenu != null;
		btnReturnToMainMenu.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

		// Don't let user touch outside dialog box after game finished
		alert.setCanceledOnTouchOutside(false);
		// Don't let back button exit back to game
		alert.setCancelable(false);
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	// Adapted from https://stackoverflow.com/a/29059132
	public class PicButton extends androidx.appcompat.widget.AppCompatButton implements Target {

		public PicButton(Context context) {
			super(context);
		}

		public PicButton(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public PicButton(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			setBackgroundDrawable(new BitmapDrawable(globalResources, bitmap));
		}

		@Override
		public void onBitmapFailed(Exception e, Drawable errorDrawable) {
			Log.e(TAG, "onBitmapFailed: ", e);
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {

		}
	}

	public void playSound(GameSoundEffects soundEffect){
		switch(soundEffect){
			case BEGIN_GAME:
				soundPool.play(startGameSound, 1, 1, 0, 0, 1);
				break;
			case CORRECT_CHOICE:
				soundPool.play(correctChoiceSound, 1, 1, 0, 0, 1);
				break;
			case INCORRECT_CHOICE:
				soundPool.play(incorrectChoiceSound, 1, 1, 0, 0, 1);
				break;
			case WIN:
				soundPool.play(winSound, 1, 1, 0, 0, 1);
				break;
			case WIN_WITH_HIGH_SCORE:
				soundPool.play(winHighScoreSound, 1, 1, 0, 0, 1);
				break;
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		soundPool.release();
		soundPool = null;
	}

}
