package ca.cmpt276.prj.model;

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

	public boolean doesTappedMatch(boolean isTappedDiscardPile, CardImage image) {
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

	public CardImage[] getDiscardPileImages() {
		return deck.getDiscardPileImages();
	}

	public CardImage[] getDrawPileImages() {
		return deck.getDrawPileImages();
	}

	public boolean isGameOver() {
		return deck.getDrawPile().isEmpty();
	}

}
