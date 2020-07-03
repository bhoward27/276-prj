package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	private List<Card> discardPile;
	private List<Card> drawPile;
	private int totalNumCards;
	private int imageSet;
	private int imageSetOffset;

	public Deck(int numImages, int imageSet) {
		this.discardPile = new ArrayList<>();
		this.drawPile = new ArrayList<>();

		// total number of cards is images^2 - images + 1
		this.totalNumCards = numImages*numImages - numImages + 1;
		this.imageSet = imageSet;
		// offset
		this.imageSetOffset = (imageSet-1)*totalNumCards;

		// order is numImages-1
		initializePiles(numImages-1);
	}

	// return false if the draw pile has nothing left
	public boolean moveTopDrawToDiscard() {
		if (drawPile.size() == 0) {
			return false;
		}
		// treat 0th index as top
		discardPile.add(0, drawPile.get(0));
		drawPile.remove(0);
		return true;
	}

	// get top card in discard pile, short function name for future readability
	public Card getTopDsc() {
		return discardPile.get(0);
	}

	// get top card in draw pile pile
	public Card getTopDrw() {
		return drawPile.get(0);
	}

	public List<Card> getDrawPile() {
		return drawPile;
	}

	public List<Card> getDiscardPile() {
		return discardPile;
	}

	public int getTotalNumCards() {
		return totalNumCards;
	}

	// citation https://radiganengineering.com/2013/01/spot-it-howd-they-do-that/
	// hardcoded entries so far because our order is fixed to 2 for this iteration
	private void initializePiles(int order) {
		if (order == 2) {
			int[][] cardOrders = {{0, 1, 4},
					{2, 3, 4}, {0, 2, 5},
					{1, 3, 5}, {0, 3, 6},
					{1, 2, 6}, {4, 5, 6}};

			for (int i = 0; i < totalNumCards; i++) {
				CardImage image1 = CardImage.valueOf(cardOrders[i][0] + imageSetOffset);
				CardImage image2 = CardImage.valueOf(cardOrders[i][1] + imageSetOffset);
				CardImage image3 = CardImage.valueOf(cardOrders[i][2] + imageSetOffset);

				drawPile.add(new Card(imageSet, image1, image2, image3));
			}
			Collections.shuffle(drawPile);
			moveTopDrawToDiscard();
		} else {
			throw new UnsupportedOperationException("Not implemented: order != 2");
		}
	}
}
