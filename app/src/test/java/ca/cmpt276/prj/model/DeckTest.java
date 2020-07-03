package ca.cmpt276.prj.model;

import org.junit.jupiter.api.Test;

import static ca.cmpt276.prj.model.Card.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Card.PREDATOR_SET;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
	private static final String TAG = "%%%DECKTEST: ";
	public static final int NUM_IMAGES = 3;

	@Test
	void basic() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		assertEquals(deck.getDiscardPile().size(), 1);
		assertEquals(deck.getDrawPile().size(), deck.getTotalNumCards()-1);
	}

	@Test
	void testMovingCard() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		deck.moveTopDrawToDiscard();
		assertEquals(deck.getDiscardPile().size(), 2);
		assertEquals(deck.getDrawPile().size(), deck.getTotalNumCards()-2);
	}

	@Test
	void testMovingCardValues() {
		Deck deck = new Deck(NUM_IMAGES, LANDSCAPE_SET);
		System.out.println(TAG + "Draw Top Image: " + deck.getTopDrw().getTopImage());
		System.out.println(TAG + "Draw Mid Image: " + deck.getTopDrw().getMiddleImage());
		System.out.println(TAG + "Draw Bot Image: " + deck.getTopDrw().getBottomImage());
		System.out.println(TAG + "Discard Top Image: " + deck.getTopDsc().getTopImage());
		System.out.println(TAG + "Discard Mid Image: " + deck.getTopDsc().getMiddleImage());
		System.out.println(TAG + "Discard Bot Image: " + deck.getTopDsc().getBottomImage());
		System.out.println(TAG + "MOVING");
		deck.moveTopDrawToDiscard();

		System.out.println(TAG + "Draw Top Image: " + deck.getTopDrw().getTopImage());
		System.out.println(TAG + "Draw Mid Image: " + deck.getTopDrw().getMiddleImage());
		System.out.println(TAG + "Draw Bot Image: " + deck.getTopDrw().getBottomImage());
		System.out.println(TAG + "Discard Top Image: " + deck.getTopDsc().getTopImage());
		System.out.println(TAG + "Discard Mid Image: " + deck.getTopDsc().getMiddleImage());
		System.out.println(TAG + "Discard Bot Image: " + deck.getTopDsc().getBottomImage());
	}

	@Test
	void testMovingCardValuesPredator() {
		Deck deck = new Deck(NUM_IMAGES, PREDATOR_SET);
		System.out.println(TAG + "Draw Top Image: " + deck.getTopDrw().getTopImage());
		System.out.println(TAG + "Draw Mid Image: " + deck.getTopDrw().getMiddleImage());
		System.out.println(TAG + "Draw Bot Image: " + deck.getTopDrw().getBottomImage());
		System.out.println(TAG + "Discard Top Image: " + deck.getTopDsc().getTopImage());
		System.out.println(TAG + "Discard Mid Image: " + deck.getTopDsc().getMiddleImage());
		System.out.println(TAG + "Discard Bot Image: " + deck.getTopDsc().getBottomImage());
		System.out.println(TAG + "MOVING");
		deck.moveTopDrawToDiscard();

		System.out.println(TAG + "Draw Top Image: " + deck.getTopDrw().getTopImage());
		System.out.println(TAG + "Draw Mid Image: " + deck.getTopDrw().getMiddleImage());
		System.out.println(TAG + "Draw Bot Image: " + deck.getTopDrw().getBottomImage());
		System.out.println(TAG + "Discard Top Image: " + deck.getTopDsc().getTopImage());
		System.out.println(TAG + "Discard Mid Image: " + deck.getTopDsc().getMiddleImage());
		System.out.println(TAG + "Discard Bot Image: " + deck.getTopDsc().getBottomImage());
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
}