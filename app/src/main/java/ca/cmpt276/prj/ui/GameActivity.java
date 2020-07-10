package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

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

    public void setupButtons() {
        refreshButtonImages();

        for (ImageButton imageButton : btnDisc) {
            imageButton.setOnClickListener(view -> {
                tappedListener(DISCARD_PILE_TAPPED, imageButton);
            });
        }

        for (ImageButton imageButton : btnDraw) {
            imageButton.setOnClickListener(view -> {
                tappedListener(DRAW_PILE_TAPPED, imageButton);
            });
        }
    }

    private void tappedListener(boolean pile, ImageButton button) {
        // debug
        Log.d(TAG, "button: " + button);
        Log.d(TAG, "button tag: (tappedListener) " + button.getTag());
        if (gameInstance.tappedUpdateState(pile, (CardImage) button.getTag())) {
            if (gameInstance.isGameOver()) {
                finishGame();
                return;
            } else {
                refreshButtonImages();
            }
        }
    }

    // TODO: ended game dialog or something
    private void finishGame() {

        Log.d(TAG, "finishGame: ");
    }

    private void initGame() {
        scoreManager = ScoreManager.getInstance();
        // TODO: we need options to be working to set the image set dynamically here
        gameInstance = new Game(NUM_IMAGES, LANDSCAPE_SET);
    }
}
