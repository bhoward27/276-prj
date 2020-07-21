package ca.cmpt276.prj.model;

public class ImageNameMatrix {
    private static String cardImageNames[][];
    private static ImageNameMatrix instance;

    public static ImageNameMatrix getInstance() {
        if (instance == null) {
            instance = new ImageNameMatrix();
        }
        return instance;
    }

    private ImageNameMatrix() { }

    //  In an activity, call getResources and store the xml string array into the singleton.
    //  On splash screen do this I guess.
}
