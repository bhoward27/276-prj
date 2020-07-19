package ca.cmpt276.prj.model;

import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Constants.PREDATOR_SET;

/**
 * This model has data about the three images on a particular card. Its constructor ensures that a
 * card does not have any duplicate images on it, and that its images are all from the same image
 * set / theme (either all predators or all landscapes). It has public methods for determining if
 * two cards have a matching image.
 */
public class Card {
    private CardImage topImage;
    private CardImage middleImage;
    private CardImage bottomImage;

    /**
     * @param imageSet set to 1 if using the landscape image set;
     *                        2 if using the predator image set.
     */
    public Card(int imageSet, CardImage topImage, CardImage middleImage,
                CardImage bottomImage) {
        if (hasDuplicateImage(topImage, middleImage, bottomImage)) {
            throw new IllegalArgumentException("Error: All three CardImages must be unique.");
        }
        if (!isUniformlyThemed(imageSet, topImage, middleImage, bottomImage)) {
            throw new IllegalArgumentException("Error: All three CardImages must be from the " +
                    "same image set.");
        }

        this.topImage = topImage;
        this.middleImage = middleImage;
        this.bottomImage = bottomImage;
    }

    private boolean hasDuplicateImage(CardImage topImage, CardImage middleImage,
                                      CardImage bottomImage) {
        return (topImage.equals(middleImage) || topImage.equals(bottomImage)
                || middleImage.equals(bottomImage));
    }

    /**
     * @param imageSet set to 1 if using the landscape image set;
                       set to 2 if using the predator image set.
     * @return true if all CardImages are from the same image set and match with
     *          imageSet; false otherwise.
     */
    private boolean isUniformlyThemed(int imageSet, CardImage topImage,
                                      CardImage middleImage, CardImage bottomImage) {

        if (imageSet == LANDSCAPE_SET) {
            return !isInPredatorSet(topImage) && !isInPredatorSet(middleImage) &&
                    !isInPredatorSet(bottomImage);
        }
        else {
            return isInPredatorSet(topImage) && isInPredatorSet(middleImage) &&
                    isInPredatorSet(bottomImage);
        }
    }

    private boolean isInPredatorSet(CardImage image) {
        return image.compareTo(CardImage.BEAR) >= 0;
    }

    public CardImage getTopImage() {
        return topImage;
    }

    public CardImage getMiddleImage() {
        return middleImage;
    }

    public CardImage getBottomImage() {
        return bottomImage;
    }
}