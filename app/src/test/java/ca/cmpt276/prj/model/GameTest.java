package ca.cmpt276.prj.model;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static ca.cmpt276.prj.model.Constants.DISCARD_PILE_TAPPED;
import static ca.cmpt276.prj.model.Constants.DRAW_PILE_TAPPED;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES;
import static ca.cmpt276.prj.model.Constants.PREDATOR_SET;
import static org.junit.jupiter.api.Assertions.*;


class GameTest {

	@RepeatedTest(100)
	void basicGameplay() {
		Game game = new Game(NUM_IMAGES, LANDSCAPE_SET);

		List<CardImage> possibleDrawImages = game.getDrawPileImages();
		List<CardImage> possibleDiscardImages = game.getDiscardPileImages();

		int numtries = 0;
		int matches = 0;
		while (game.getRemainingCards() > 0) {
			boolean success = false;
			possibleDrawImages = game.getDrawPileImages();
			possibleDiscardImages = game.getDiscardPileImages();
			System.out.println("Cards Remaining: " + game.getRemainingCards());
			for (CardImage imageDraw : possibleDrawImages) {
				if (success) break;
				System.out.println("Draw pile: " + possibleDrawImages);
				System.out.println("Discard pile: " + possibleDiscardImages);
				for (CardImage imageDisc : possibleDiscardImages) {
					success = (game.tappedUpdateState(DISCARD_PILE_TAPPED, imageDisc) || game.tappedUpdateState(DRAW_PILE_TAPPED, imageDraw));
					System.out.println("Draw selected: " + game.getSelectedDrawPileImage());
					System.out.println("Discard selected: " + game.getSelectedDiscardPileImage());
					if (success) {
						System.out.println(imageDraw + " matched " + imageDisc);
						matches++;
						success = true;
						break;
					} else {
						numtries++;
					}
				}
			}
		}

		System.out.println("matches: " + matches);
		System.out.println("numtries: " + numtries);
		assertTrue(game.isGameOver());
		assertEquals(6, matches);

	}

}