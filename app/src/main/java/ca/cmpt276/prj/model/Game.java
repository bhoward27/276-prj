package ca.cmpt276.prj.model;

import java.util.List;

import static ca.cmpt276.prj.model.Constants.*;

/**
 * The Game class interfaces with Deck to keep the game state.
 * Use the return value of tappedUpdateState to determine
 * whether or not the tapped image matches with the previously tapped image.
 */
public class Game {
	Deck deck;
	int selectedDiscardPileImage;
	int selectedDrawPileImage;

	public Game() {
		this.deck = new Deck();
		this.selectedDiscardPileImage = NONE_SELECTED;
		this.selectedDrawPileImage = NONE_SELECTED;
	}

	public Deck getDeck() {
		return deck;
	}

	public boolean tappedUpdateState(int imageIndex) {
		if (deck.getDiscardPileImages().contains(imageIndex)) {
			deck.moveTopDrawToDiscard();
			return true;
		} else {
			return false;
		}
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
