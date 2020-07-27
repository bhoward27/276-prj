package ca.cmpt276.prj.model;

import java.util.List;

import static ca.cmpt276.prj.model.Constants.NONE_SELECTED;

/**
 * The Game class interfaces with Deck to keep the game state.
 * Use the return value of tappedUpdateState to determine
 * whether or not the tapped image matches with the previously tapped image.
 */
public class Game {
	Deck deck;
	int selectedDiscardPileImage;
	int selectedDrawPileImage;
	int order;
	int deckSize;

	public Game(int order, int deckSize, boolean isWordMode) {
		this.deck = new Deck(order, deckSize, isWordMode);
		this.selectedDiscardPileImage = NONE_SELECTED;
		this.selectedDrawPileImage = NONE_SELECTED;
		this.order = order;
		this.deckSize = deckSize;
	}

	public Deck getDeck() {
		return deck;
	}

	public int getDeckSize() {
		return deckSize;
	}

	public boolean tappedUpdateState(int imageIndex) {
		if (deck.getDiscardPileImages().contains(imageIndex)) {
			deck.moveTopDrawToDiscard();
			return true;
		} else {
			return false;
		}
	}

	public int getNumImagesPerCard() {
		return order+1;
	}

	public List<Integer> getDiscardPileImages() {
		return deck.getDiscardPileImages();
	}

	public List<Integer> getDrawPileImages() {
		return deck.getDrawPileImages();
	}

	public boolean isGameOver() {
		return deck.getDrawPile().isEmpty();
	}

	public int getRemainingCards() {
		return deck.getDrawPile().size();
	}
}
