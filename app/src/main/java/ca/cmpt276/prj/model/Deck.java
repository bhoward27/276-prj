package ca.cmpt276.prj.model;

import java.util.Collections;
import java.util.Stack;

/**
 * The Deck class handles the interfacing of the two card piles which will be used by the Game Instance class.
 * The constructor takes in how many images per card and a value for the image set, allowing
 * the selection between image set 1 (landscape) and image set 2 (predator)
 */
public class Deck {
	private Stack<Card> discardPile;
	private Stack<Card> drawPile;
	private int totalNumCards;
	private int imageSet;
	private int imageSetOffset;
	private int imagesPerCard;

	public Deck(int imagesPerCard, int imageSet) {
		this.discardPile = new Stack<>();
		this.drawPile = new Stack<>();
		this.imagesPerCard = imagesPerCard;

		// total number of cards is images^2 - images + 1
		this.totalNumCards = imagesPerCard*imagesPerCard - imagesPerCard + 1;
		this.imageSet = imageSet;
		// offset
		this.imageSetOffset = (imageSet-1)*totalNumCards;

		initializePiles();
	}

	// return false if the draw pile has nothing left
	public boolean moveTopDrawToDiscard() {
		if (drawPile.isEmpty()) {
			return false;
		}
		discardPile.push(drawPile.pop());
		return true;
	}

	public Card getTopDiscard() {
		return discardPile.peek();
	}

	public Card getTopDraw() {
		return drawPile.peek();
	}

	public Stack<Card> getDrawPile() {
		return drawPile;
	}

	public Stack<Card> getDiscardPile() {
		return discardPile;
	}

	public int getTotalNumCards() {
		return totalNumCards;
	}

	public int getImageSet() {
		return imageSet;
	}

	public CardImage[] getDiscardPileImages() {
		Card card = getTopDiscard();
		CardImage[] images = new CardImage[3];

		images[0] = card.getTopImage();
		images[1] = card.getMiddleImage();
		images[2] = card.getBottomImage();

		return images;
	}

	public CardImage[] getDrawPileImages() {
		Card card = getTopDraw();
		CardImage[] images = new CardImage[3];

		images[0] = card.getTopImage();
		images[1] = card.getMiddleImage();
		images[2] = card.getBottomImage();

		return images;
	}

	// citation https://radiganengineering.com/2013/01/spot-it-howd-they-do-that/
	// hardcoded entries so far because our order is fixed to 2 for this iteration
	private void initializePiles() {
		if (imagesPerCard == 3) {
			int[][] cardOrders = {{0, 1, 4},
					{2, 3, 4}, {0, 2, 5},
					{1, 3, 5}, {0, 3, 6},
					{1, 2, 6}, {4, 5, 6}};

			for (int i = 0; i < totalNumCards; i++) {
				CardImage[] values = CardImage.values();
				CardImage image1 = values[cardOrders[i][0] + imageSetOffset];
				CardImage image2 = values[cardOrders[i][1] + imageSetOffset];
				CardImage image3 = values[cardOrders[i][2] + imageSetOffset];

				drawPile.push(new Card(imageSet, image1, image2, image3));
			}
			Collections.shuffle(drawPile);
			moveTopDrawToDiscard();
		} else {
			throw new UnsupportedOperationException("Not implemented: numImages != 3");
		}
	}
}
