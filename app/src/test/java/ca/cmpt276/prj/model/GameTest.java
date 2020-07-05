package ca.cmpt276.prj.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static ca.cmpt276.prj.model.Constants.DRAW_PILE_TAPPED;
import static ca.cmpt276.prj.model.Constants.NUM_IMAGES;
import static ca.cmpt276.prj.model.Constants.PREDATOR_SET;

class GameTest {

	@Test
	void basicGameplay() {
		Game game = new Game(NUM_IMAGES, PREDATOR_SET);

		List<CardImage> possibleDrawImages = game.getDrawPileImages();
		List<CardImage> possibleDiscardImages = game.getDiscardPileImages();

		

		//game.tappedUpdateState(DRAW_PILE_TAPPED, );


	}

}