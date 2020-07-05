package ca.cmpt276.prj.model;

import org.junit.jupiter.api.Test;

import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES;
import static ca.cmpt276.prj.model.Constants.PREDATOR_SET;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

	@Test
	void basic() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		assertEquals(1, deck.getDiscardPile().size());
		assertEquals(deck.getDrawPile().size(), deck.getTotalNumCards() - 1);
	}

	@Test
	void testMovingCard() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		deck.moveTopDrawToDiscard();
		assertEquals(2, deck.getDiscardPile().size());
		assertEquals(deck.getDrawPile().size(), deck.getTotalNumCards() - 2);
	}

	@Test
	void testMovingCardValues() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		System.out.println("Draw Top Image: " + deck.getTopDraw().getTopImage());
		System.out.println("Draw Mid Image: " + deck.getTopDraw().getMiddleImage());
		System.out.println("Draw Bot Image: " + deck.getTopDraw().getBottomImage());
		System.out.println("Discard Top Image: " + deck.getTopDiscard().getTopImage());
		System.out.println("Discard Mid Image: " + deck.getTopDiscard().getMiddleImage());
		System.out.println("Discard Bot Image: " + deck.getTopDiscard().getBottomImage());
		System.out.println("MOVING");
		deck.moveTopDrawToDiscard();

		System.out.println("Draw Top Image: " + deck.getTopDraw().getTopImage());
		System.out.println("Draw Mid Image: " + deck.getTopDraw().getMiddleImage());
		System.out.println("Draw Bot Image: " + deck.getTopDraw().getBottomImage());
		System.out.println("Discard Top Image: " + deck.getTopDiscard().getTopImage());
		System.out.println("Discard Mid Image: " + deck.getTopDiscard().getMiddleImage());
		System.out.println("Discard Bot Image: " + deck.getTopDiscard().getBottomImage());
	}

	@Test
	void testMovingCardValuesPredator() {
		Deck deck = new Deck(NUM_IMAGES, PREDATOR_SET);
		System.out.println("Draw Top Image: " + deck.getTopDraw().getTopImage());
		System.out.println("Draw Mid Image: " + deck.getTopDraw().getMiddleImage());
		System.out.println("Draw Bot Image: " + deck.getTopDraw().getBottomImage());
		System.out.println("Discard Top Image: " + deck.getTopDiscard().getTopImage());
		System.out.println("Discard Mid Image: " + deck.getTopDiscard().getMiddleImage());
		System.out.println("Discard Bot Image: " + deck.getTopDiscard().getBottomImage());
		System.out.println("MOVING");
		deck.moveTopDrawToDiscard();

		System.out.println("Draw Top Image: " + deck.getTopDraw().getTopImage());
		System.out.println("Draw Mid Image: " + deck.getTopDraw().getMiddleImage());
		System.out.println("Draw Bot Image: " + deck.getTopDraw().getBottomImage());
		System.out.println("Discard Top Image: " + deck.getTopDiscard().getTopImage());
		System.out.println("Discard Mid Image: " + deck.getTopDiscard().getMiddleImage());
		System.out.println("Discard Bot Image: " + deck.getTopDiscard().getBottomImage());
	}

	@Test
	void testEndOfCardPile() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		boolean ended = false;
		for (int i = 0; i < deck.getTotalNumCards()-1; i++) {
			ended = deck.moveTopDrawToDiscard();
		}
		assertTrue(ended);
	}

	@Test
	void testImageSetGetter() {
		Deck deckLand = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		Deck deckPred = new Deck(NUM_IMAGES, PREDATOR_SET);
		assertEquals(LANDSCAPE_SET, deckLand.getCurrentImageSet());
		assertEquals(PREDATOR_SET, deckPred.getCurrentImageSet());
	}

}