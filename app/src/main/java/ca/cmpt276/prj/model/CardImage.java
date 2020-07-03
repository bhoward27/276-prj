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
public enum CardImage { CAVE(1), DESERT(2), FOREST(3), OCEAN(4), STORM(5), SUNRISE(6), VOLCANO(7),
    BEAR(8), EAGLE(9), LEOPARD(10), LION(11), SHARK(12), SNAKE(13), SPIDER(14);
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