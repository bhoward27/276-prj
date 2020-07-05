package ca.cmpt276.prj.model;

import java.util.List;

public class Game {
	Deck deck;
	CardImage selectedDiscardPileImage = null;
	CardImage selectedDrawPileImage = null;

	public Game(int imagesPerCard, int imageSet) {
		this.deck = new Deck(imagesPerCard, imageSet);
	}

	public Deck getDeck() {
		return deck;
	}

	public boolean tappedUpdateState(boolean isTappedDiscardPile, CardImage image) {
		if (isTappedDiscardPile) {
			selectedDiscardPileImage = image;
		} else {
			selectedDrawPileImage = image;
		}

		if (selectedDiscardPileImage == null || selectedDrawPileImage == null) {
			return false;
		}

		if (selectedDiscardPileImage == selectedDrawPileImage) {
			deck.moveTopDrawToDiscard();
			return true;
		} else {
			selectedDiscardPileImage = null;
			selectedDrawPileImage = null;
			return false;
		}

	}

	public List<CardImage> getDiscardPileImages() {
		return deck.getDiscardPileImages();
	}

	public List<CardImage> getDrawPileImages() {
		return deck.getDrawPileImages();
	}

	public boolean isGameOver() {
		return deck.getDrawPile().isEmpty();
	}

}
