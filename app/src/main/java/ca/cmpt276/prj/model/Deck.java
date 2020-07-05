package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * The Deck class handles the interfacing of the two card piles which will be used by the Game Instance class.
 * The constructor takes in how many images per card and a value for the image set, allowing
 * the selection between image set 1 (landscape) and image set 2 (predator)
 */
public class Deck {
	private Stack<Card> discardPile;
	private Stack<Card> drawPile;
	private Integer[][] cardOrders;
	private int totalNumCards;
	private int currentImageSet;
	private int imageSetOffset;
	private int imagesPerCard;

	public Deck(int imagesPerCard, int imageSetSelected) {
		this.discardPile = new Stack<>();
		this.drawPile = new Stack<>();
		this.imagesPerCard = imagesPerCard;

		// total number of cards is images^2 - images + 1
		this.totalNumCards = imagesPerCard*imagesPerCard - imagesPerCard + 1;
		this.currentImageSet = imageSetSelected;
		// offset
		this.imageSetOffset = (imageSetSelected-1)*totalNumCards;

		setCardOrders();
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

	public int getCurrentImageSet() {
		return currentImageSet;
	}

	public List<CardImage> getDiscardPileImages() {
		Card card = getTopDiscard();
		List<CardImage> images = new ArrayList<>(imagesPerCard);

		images.add(card.getTopImage());
		images.add(card.getMiddleImage());
		images.add(card.getBottomImage());

		return images;
	}

	public List<CardImage> getDrawPileImages() {
		Card card = getTopDraw();
		List<CardImage> images = new ArrayList<>(imagesPerCard);

		images.add(card.getTopImage());
		images.add(card.getMiddleImage());
		images.add(card.getBottomImage());

		return images;
	}

	// convert hardcoded 2d array to list/stack and shuffle the order of cards
	// and order of images on each card independently
	private void initializePiles() {
		List<Integer[]> cards = new ArrayList<>(Arrays.asList(cardOrders));
		Stack<Integer> images = new Stack<>();

		Collections.shuffle(cards);

		for (Integer[] card : cards) {
			images.addAll(Arrays.asList(card));

			Collections.shuffle(images);

			CardImage[] values = CardImage.values();
			CardImage image1 = values[images.pop() + imageSetOffset];
			CardImage image2 = values[images.pop() + imageSetOffset];
			CardImage image3 = values[images.pop() + imageSetOffset];

			drawPile.push(new Card(currentImageSet, image1, image2, image3));
		}

		moveTopDrawToDiscard();
	}

	// citation https://radiganengineering.com/2013/01/spot-it-howd-they-do-that/
	// for hardcoded entries
	private void setCardOrders() {
		// only 3 images per card is currently implemented
		switch (imagesPerCard) {
			case 3: cardOrders =
					new Integer[][]{{0, 1, 4},
					{2, 3, 4}, {0, 2, 5},
					{1, 3, 5}, {0, 3, 6},
					{1, 2, 6}, {4, 5, 6}};
			break;
			default:
				throw new UnsupportedOperationException("Not implemented: imagesPerCard != 3");
		}
	}

}
