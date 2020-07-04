package ca.cmpt276.prj.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum defines the two sets of images that can appear on a card.
 * Any image not defined in this enum is invalid for a card.
 * Citation:
 * https://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum
 * Makes accessing values and enums easier
 * TODO: refactor image set selection elsewhere for future expandability
 */
public enum CardImage { CAVE, DESERT, FOREST, OCEAN, STORM, SUNRISE, VOLCANO,
    BEAR, EAGLE, LEOPARD, LION, SHARK, SNAKE, SPIDER
}