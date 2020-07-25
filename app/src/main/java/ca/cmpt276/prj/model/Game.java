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

	public Game(int order) {
		this.deck = new Deck(order);
		this.selectedDiscardPileImage = NONE_SELECTED;
		this.selectedDrawPileImage = NONE_SELECTED;
		this.order = order;
	}

	public Deck getDeck() {
		return deck;
	}

	public boolean tappedUpdateState(boolean isTappedDiscardPile, int imageIndex) {
		if (isTappedDiscardPile) {
			selectedDiscardPileImage = imageIndex;
		} else {
			selectedDrawPileImage = imageIndex;
		}
		// Initial game state check (or nothing selected in one pile)
		if (selectedDiscardPileImage == NONE_SELECTED || selectedDrawPileImage == NONE_SELECTED) {
			return false;
		}

		if (selectedDiscardPileImage == selectedDrawPileImage) {
			deck.moveTopDrawToDiscard();
			selectedDiscardPileImage = NONE_SELECTED;
			selectedDrawPileImage = NONE_SELECTED;
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
