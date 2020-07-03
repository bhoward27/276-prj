package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	private List<Card> discardPile;
	private List<Card> drawPile;

	public Deck(int order) {
		discardPile = new ArrayList<>();
		drawPile = new ArrayList<>();

		initializePiles(order);
	}

	public void moveTopDrawToDiscard() {
		// treat 0th index as top
		discardPile.add(0, drawPile.get(0));
		drawPile.remove(0);
	}

	// get top card in discard pile, short function name for future readability
	public Card topDsc() {
		return discardPile.get(0);
	}

	// get top card in draw pile pile
	public Card topDrw() {
		return drawPile.get(0);
	}

	// citation https://radiganengineering.com/2013/01/spot-it-howd-they-do-that/
	// hardcoded entries so far because our order is fixed to 2 for this iteration
	private void initializePiles(int order) {
		// number of cards per deck is order^2 - order + 1
		int cardsPerDeck = order*order - order + 1;
		if (order == 2) {
			int[][] cardOrders = {{0, 1, 4},
					{2, 3, 4}, {0, 2, 5},
					{1, 3, 5}, {0, 3, 6},
					{1, 2, 6}, {4, 5, 6}};

			for (int i = 0; i < cardsPerDeck; i++) {
				CardImage image1 = CardImage.valueOf(cardOrders[i][0]);
				CardImage image2 = CardImage.valueOf(cardOrders[i][1]);
				CardImage image3 = CardImage.valueOf(cardOrders[i][2]);

				// TODO: hardcoded landscape image set here because there's no elegant way to pass it in
				drawPile.add(new Card(true, image1, image2, image3));
			}
			Collections.shuffle(drawPile);
			moveTopDrawToDiscard();
		} else {
			throw new UnsupportedOperationException("Not implemented: order != 2");
		}
	}
}
