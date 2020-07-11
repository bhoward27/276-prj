package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.CardImage;
import ca.cmpt276.prj.model.Game;
import ca.cmpt276.prj.model.GenRand;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.DISCARD_PILE;
import static ca.cmpt276.prj.model.Constants.DRAW_PILE;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES;

public class GameActivity extends AppCompatActivity {
    public static final String TAG = "%%%GAMEACTIVITY";
    List<ImageButton> discPileButtons;
    List<ImageButton> drawPileButtons;
    List<ImageButton> allButtons;
    List<Integer> rndLeftMargin;
    List<Integer> rndTopMargin;
    int randCount;
    ScoreManager scoreManager;
    Game gameInstance;
    String resourcePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
        // TODO: we need options to be working to set the image set dynamically here
        gameInstance = new Game(NUM_IMAGES, LANDSCAPE_SET);

        // we have to wait until the cardview finishes loading before getting the positions for the
        // images
        CardView cardView = findViewById(R.id.crdDiscPile);
        cardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
        Chronometer scoreTimer = findViewById(R.id.chrnTimerForScoring);
        scoreTimer.setBase(SystemClock.elapsedRealtime());
        scoreTimer.start();
    }

    private void getButtons() {
        discPileButtons.add(findViewById(R.id.btnDiscIm1));
        discPileButtons.add(findViewById(R.id.btnDiscIm2));
        discPileButtons.add(findViewById(R.id.btnDiscIm3));

        drawPileButtons.add(findViewById(R.id.btnDrawIm1));
        drawPileButtons.add(findViewById(R.id.btnDrawIm2));
        drawPileButtons.add(findViewById(R.id.btnDrawIm3));

        for (ImageButton button : discPileButtons) {
            button.setTag(R.string.tag_btn_key, DISCARD_PILE);
        }

        for (ImageButton button : drawPileButtons) {
            button.setTag(R.string.tag_btn_key, DRAW_PILE);
        }

        allButtons.addAll(discPileButtons);
        allButtons.addAll(drawPileButtons);
    }

    private void refreshButtons() {
        // TODO: need options, same as below, this is a hack
        resourcePrefix = gameInstance.getDeck().getCurrentImageSet() == 1 ? "landscape_" : "predator_";

        String resourceName;
        int resourceID;
        List<CardImage> discPileImages = gameInstance.getDiscardPileImages();
        List<CardImage> drawPileImages = gameInstance.getDrawPileImages();

        CardImage image;
        for (ImageButton button : allButtons) {
            int index = allButtons.indexOf(button);
            boolean pile = (boolean) button.getTag(R.string.tag_btn_key);
            if (pile == DISCARD_PILE) {
                image = discPileImages.get(index);
            } else {
                image = drawPileImages.get(index - gameInstance.getImagesPerCard());
            }
            resourceName = resourcePrefix + image.name().toLowerCase();
            resourceID = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            button.setImageResource(resourceID);
            button.setTag(image);

            RelativeLayout.LayoutParams buttonLayoutParams = (RelativeLayout.LayoutParams) button.getLayoutParams();
            buttonLayoutParams.leftMargin = rndLeftMargin.get(randCount + index);
            buttonLayoutParams.topMargin = rndTopMargin.get(randCount + index);

            button.setLayoutParams(buttonLayoutParams);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupButtons() {
        // get global access to button ids
        getButtons();
        // this function creates the array of button positions which will be used
        // for each image/card
        setupButtonPositions();
        // this function adds images and tags to the buttons
        refreshButtons();

        for (ImageButton button : allButtons) {
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

    private void updateRemainingCardsText() {
        TextView txtRemaining = findViewById(R.id.txtCardsRemaining);
        txtRemaining.setText(getString(R.string.txt_cards_remaining) + " " + gameInstance.getRemainingCards());
    }

    private void setupButtonPositions() {
        CardView cardView = findViewById(R.id.crdDiscPile);
        int cardWidth = cardView.getWidth();
        int cardHeight = cardView.getHeight();

        int imageButtonWidth = Math.round(getResources().getDimension(R.dimen.button_width));
        int imageButtonHeight = Math.round(getResources().getDimension(R.dimen.button_height));

        int widthMax = cardWidth - imageButtonWidth;
        int heightMax = cardHeight - imageButtonHeight;

        int totalCards = gameInstance.getDeck().getTotalNumCards();

        GenRand gen = new GenRand(imageButtonWidth, imageButtonHeight, widthMax, heightMax, gameInstance.getImagesPerCard());
        for (int card = 0; card < totalCards; card++) {
            gen.generate();
            rndLeftMargin.addAll(gen.getxList());
            rndTopMargin.addAll(gen.getyList());
            gen.clear();
        }
    }

    private void tapUpdateGameState(ImageButton pressedButton) {
        boolean pile = (boolean) pressedButton.getTag(R.string.tag_btn_key);
        // if there was a match
        if (gameInstance.tappedUpdateState(pile, (CardImage) pressedButton.getTag())) {
            if (!gameInstance.isGameOver()) {
                // then change the images and remove all overlays to signify no card being selected

                // move index of random positions for card images
                randCount += gameInstance.getImagesPerCard();

                updateRemainingCardsText();
                updateShadowsAndMargins();
                refreshButtons();
                resetOverlay();
            } else {
                finishGame();
            }
        }
    }

    // TODO: make cards look like they are stacked on top of each other
    private void updateShadowsAndMargins() {

    }

    private void resetOverlay(ImageButton pressedButton) {
        // remove the overlays for all other buttons in the same card
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
        // remove the overlays for all buttons
        for (ImageButton button : allButtons) {
            int pad = Math.round(getResources().getDimension(R.dimen.button_padding));
            button.setPadding(pad, pad, pad, pad);
            button.setActivated(false);
        }
    }

    // TODO: ended game dialog or something
    private void finishGame() {
        Chronometer scoreTimer = findViewById(R.id.chrnTimerForScoring);
        int time = (int) (SystemClock.elapsedRealtime() - scoreTimer.getBase())/1000;
        // TODO: name from options
        scoreManager.addHighScore("NAME FROM OPTIONS", time);

        congratulationsDialog();
    }

    private void congratulationsDialog() {
        // adapted from Miguel @ https://stackoverflow.com/a/18898412
        ImageView congratsImage = new ImageView(this);
        // TODO: permanent image
        congratsImage.setImageResource(R.drawable.predator_spider);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        // TODO: permanent strings
                        // setMessage(getString(R.string.disp_congratulations)).
                        setMessage("Good job, you won the game!").
                        setPositiveButton("hooray!", (dialog, which) -> {
                            this.finish();
                            dialog.dismiss();
                        }).
                        setView(congratsImage);

        Dialog alert = builder.create();
        alert.show();
        // don't let user touch outside dialog box after game finished
        alert.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
