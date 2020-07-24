package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.Card;
import ca.cmpt276.prj.model.Game;
import ca.cmpt276.prj.model.GenRand;
import ca.cmpt276.prj.model.OptionSet;
import ca.cmpt276.prj.model.Score;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.BUTTON_SPACING_PADDING;
import static ca.cmpt276.prj.model.Constants.DISCARD_PILE;
import static ca.cmpt276.prj.model.Constants.DRAW_PILE;
import static ca.cmpt276.prj.model.Constants.IMAGE_FOLDER_NAME;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

/**
 * Class for displaying the game to the player, including game over messages.
 */
public class GameActivity extends AppCompatActivity {
    List<ImageButton> discPileButtons;
    List<ImageButton> drawPileButtons;
    List<ImageButton> allButtons;
    List<Integer> rndLeftMargin;
    List<Integer> rndTopMargin;
    int randCount;
    int imageSet;
    String imageSetPrefix;
    ScoreManager scoreManager;
    OptionSet options;
    Game gameInstance;
    String resourcePrefix;
    Chronometer scoreTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initGame();
    }

    private void initGame() {
        discPileButtons = new ArrayList<>();
        drawPileButtons = new ArrayList<>();
        allButtons = new ArrayList<>();
        rndLeftMargin = new ArrayList<>();
        rndTopMargin = new ArrayList<>();
        randCount = 0;

        scoreManager = ScoreManager.getInstance();
        options = OptionSet.getInstance();
        imageSet = options.getImageSet();
        imageSetPrefix = options.getImageSetPrefix();

        // temporarily set order here before options is done
        options.setOrder(2);
        gameInstance = new Game(options.getOrder());

        // We have to wait until the cardview loads before getting the positions for the images
        CardView cardView = findViewById(R.id.crdDiscPile);
        cardView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                updateRemainingCardsText();
                setupButtons();
                setupTimer();
            }
        });
    }

    private void setupTimer() {
        scoreTimer = findViewById(R.id.chrnTimerForScoring);
        scoreTimer.setBase(SystemClock.elapsedRealtime());
        scoreTimer.start();
    }

    private void getButtons() {
        RelativeLayout discCard = findViewById(R.id.lytDisc);
        RelativeLayout drawCard = findViewById(R.id.lytDraw);

        RelativeLayout.LayoutParams discBtnParams = (RelativeLayout.LayoutParams)
                findViewById(R.id.btnDiscIm1).getLayoutParams();
        RelativeLayout.LayoutParams drawBtnParams = (RelativeLayout.LayoutParams)
                findViewById(R.id.btnDrawIm1).getLayoutParams();

        for (int i = 0; i < options.getOrder()+1; i++) {
            ImageButton button = new ImageButton(this);
            button.setLayoutParams(discBtnParams);
            button.setTag(R.string.tag_btn_key, DISCARD_PILE);
            discCard.addView(button);
            discPileButtons.add(button);

            button = new ImageButton(this);
            button.setLayoutParams(drawBtnParams);
            button.setTag(R.string.tag_btn_key, DRAW_PILE);
            drawCard.addView(button);
            drawPileButtons.add(button);
        }

        discCard.removeView(findViewById(R.id.btnDiscIm1));
        drawCard.removeView(findViewById(R.id.btnDrawIm1));

        allButtons.addAll(discPileButtons);
        allButtons.addAll(drawPileButtons);
    }

    private void refreshButtons() {
        resourcePrefix = imageSetPrefix + RESOURCE_DIVIDER;

        List<Integer> discPileImages = gameInstance.getDiscardPileImages();
        List<Integer> drawPileImages = gameInstance.getDrawPileImages();

        for (ImageButton button : allButtons) {
            // this index is used for accessing the random number for this image out of all total images
            // and also for getting the image for the button from the gameInstance piles
            int index = allButtons.indexOf(button);
            boolean pile = (boolean) button.getTag(R.string.tag_btn_key);

            List<Integer> currPileImages = (pile == DISCARD_PILE) ? discPileImages : drawPileImages;
            int imageNum = currPileImages.get(index % gameInstance.getOrder());

            // creates a string such as a_0 if the imageSet is 0 and imageNum is 0
            String resourceName = resourcePrefix + imageNum;
            int resourceID = getResources().getIdentifier(resourceName, IMAGE_FOLDER_NAME,
                                                            getPackageName());
            button.setImageResource(resourceID);
            button.setTag(imageNum);

            // set the random position
            RelativeLayout.LayoutParams buttonLayoutParams =
                    (RelativeLayout.LayoutParams) button.getLayoutParams();
            buttonLayoutParams.leftMargin = rndLeftMargin.get(randCount + index);
            buttonLayoutParams.topMargin = rndTopMargin.get(randCount + index);

            button.setLayoutParams(buttonLayoutParams);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupButtons() {
        // Get global access to button ids
        getButtons();
        // This function creates the array of button positions which will be used
        // for each image/card
        setupButtonPositions();
        // This function adds images and tags to the buttons
        refreshButtons();

        // Dynamic ImageButton sizes
        double buttonSpacingX = findViewById(R.id.crdDiscPile).getWidth() /
                ((gameInstance.getOrder()) * BUTTON_SPACING_PADDING);
        //double buttonSpacingY = (1 / IMAGES_RATIOS) * buttonSpacingX;
        double buttonSpacingY = (1 / 2) * buttonSpacingX;

        int buttonHeight = (int) Math.round(buttonSpacingY);
        // 2.01:1 is the ratio of the current images
        // TODO: change to not hardcode ratio
        int buttonWidth = (int) Math.round(buttonSpacingX);

        for (ImageButton button : allButtons) {
            RelativeLayout.LayoutParams buttonLayoutParams =
                    (RelativeLayout.LayoutParams) button.getLayoutParams();

            buttonLayoutParams.width = buttonWidth;
            buttonLayoutParams.height = buttonHeight;

            button.setLayoutParams(buttonLayoutParams);

            button.setOnTouchListener((ignored, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    int pad = Math.round(getResources().getDimension(R.dimen.button_selected_padding));
                    button.setPadding(pad, pad, pad, pad);
                    button.setActivated(true);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    resetOverlay(button);
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

    private void setupButtonPositions() {
        CardView cardView = findViewById(R.id.crdDiscPile);
        int cardWidth = cardView.getWidth();
        int cardHeight = cardView.getHeight();

        /* TODO: this should be changed if not all the buttons are the same size, the random generator
            will also need to be changed to accommodate for this fact
         */
        int imageButtonWidth = allButtons.get(0).getWidth();
        int imageButtonHeight = allButtons.get(0).getHeight();

        int widthMax = cardWidth - imageButtonWidth;
        int heightMax = cardHeight - imageButtonHeight;

        int totalCards = gameInstance.getDeck().getTotalNumCards();

        for (int card = 0; card < totalCards; card++) {
            GenRand gen = new GenRand(imageButtonWidth, imageButtonHeight, widthMax, heightMax,
                                        gameInstance.getOrder());
            rndLeftMargin.addAll(gen.getxList());
            rndTopMargin.addAll(gen.getyList());
        }
    }

    private void tapUpdateGameState(ImageButton pressedButton) {
        boolean pile = (boolean) pressedButton.getTag(R.string.tag_btn_key);
        // If there was a match
        if (gameInstance.tappedUpdateState(pile, (int) pressedButton.getTag())) {
            if (!gameInstance.isGameOver()) {
                // Then change the images and remove all overlays to signify no card being selected

                // Move index of random positions for card images
                randCount += gameInstance.getOrder();

                updateRemainingCardsText();
                updateShadowsAndMargins();
                refreshButtons();
                resetOverlay();
            } else {
                finishGame();
            }
        }
    }

    // Code for setting margins adapted from Muhammad Nabeel Arif and Salam El-Banna
    // @ https://stackoverflow.com/a/9563438
    private float convertPixelsToDp(float px) {
        return px / ((float) getResources().getDisplayMetrics().densityDpi
                / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void updateShadowsAndMargins() {
        ConstraintLayout.LayoutParams discCardView =
                (ConstraintLayout.LayoutParams) findViewById(R.id.crdDiscPile).getLayoutParams();
        ConstraintLayout.LayoutParams drawCardView =
                (ConstraintLayout.LayoutParams) findViewById(R.id.crdDrawPile).getLayoutParams();

        float shiftAmt = convertPixelsToDp(getResources().getDimension(R.dimen.cardview_margins))
                            / gameInstance.getDeck().getTotalNumCards() + 1;

        discCardView.leftMargin -= shiftAmt;
        discCardView.topMargin -= shiftAmt;
        discCardView.rightMargin += shiftAmt;
        discCardView.bottomMargin += shiftAmt;

        drawCardView.leftMargin += shiftAmt;
        drawCardView.topMargin += shiftAmt;
        drawCardView.rightMargin -= shiftAmt;
        drawCardView.bottomMargin -= shiftAmt;
    }

    private void resetOverlay(ImageButton pressedButton) {
        // Remove the overlays for all other buttons in the same card
        boolean pile = (boolean) pressedButton.getTag(R.string.tag_btn_key);
        for (ImageButton button : (pile == DISCARD_PILE ? discPileButtons : drawPileButtons)) {
            if (button != pressedButton) {
                int pad = Math.round(getResources().getDimension(R.dimen.button_padding));
                button.setPadding(pad, pad, pad, pad);
                button.setActivated(false);
            }
        }
    }

    private void resetOverlay() {
        // Remove the overlays for all buttons
        for (ImageButton button : allButtons) {
            int pad = Math.round(getResources().getDimension(R.dimen.button_padding));
            button.setPadding(pad, pad, pad, pad);
            button.setActivated(false);
        }
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
        int winImageID = getResources().getIdentifier(imageSetPrefix + RESOURCE_DIVIDER +
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
        TextView dialogMessages = (TextView) alert.findViewById(android.R.id.message);
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
}
