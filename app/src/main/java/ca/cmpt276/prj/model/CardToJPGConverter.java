package ca.cmpt276.prj.model;

/*  May want to rename to CardToJPGExporter (or simply CardExporter) depending on whether it
    actually handles the saving itself or if it merely converts to jpg and then hands the JPG off to
    somewhere else to be saved.
    Or maybe there should be a  third class, CardExporter, and it basically interacts with
    the LocalFiles class (should probably be renamed to something singular) and the card converter.

    Discuss with Michael.
 */

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class CardToJPGConverter {
    private OptionsManager options; //  do I need options?
    private Game game; //   Not sure if this should be member or just a local variable in a function.
    private GenRand rand; //    Not sure if should be member.
    private List<Card> cards; //    not sure if should be a member.
    private List<Bitmap> bitmaps;
    private static final double HEIGHT_IN_INCHES = 3.5;
    private static final double WIDTH_IN_INCHES = 2.5;
    private static final int PPI = 200;
    private static final int HEIGHT_IN_PX = (int) (HEIGHT_IN_INCHES * PPI);
    private static final int WIDTH_IN_PX = (int) (WIDTH_IN_INCHES * PPI);

    /*
    Not sure if this is even relevant to what I'm doing. Maybe what I need is in GenRand already.
    Stuff from William's code:

    // START GETTING CARDVIEW WIDTH AND HEIGHT
		int cardViewMarginSize = globalResources.getDimensionPixelSize(R.dimen.cardview_margins); // 10 dp
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int heightInPx = displayMetrics.heightPixels;
		int widthInPx = displayMetrics.widthPixels;

		TypedValue tv = new TypedValue();

		// remove the action bar size from height: https://stackoverflow.com/a/13216807
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			heightInPx -= TypedValue.complexToDimensionPixelSize(tv.data, globalResources.getDisplayMetrics());
		}

		// remove status bar from height: https://gist.github.com/hamakn/8939eb68a920a6d7a498
		int resourceId = globalResources.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			heightInPx -= globalResources.getDimensionPixelSize(resourceId);
		}

		int cardWidthInPx = widthInPx - cardViewMarginSize;
		// percentage of height which is the cardview, and remove the margins
		globalResources.getValue(R.fraction.disc_guideline_pct, tv, true);
		int cardHeightInPx = (int) Math.round(heightInPx * tv.getFloat() - cardViewMarginSize);
		// END GETTING CARDVIEW WIDTH AND HEIGHT
     */

    //  Should the above commented out code be made into separate methods
    //  that can be used by the card converter? Perhaps method could be put
    //  in Game or Deck and then GameActivity and CardConverter uses it however necessary?
    //  Talk to William about this if need be.

    /*
    Card bitmap should be the aspect ratio of an actual card in real life.
    (can have it in millimetres to use ints instaed)
    A playing card is approximately 6.4 x 8.9 cm. So aspect ratio = 6.4/8.9 ~ 0.719 ~ 7:10 = 7/10
    What resolution should it be then?
     */

    CardToJPGConverter(Context context) {
        options = options.getInstance();
        game = new Game();
        //rand = new GenRand(context, maxX, maxY);
        setupCards();
        createBitmaps();
    }

//    private Bitmap toBitmap(Card c) {
//
//    }

    private void createBitmaps() {
        //  would be neat to have the last image in bitmaps be an image for the back of a card.
        bitmaps = new ArrayList<>();
        for (Card c : cards) {
            //bitmaps.add(toBitmap(c));
        }
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    private void setupCards() {
        cards = game.getDeck().getAllCards();
        for (Card c : cards) {
            rand.gen(c);
        }
    }
}
