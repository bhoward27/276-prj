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

import java.util.List;

public class CardToJPGConverter {
    private OptionsManager options; //  do I need options?
    private Game game; //   Not sure if this should be member or just a local variable in a function.
    private GenRand rand; //    Not sure if should be member.
    private List<Card> cards; //    not sure if should be a member.

    CardToJPGConverter(Context context) {
        options = options.getInstance();
        game = new Game();
        //rand = new GenRand(context, maxX, maxY);
        setupCards();
    }

//    public Bitmap getBitmapsss() {
//
//    }

    private void setupCards() {
        cards = game.getDeck().getAllCards();
        for (Card c : cards) {
            rand.gen(c);
        }
    }
}
