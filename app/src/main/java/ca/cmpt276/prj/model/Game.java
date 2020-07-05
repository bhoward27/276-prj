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
		// initial game state check (or nothing selected in one pile)
		if (selectedDiscardPileImage == null || selectedDrawPileImage == null) {
			return false;
		}

		if (selectedDiscardPileImage == selectedDrawPileImage) {
			deck.moveTopDrawToDiscard();
			selectedDiscardPileImage = null;
			selectedDrawPileImage = null;
			return true;
		} else {
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

	public int getRemainingCards() {
		return deck.getDrawPile().size();
	}

	//debug

	public CardImage getSelectedDiscardPileImage() {
		return selectedDiscardPileImage;
	}

	public CardImage getSelectedDrawPileImage() {
		return selectedDrawPileImage;
	}
}
