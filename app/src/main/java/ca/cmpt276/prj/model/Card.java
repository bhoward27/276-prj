package ca.cmpt276.prj.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This model has data about the three images on a particular card. Its constructor ensures that a
 * card does not have any duplicate images on it, and that its images are all from the same image
 * set / theme (either all predators or all landscapes). It has public methods for determining if
 * two cards have a matching image.
 */
public class Card {
    private List<Integer> imagesMap;

    public Card(List<Integer> imageOrders) {
        this.imagesMap = new ArrayList<>();
        imagesMap.addAll(imageOrders);

    }

    public List<Integer> getImagesMap() {
        return imagesMap;
    }

    public Integer getImagesByIndex(int index) {
        return imagesMap.get(index);
    }
}