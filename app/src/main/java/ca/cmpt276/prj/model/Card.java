package ca.cmpt276.prj.model;

public class Card {
    private CardImage topImage;
    private CardImage middleImage;
    private CardImage bottomImage;

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

    //  May want to make another method which tests for UNIQUE matching (i.e., one and ONLY one match).
    //  hasMatch simply tells you if it has AT LEAST one match.
    public boolean hasMatch(Card c) {
        return topImage.equals(c.getTopImage()) || topImage.equals(c.getMiddleImage())
                || topImage.equals(c.getBottomImage())

                || middleImage.equals(c.getTopImage()) || middleImage.equals(c.getMiddleImage())
                || middleImage.equals(c.getBottomImage())

                || bottomImage.equals(c.getTopImage()) || bottomImage.equals(c.getMiddleImage())
                || bottomImage.equals(c.getBottomImage());
    }

    private boolean isUniformlyThemed(boolean isLandscapeImageSet, CardImage topImage,
                                      CardImage middleImage, CardImage bottomImage) {
        if (isInPredatorSet(topImage)) {
            if (isLandscapeImageSet) {
                return false;
            }
            else {
                return isInPredatorSet(middleImage) && isInPredatorSet(bottomImage);
            }
        }
        else {
            if (!isLandscapeImageSet) {
                return false;
            }
            else {
                return !isInPredatorSet(middleImage) && !isInPredatorSet(bottomImage);
            }
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
