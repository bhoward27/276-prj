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
public enum CardImage { CAVE(0), DESERT(1), FOREST(2), OCEAN(3), STORM(4), SUNRISE(5), VOLCANO(6),
    BEAR(7), EAGLE(8), LEOPARD(9), LION(10), SHARK(11), SNAKE(12), SPIDER(13);
    private int value;
    private static Map<Integer, CardImage> map = new HashMap<>();

    CardImage(int value) {
        this.value = value;
    }

    static {
        for (CardImage cardImage : CardImage.values()) {
            map.put(cardImage.value, cardImage);
        }
    }

    public static CardImage valueOf(int cardImage) {
        return (CardImage) map.get(cardImage);
    }

    public int getValue() {
        return value;
    }
}