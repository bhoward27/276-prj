package ca.cmpt276.prj.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import static ca.cmpt276.prj.model.Constants.*;

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
	private OptionsManager opt;
	private int totalNumCards;
	private int order;
	private int deckSize;
	private int numImagesPerCard;
	private int numImagesInImageSet;
	private boolean isWordMode;

	public Deck() {
		this.discardPile = new Stack<>();
		this.drawPile = new Stack<>();
		this.allCards = new ArrayList<>();

		this.opt = OptionsManager.getInstance();

		this.order = opt.getOrder();
		this.isWordMode = opt.isWordMode();
		this.deckSize = opt.getDeckSize();
		if (opt.getImageSet() == FLICKR_IMAGE_SET) {
			this.numImagesInImageSet = opt.getFlickrImageSetSize();
		} else {
			this.numImagesInImageSet = opt.getNumImagesInImageSet();
		}


		this.numImagesPerCard = order + 1;
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
		// shuffle card orders
		Collections.shuffle(cards);

		List<List<Boolean>> deckBools = new ArrayList<>();
		Log.d("num", "numImagesInImageSet: " + numImagesInImageSet);
		// randomly choose images to be words
		List<Boolean> cardBools = new ArrayList<>();
		if (isWordMode) {
			ThreadLocalRandom rand = ThreadLocalRandom.current();
			for (int card = 0; card < deckSize; card++) {
				cardBools.clear();
				// add at least one word and image if wordmode is enabled
				cardBools.add(true);
				cardBools.add(false);
				for (int i = 0; i < numImagesPerCard - 2; i++) {
					cardBools.add(rand.nextBoolean());
				}
				Collections.shuffle(cardBools);
				deckBools.add(new ArrayList<>(cardBools));
			}
		} else {
			for (int card = 0; card < deckSize; card++) {
				cardBools.clear();
				for (int i = 0; i < numImagesPerCard; i++) {
					cardBools.add(false);
				}
				deckBools.add(new ArrayList<>(cardBools));
			}
		}

		// randomize images taken from imageset
		List<Integer> randMap = new ArrayList<>(numImagesInImageSet);
		for (int i = 0; i < numImagesInImageSet; i++) {
			randMap.add(i);
		}
		Collections.shuffle(randMap);

		// change the card image indices to random ones in the available images
		for (Integer[] card : cards) {
			for (int i = 0; i < card.length; i++) {
				card[i] = randMap.get(card[i]);
			}
		}

		// add to the drawpile a random card until there are no cards left
		int cardNum = 0;
		for (Integer[] card : cards) {
			if (cardNum >= deckSize) break;
			int cardIndex = cards.indexOf(card);
			drawPile.push(new Card(Arrays.asList(card), cardIndex, deckBools.get(cardIndex)));
			cardNum++;
		}

		allCards.addAll(drawPile);

		moveTopDrawToDiscard();
	}

	// Citation: https://radiganengineering.com/2013/01/spot-it-howd-they-do-that/
	// for hardcoded entries
	private void setCardOrders() {
		// only 3 images per card is currently implemented
		switch (order) {
			case 2:
				cardConfigurations =
						new Integer[][]{{0, 1, 4},
								{2, 3, 4}, {0, 2, 5},
								{1, 3, 5}, {0, 3, 6},
								{1, 2, 6}, {4, 5, 6}};
				break;
			case 3:
				cardConfigurations =
						new Integer[][]{{0, 1, 2, 9},
								{9, 3, 4, 5}, {8, 9, 6, 7},
								{0, 10, 3, 6}, {1, 10, 4, 7},
								{8, 2, 10, 5}, {0, 8, 11, 4},
								{1, 11, 5, 6}, {11, 2, 3, 7},
								{0, 12, 5, 7}, {8, 1, 3, 12},
								{12, 2, 4, 6}, {9, 10, 11, 12}};
				break;
			case 5:
				cardConfigurations =
						new Integer[][]{{0, 1, 2, 3, 4, 25}, {5, 6, 7, 8, 9, 25}, {10, 11, 12, 13, 14, 25}, {15, 16, 17, 18, 19, 25}, {20, 21, 22, 23, 24, 25}, {0, 5, 10, 15, 20, 26}, {1, 6, 11, 16, 21, 26}, {2, 7, 12, 17, 22, 26},
								{3, 8, 13, 18, 23, 26}, {4, 9, 14, 19, 24, 26}, {0, 6, 12, 18, 24, 27}, {1, 7, 13, 19, 20, 27}, {2, 8, 14, 15, 21, 27}, {3, 9, 10, 16, 22, 27}, {4, 5, 11, 17, 23, 27}, {0, 7, 14, 16, 23, 28},
								{1, 8, 10, 17, 24, 28}, {2, 9, 11, 18, 20, 28}, {3, 5, 12, 19, 21, 28}, {4, 6, 13, 15, 22, 28}, {0, 8, 11, 19, 22, 29}, {1, 9, 12, 15, 23, 29}, {2, 5, 13, 16, 24, 29}, {3, 6, 14, 17, 20, 29},
								{4, 7, 10, 18, 21, 29}, {0, 9, 13, 17, 21, 30}, {1, 5, 14, 18, 22, 30}, {2, 6, 10, 19, 23, 30}, {3, 7, 11, 15, 24, 30}, {4, 8, 12, 16, 20, 30}, {25, 26, 27, 28, 29, 30},
						};
				break;
			default:
				throw new UnsupportedOperationException("This order is not implemented.");
		}
	}
}
