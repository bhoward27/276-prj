package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.CardImage;
import ca.cmpt276.prj.model.Game;
import ca.cmpt276.prj.model.ScoreManager;

import static ca.cmpt276.prj.model.Constants.DISCARD_PILE_TAPPED;
import static ca.cmpt276.prj.model.Constants.DRAW_PILE_TAPPED;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES;


public class GameActivity extends AppCompatActivity {
    public static final String TAG = "%%%GAMEACTIVITY";
    ImageButton[] btnDisc = new ImageButton[3];
    ImageButton[] btnDraw = new ImageButton[3];
    ScoreManager scoreManager;
    Game gameInstance;
    String resourcePrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initGame();
        getButtons();
        setupButtons();
        setupDecorations();
    }

    private void setupDecorations() {
        Chronometer scoreTimer = findViewById(R.id.chrnTimerForScoring);
        scoreTimer.setBase(SystemClock.elapsedRealtime());
        scoreTimer.start();
    }

    private void getButtons() {
        btnDisc[0] = findViewById(R.id.btnDiscIm1);
        btnDisc[1] = findViewById(R.id.btnDiscIm2);
        btnDisc[2] = findViewById(R.id.btnDiscIm3);

        btnDraw[0] = findViewById(R.id.btnDrawIm1);
        btnDraw[1] = findViewById(R.id.btnDrawIm2);
        btnDraw[2] = findViewById(R.id.btnDrawIm3);
    }

    private void refreshButtonImages() {
        // TODO: need options, same as below, this is a hack
        resourcePrefix = gameInstance.getDeck().getCurrentImageSet() == 1 ? "landscape_" : "predator_";

        String resourceName;
        int resourceID;
        int btnNumber = 0;
        for (CardImage discImage : gameInstance.getDiscardPileImages()) {
            resourceName = resourcePrefix + discImage.name().toLowerCase();
            resourceID = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            btnDisc[btnNumber].setImageResource(resourceID);
            btnDisc[btnNumber].setTag(discImage);

            // debug
            Log.d("%%%GAMEACTIVITY", "resourceName: " + resourceName);
            Log.d(TAG, "button tag: (refresh) " + (CardImage) btnDisc[btnNumber].getTag());
            btnNumber++;
        }

        btnNumber = 0;
        for (CardImage drawImage : gameInstance.getDrawPileImages()) {
            resourceName = resourcePrefix + drawImage.name().toLowerCase();
            resourceID = getResources().getIdentifier(resourceName, "drawable", getPackageName());
            btnDraw[btnNumber].setImageResource(resourceID);
            btnDraw[btnNumber].setTag(drawImage);

            // debug
            Log.d(TAG, "resourceName: " + resourceName);
            Log.d(TAG, "button tag: (refresh) " + (CardImage) btnDraw[btnNumber].getTag());
            btnNumber++;
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupButtons() {
        refreshButtonImages();

        for (ImageButton imageButton : btnDisc) {
            imageButton.setOnTouchListener((ignored, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    imageButton.setColorFilter(getColor(R.color.colorGreenFilter));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    resetOverlay(DISCARD_PILE_TAPPED, imageButton);
                    tapUpdateGameState(DISCARD_PILE_TAPPED, imageButton);
                }

                return false;
            });
        }

        for (ImageButton imageButton : btnDraw) {
            imageButton.setOnTouchListener((ignored, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    imageButton.setColorFilter(getColor(R.color.colorGreenFilter));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    resetOverlay(DRAW_PILE_TAPPED, imageButton);
                    tapUpdateGameState(DRAW_PILE_TAPPED, imageButton);
                }

                return false;
            });
        }
    }

    private void tapUpdateGameState(boolean pile, ImageButton pressedButton) {
        // debug
        Log.d(TAG, "button: " + pressedButton);
        Log.d(TAG, "button tag: (tappedListener) " + pressedButton.getTag());

        // if there was a match
        if (gameInstance.tappedUpdateState(pile, (CardImage) pressedButton.getTag())) {
            Log.d(TAG, "tapUpdateGameState: match");
            if (!gameInstance.isGameOver()) {
                // then change the images and remove the overlay to signify no card being selected
                refreshButtonImages();
                resetOverlay(DISCARD_PILE_TAPPED, null);
                resetOverlay(DRAW_PILE_TAPPED, null);
            } else {
                finishGame();
            }
        }
    }

    private void resetOverlay(boolean pile, ImageButton pressedButton) {
        // remove the overlays for all other buttons in the same card
        for (ImageButton imageButton : (pile == DISCARD_PILE_TAPPED ? btnDisc : btnDraw)) {
            if (imageButton != pressedButton) {
                imageButton.setColorFilter(0);
            }
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
                            dialog.dismiss();
                            this.finish();
                        }).
                        setView(congratsImage);

        Dialog alert = builder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(false); // don't let user touch outside dialog box after game finished
    }

    private void initGame() {
        scoreManager = ScoreManager.getInstance();
        // TODO: we need options to be working to set the image set dynamically here
        gameInstance = new Game(NUM_IMAGES, LANDSCAPE_SET);
    }

}
