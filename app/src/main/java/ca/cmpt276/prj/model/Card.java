package ca.cmpt276.prj.model;

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
     * @param isLandscapeImageSet set to true if using the landscape image set;
     *                            set to false if using the predator image set.
     */
    public Card(boolean isLandscapeImageSet, CardImage topImage, CardImage middleImage,
                CardImage bottomImage) {
        if (hasDuplicateImage(topImage, middleImage, bottomImage)) {
            throw new IllegalArgumentException("Error: All three CardImages must be unique.");
        }
        if (!isUniformlyThemed(isLandscapeImageSet, topImage, middleImage, bottomImage)) {
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
     * @param isLandscapeImageSet set to true if using the landscape image set;
     *                             set to false if using the predator image set.
     * @return true if all CardImages are from the same image set and match with
     *          isLandscapeImageSet; false otherwise.
     */
    private boolean isUniformlyThemed(boolean isLandscapeImageSet, CardImage topImage,
                                      CardImage middleImage, CardImage bottomImage) {
        if (isLandscapeImageSet) {
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

    public boolean hasMatch(Card c) {
        CardImage thisImages[] = { topImage, middleImage, bottomImage };
        CardImage cImages[] = { c.getTopImage(), c.getMiddleImage(), c.getBottomImage() };
        for (CardImage x : thisImages) {
            for (CardImage y : cImages) {
                if (x.equals(y)) {
                    return true;
                }
            }
        }
        return false;
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

    public boolean hasExactlyOneMatch(Card c) {
        CardImage thisImages[] = { topImage, middleImage, bottomImage };
        CardImage cImages[] = { c.getTopImage(), c.getMiddleImage(), c.getBottomImage() };

        boolean foundMatch = false;
        for (CardImage x : thisImages) {
            if (!foundMatch) {
                int numMatches = 0;
                for (CardImage y : cImages) {
                    if (x.equals(y)) {
                        foundMatch = true;
                        if (++numMatches > 1) {
                            return false;
                        }
                    }
                }
            }
            else {
                for (CardImage y : cImages) {
                    if (x.equals(y)) {
                        return false;
                    }
                }
            }
        }
        return foundMatch;
    }
}