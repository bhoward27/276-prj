package ca.cmpt276.prj.model;

public class ImageNameMatrix {
    private static final int NUM_IMAGE_SETS = 2; // # of rows in the matrix
    private static final int NUM_IMAGES_PER_SET = 13; // # of columns in the matrix
    private static String cardImageNames[][];
    private static ImageNameMatrix instance;

    public static ImageNameMatrix getInstance() {
        if (instance == null) {
            instance = new ImageNameMatrix();
        }
        return instance;
    }

    private ImageNameMatrix() { }

    /**
     * This method copies the data from namesFromXML into cardImageNames,
     * a 2D array where each row holds all the image names of a particular image set.
     *
     * @param namesFromXML the 1D string array from card_image_names.xml.
     *
     * This method must be called before the ImageNameMatrix can be used,
     * otherwise a NullPointerException will likely occur.
     * This method should only be called once: on the splash screen activity.
     */
    public void setCardImageNames(String namesFromXML[]) {
        cardImageNames = new String[NUM_IMAGE_SETS][NUM_IMAGES_PER_SET];
        int row;
        int column;
        for (int i = 0; i < namesFromXML.length; ++i) {
            column = i % NUM_IMAGES_PER_SET;
            row = (i - column) / NUM_IMAGES_PER_SET;
            cardImageNames[row][column] = namesFromXML[i];
        }
    }

    public String getName(int imageSet, int imageNum) {
        return cardImageNames[imageSet][imageNum];
    }
}