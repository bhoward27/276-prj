package ca.cmpt276.prj.model;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

public class CardToBitmapConverter {
    private OptionsManager options; //  do I need options?
    private Game game; //   Not sure if this should be member or just a local variable in a function.
    private GenRand rand; //    Not sure if should be member.
    private List<Card> cards; //    not sure if should be a member.
    private List<Bitmap> bitmaps; //    these bitmaps must be the final bitmaps of the entire card.

    //  names of the image files which can be passed to Michael's file manager.
    private List<String> fileNames;
    public static final String EXPORTED_CARD_PREFIX = "card" + RESOURCE_DIVIDER;


    //  Values of height and width would be reversed for a typical playing card; however,
    //  this would cause a lot of extra work for me because the in-game cards are in a landscape
    //  orientation. So I am keeping the cards at that orientation (i.e., the exported card
    //  will be wider than it is tall, like in the actual game).

    //  Dimensions of a card in pixels and inches:
    private static final double HEIGHT_IN_INCHES = 2.5;
    private static final double WIDTH_IN_INCHES = 3.5;
    private static final double MARGIN_IN_INCHES = 3.0 / 8;
    private static final int PPI = 200; //  pixels per inch
    private static final int HEIGHT_IN_PX = (int) (HEIGHT_IN_INCHES * PPI); //  px = pixels
    private static final int WIDTH_IN_PX = (int) (WIDTH_IN_INCHES * PPI);
    private static final int MARGIN_IN_PX = (int) (MARGIN_IN_INCHES * PPI);

    //  the inner height and width is referring to the smaller space in the card where
    //  images can go. It's the space "inside the margins".
    private static final int INNER_HEIGHT_IN_PX = HEIGHT_IN_PX - (2 * MARGIN_IN_PX);
    private static final int INNER_WIDTH_IN_PX = WIDTH_IN_PX - (2 * MARGIN_IN_PX);


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

    //  probably shouldn't be instantiated unless user has clicked on the export button.
    public CardToBitmapConverter(Context context) {
        options = options.getInstance(); // probably unneeded. Assuming that Game has the options.
        game = new Game();
        rand = new GenRand(context, INNER_WIDTH_IN_PX, INNER_HEIGHT_IN_PX);
        setupCards();
        initFileNames();
        createBitmaps();
    }

    private void initFileNames() {
        //  hopefully this isn't too many digits for a filename
        String lastDigitsOfCurrentTime = toLastDigits(getSystemTime(), 6);
        String postfix = RESOURCE_DIVIDER + lastDigitsOfCurrentTime;
        fileNames = new ArrayList<>();
        for (int i = 0; i < cards.size(); ++i) {
            fileNames.add(EXPORTED_CARD_PREFIX + i + postfix);
        }
    }

    /**
     * @return the current time in seconds.
     */
    private long getSystemTime() {
        //  CITATION: the code for this method is based off various sources:
        //  -   https://stackoverflow.com/a/5369753/10752685
        //  -   https://developer.android.com/reference/java/util/Calendar.html

        //  not sure if this should be a long or if int is good enough.
       return Calendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * @return the last ten digits of a number, as a string.
     */
    private String toLastDigits(long n, int numDigits) {
        if (numDigits < 1 || numDigits > 100) {
            throw new IllegalArgumentException("Error: Invalid numDigits argument." +
                    " numDigits must be in range [1, 100]");
        }
        String lastDigits = "";
        int digit;
        int reverseDigits[] = new int[numDigits];
        for (int i = 0; i < numDigits; ++i) {
            digit = (int) n % 10;
            reverseDigits[i] = digit;
            n /= 10;
        }
        for (int i = numDigits - 1; i >= 0; --i) {
            lastDigits += reverseDigits[i];
        }
        return lastDigits;
    }

    //  Code under construction
//    private Bitmap toBitmap(Card c) {
//        //  construct list of subImages.
//        //  Constructs each subimage based on the specifications from the Card c.
//        List<Bitmap> subImages = new ArrayList();
//        List<Integer> imagesMap = c.getImagesMap();
//
//        //  using the imagesMap ArrayList is relatively arbitrary, but it creates a
//        //  for loop that will iterate the correct number of times. May want to refactor.
//        for (Integer imageFileIndex : imagesMap) {
//
//        }
//
//        return createComposite(subImages);
//    }

    //  Code under construction
    //  puts the sub images into a composite image to make the full card picture.
//    private Bitmap createComposite(List<Bitmap> subImages) {
//
//    }

    private void createBitmaps() {
        //  would be neat to have the last image in bitmaps be an image for the back of a card.
        bitmaps = new ArrayList<>();
        for (Card c : cards) {
//            bitmaps.add(toBitmap(c));
        }
    }

    public List<String> getFileNames() {
        return fileNames;
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
