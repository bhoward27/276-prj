package ca.cmpt276.prj.model;

import java.util.List;

/**
 * The Game class interfaces with Deck to keep the game state.
 * Use the return value of tappedUpdateState to determine
 * whether or not the tapped image matches with the previously tapped image.
 */
public class Game {
	Deck deck;
	int selectedDiscardPileImage;
	int selectedDrawPileImage;
	int imagesPerCard;

	public Game(int imagesPerCard) {
		this.deck = new Deck(imagesPerCard);
		this.selectedDiscardPileImage = Integer.MAX_VALUE;
		this.selectedDrawPileImage = Integer.MAX_VALUE;
		this.imagesPerCard = imagesPerCard;
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
		if (selectedDiscardPileImage == Integer.MAX_VALUE || selectedDrawPileImage == Integer.MAX_VALUE) {
			return false;
		}

		if (selectedDiscardPileImage == selectedDrawPileImage) {
			deck.moveTopDrawToDiscard();
			selectedDiscardPileImage = Integer.MAX_VALUE;
			selectedDrawPileImage = Integer.MAX_VALUE;
			return true;
		} else {
			return false;
		}
	}

	public int getImagesPerCard() {
		return imagesPerCard;
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
