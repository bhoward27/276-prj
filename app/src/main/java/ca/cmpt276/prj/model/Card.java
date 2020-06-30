package ca.cmpt276.prj.model;

public class Card {
    private boolean isLandscapeImageSet;  //  Might want this to be only in Deck class.
    private CardImage topImage;
    private CardImage middleImage;
    private CardImage bottomImage;

    public Card(boolean isLandscapeImageSet, CardImage topImage, CardImage middleImage, CardImage bottomImage) {
        this.isLandscapeImageSet = isLandscapeImageSet;
        if (hasDuplicateImage(topImage, middleImage, bottomImage)) {
            throw new IllegalArgumentException("Error: All three CardImages must be unique.");
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

    private boolean isNotUniformlyThemed(CardImage topImage, CardImage middleImage,
                                         CardImage bottomImage) {
        if (isInPredatorSet(topImage)) {
            if (isLandscapeImageSet) {
                return true;
            }
            else {
                if (isInPredatorSet(middleImage)) {
                    if (isInPredatorSet(bottomImage)) {
                        return false;
                    }
                    else {
                        return true;
                    }
                }
                else {
                    return true;
                }
            }
        }
        else {
            //  NOTE: this should have it's own proper if-else structure, not just return true.
            //  I only set it to that so that it will compile on my commit.
            return true;
        }
    }

    private boolean isInPredatorSet(CardImage image) {
        return image.compareTo(CardImage.BEAR) >= 0;
    }
}
