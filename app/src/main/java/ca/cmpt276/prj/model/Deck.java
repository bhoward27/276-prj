package ca.cmpt276.prj.model;

import android.util.Log;

import java.lang.reflect.Array;
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
	private List<Card> allCards;
	private Integer[][] cardConfigurations;
	private int totalNumCards;
	private int order;

	public Deck(int order) {
		this.discardPile = new Stack<>();
		this.drawPile = new Stack<>();
		this.allCards = new ArrayList<>();
		this.order = order;

		int numImagesPerCard = order + 1;
		// Total number of cards is images^2 - images + 1
		this.totalNumCards = numImagesPerCard * numImagesPerCard - numImagesPerCard + 1;

		setCardOrders();
		initializePiles();
	}

	// Return false if the draw pile has nothing left
	public boolean moveTopDrawToDiscard() {
		if (drawPile.isEmpty()) {
			return false;
		}
		discardPile.push(drawPile.pop());
		return true;
	}

	public List<Card> getAllCards() {
		return allCards;
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

	public List<Integer> getDiscardPileImages() {
		return getTopDiscard().getImagesMap();
	}

	public List<Integer> getDrawPileImages() {
		return getTopDraw().getImagesMap();
	}

	// Convert hardcoded 2d array to list/stack and shuffle the order of cards
	private void initializePiles() {
		List<Integer[]> cards = new ArrayList<>(Arrays.asList(cardConfigurations));
		Collections.shuffle(cards);

		for (Integer[] card : cards) {
			drawPile.push(new Card(Arrays.asList(card)));
		}

		allCards.addAll(drawPile);

		moveTopDrawToDiscard();
	}

	// Citation: https://radiganengineering.com/2013/01/spot-it-howd-they-do-that/
	// for hardcoded entries
	private void setCardOrders() {
		// only 3 images per card is currently implemented
		switch (order) {
			case 2: cardConfigurations =
					new Integer[][]{{0, 1, 4},
							{2, 3, 4}, {0, 2, 5},
							{1, 3, 5}, {0, 3, 6},
							{1, 2, 6}, {4, 5, 6}};
			break;
			case 3: cardConfigurations =
					new Integer[][]{{0, 1, 2, 9},
							{9, 3, 4, 5}, {8, 9, 6, 7},
							{0, 10, 3, 6}, {1, 10, 4, 7},
							{8, 2, 10, 5}, {0, 8, 11, 4},
							{1, 11, 5, 6}, {11, 2, 3, 7},
							{0, 12, 5, 7}, {8, 1, 3, 12},
							{12, 2, 4, 6}, {9, 10, 11, 12}};
			break;
			default:
				throw new UnsupportedOperationException("Not implemented: imagesPerCard != 3");
		}
	}
}
