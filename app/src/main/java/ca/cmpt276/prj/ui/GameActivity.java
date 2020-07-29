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
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import ca.cmpt276.prj.model.FlickrFoldrImageRenamr;
import ca.cmpt276.prj.model.Game;
import ca.cmpt276.prj.model.GenRand;
import ca.cmpt276.prj.model.ImageNameMatrix;
import ca.cmpt276.prj.model.OptionSet;
import ca.cmpt276.prj.model.Score;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.DISCARD_PILE;
import static ca.cmpt276.prj.model.Constants.DRAW_PILE;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.IMAGE_FOLDER_NAME;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

/**
 * Class for displaying the game to the player, including game over messages.
 */
public class GameActivity extends AppCompatActivity {
	private static final String TAG = "GameActivity";

	List<PicButton> discPileButtons;
	List<PicButton> drawPileButtons;
	List<PicButton> allButtons;
	int buttonCount;
	int imageSet;
	int numImagesPerCard;

	ImageNameMatrix imageNames;
	String imageSetPrefix;
	ScoreManager scoreManager;
	OptionSet options;
	Game gameInstance;
	String resourcePrefix;
	Chronometer scoreTimer;
	Resources globalResources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		initGame();
	}

	private void initGame() {
		FlickrFoldrImageRenamr.makeFileNamesConsistent(this);
		options = OptionSet.getInstance();

		discPileButtons = new ArrayList<>();
		drawPileButtons = new ArrayList<>();
		allButtons = new ArrayList<>();
		buttonCount = 0;

		scoreManager = ScoreManager.getInstance();
		imageSet = options.getImageSet();
		imageSetPrefix = options.getImageSetPrefix();
		imageNames = ImageNameMatrix.getInstance();
		resourcePrefix = imageSetPrefix + RESOURCE_DIVIDER;
		globalResources = getResources();
		numImagesPerCard = options.getOrder() + 1;

		if (options.getImageSet() == FLICKR_IMAGE_SET && options.isWordMode()) {
			throw new Error("Flickr image set does not support word mode.");
		}

		gameInstance = new Game();

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
			RelativeLayout.LayoutParams buttonLayoutParams =
					(RelativeLayout.LayoutParams) button.getLayoutParams();
			buttonLayoutParams.width = (int) Math.round(currCard.imageWidths.get(modIndex));
			buttonLayoutParams.height = (int) Math.round(currCard.imageHeights.get(modIndex));

			buttonLayoutParams.leftMargin = currCard.leftMargins.get(modIndex);
			buttonLayoutParams.topMargin = currCard.topMargins.get(modIndex);

			// set the image or word
			if (!currCard.isWord.get(modIndex)) {
				// creates a string such as a_0 if the imageSet is 0 and imageNum is 0
				button.setText("");
				if (imageSet != FLICKR_IMAGE_SET) {
					String resourceName = resourcePrefix + imageNum;
					int resourceID = globalResources.getIdentifier(resourceName, IMAGE_FOLDER_NAME,
							getPackageName());
					button.setBackgroundResource(resourceID);
				} else {
					String pathName = this.getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE) + "/" + "c_" + imageNum + JPG_EXTENSION;
                    File imageFile = new File(pathName);
					Picasso.get().load(imageFile).into(button);
				}
			} else {
				button.setBackground((Drawable) button.getTag(R.string.tag_btn_bg));
				button.setText(imageNames.getName(imageSet, imageNum));
			}
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
		int cardRatio = cardWidth / cardHeight;
		// END GETTING CARDVIEW WIDTH AND HEIGHT

		List<Card> allCards = gameInstance.getDeck().getAllCards();
		for (Card c : allCards) {
			List<Integer> imagesMap = c.getImagesMap();
			for (int i : imagesMap) {
				double w;
				double h;
				// regular, non-flickr image setup (can be dynamic width/height)
				if (!c.isWord.get(imagesMap.indexOf(i)) && imageSet != FLICKR_IMAGE_SET) {
					Drawable image;
					String resourceName = resourcePrefix + i;
					int resourceID = globalResources.getIdentifier(resourceName, IMAGE_FOLDER_NAME,
							getPackageName());
					image = getDrawable(resourceID);
					double ratio = (double) image.getIntrinsicWidth() / image.getIntrinsicHeight();
					if (ratio > cardRatio) { // if the image is wider than the card's ratio
						h = (double) cardHeight / Math.log(numImagesPerCard * 20);
						w = ratio * h;
					} else {
						w = (double) cardWidth / Math.log(numImagesPerCard * 20);
						h = (1.0 / ratio) * w;
					}
				} else if (imageSet != FLICKR_IMAGE_SET) {
					// make word buttons slightly bigger
					w = cardWidth / Math.log(numImagesPerCard * 10);
					h = (double) w / 1.5;
				} else {
					// make flickr buttons square
					w = cardWidth / Math.log(numImagesPerCard * 10);
					h = w;

				}

				c.imageWidths.add(w);
				c.imageHeights.add(h);
			}
		}

		// generate the random positions for the images on this card
		GenRand rand = new GenRand();
		for (Card c : allCards) {
			rand.gen(c.imageWidths, c.imageHeights, cardWidth, cardHeight);
			c.leftMargins.addAll(rand.getXMargins());
			c.topMargins.addAll(rand.getYMargins());
		}
	}

	private void tapUpdateGameState(PicButton pressedButton) {
		// If there was a match
		if (gameInstance.tappedUpdateState((int) pressedButton.getTag())) {
			if (!gameInstance.isGameOver()) {
				// Then change the images and remove all overlays to signify no card being selected

				// Move index of random positions for card images
				buttonCount += numImagesPerCard;

				updateRemainingCardsText();
				updateShadowsAndMargins();
				refreshButtons();
			} else {
				finishGame();
			}
		}
	}

	private void updateShadowsAndMargins() {
		ConstraintLayout.LayoutParams discCardView =
				(ConstraintLayout.LayoutParams) findViewById(R.id.crdDiscPile).getLayoutParams();
		ConstraintLayout.LayoutParams drawCardView =
				(ConstraintLayout.LayoutParams) findViewById(R.id.crdDrawPile).getLayoutParams();

		int shiftAmt = globalResources.getDimensionPixelSize(R.dimen.cardview_margins) / options.getDeckSize();

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
		int playerRank = scoreManager.addHighScore(options.getPlayerName(),
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
		if (playerRank != 0) {
			winMessage += getString(R.string.txt_player_place, playerRank);
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
}
