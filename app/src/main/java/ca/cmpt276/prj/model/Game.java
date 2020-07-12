package ca.cmpt276.prj.model;

import java.util.List;

/**
 * The Game class interfaces with Deck to keep the game state.
 * Use the return value of tappedUpdateState to determine
 * whether or not the tapped image matches with the previously tapped image.
 */
public class Game {
	Deck deck;
	CardImage selectedDiscardPileImage = null;
	CardImage selectedDrawPileImage = null;
	int imagesPerCard;

	public Game(int imagesPerCard, int imageSet) {
		this.deck = new Deck(imagesPerCard, imageSet);
		this.imagesPerCard = imagesPerCard;
	}

	public Deck getDeck() {
		return deck;
	}

	public boolean tappedUpdateState(CardImage image) {

		if (deck.getTopDiscard().hasMatchImage(image)) {
			deck.moveTopDrawToDiscard();
			return true;
		} else {
			return false;
		}

	}

	public int getImagesPerCard() {
		return imagesPerCard;
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
