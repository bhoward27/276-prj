package ca.cmpt276.prj.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.IMAGE_FOLDER_NAME;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.PREDATOR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

public class CardToBitmapConverter {
    private Context context;
    private OptionsManager options;
    private Game game; //   Not sure if this should be member or just a local variable in a function.
    private GenRand rand; //    Not sure if should be member.
    private List<Card> cards;
    private List<Bitmap> bitmaps; //    these bitmaps must be the final bitmaps of the entire card.

    //  names of the image files which can be passed to Michael's file manager.
    private List<String> fileNames;
    public static final String EXPORTED_CARD_PREFIX = "card" + RESOURCE_DIVIDER;
    public static final String DRAWABLE_FOLDER_PATH = "prj\\app\\src\\main\\res\\drawable";

    //  Values of height and width would be reversed for a typical playing card; however,
    //  this would cause a lot of extra work for me because the in-game cards are in a landscape
    //  orientation. So I am keeping the cards at that orientation (i.e., the exported card
    //  will be wider than it is tall, like in the actual game).

    //  Dimensions of a card in pixels and inches:
    private static final double HEIGHT_IN_INCHES = 2.5;
    private static final double WIDTH_IN_INCHES = 3.5;
    private static final double MARGIN_IN_INCHES = 3.0 / 8;
    private static final int PPI = 200; //  pixels per inch
    private static final int HEIGHT_IN_PX = (int) (HEIGHT_IN_INCHES * PPI); //  px = pixels
    private static final int WIDTH_IN_PX = (int) (WIDTH_IN_INCHES * PPI);
    private static final int MARGIN_IN_PX = (int) (MARGIN_IN_INCHES * PPI);

    //  the inner height and width is referring to the smaller space in the card where
    //  images can go. It's the space "inside the margins".
    private static final int INNER_HEIGHT_IN_PX = HEIGHT_IN_PX - (2 * MARGIN_IN_PX);
    private static final int INNER_WIDTH_IN_PX = WIDTH_IN_PX - (2 * MARGIN_IN_PX);

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;

    //  Enabling bilinear filtering will make for prettier images, but at an apparently small
    //  cost to performance.
    private static final boolean BILINEAR_FILTER_MODE = true;

    //  probably shouldn't be instantiated unless user has clicked on the export button.
    public CardToBitmapConverter(Context context) {
        options = options.getInstance();
        game = new Game();
        this.context = context;
        rand = new GenRand(context, INNER_WIDTH_IN_PX, INNER_HEIGHT_IN_PX);
        setupCards();
        initFileNames();
        createBitmaps();
    }

    private void initFileNames() {
        /*
            Admittedly, getting the system time and appending to a filename is an odd thing to do,
            and it's not 100% fool proof. The "correct" way of doing it would simply be to have
            the converter keep track of how many times it's exported stuff.
            However, then the converter would have to be a singleton and bla bla.
            I figured, it's easier for us as programmers to just do it this way.
            I guess one fix would be to ENSURE uniqueness by actually checking the file names
            on the system and generating a new name if one's already taken.
            But I would say this is low priority right now.
         */
        String lastDigitsOfCurrentTime = toLastDigits(getSystemTime(), 6);
        String postfix = RESOURCE_DIVIDER + "t" + lastDigitsOfCurrentTime;
        fileNames = new ArrayList<>();
        for (int i = 0; i < cards.size(); ++i) {
            fileNames.add(EXPORTED_CARD_PREFIX + i + postfix);
        }
    }

    private long getSystemTime() {
        //  CITATION: the code for this method is based off various sources:
        //  -   https://stackoverflow.com/a/5369753/10752685
        //  -   https://developer.android.com/reference/java/util/Calendar.html
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * @return the last/rightmost digits of a number, as a string.
     */
    private String toLastDigits(long n, int numDigits) {
        if (numDigits < 1 || numDigits > 100) {
            throw new IllegalArgumentException("Error: Invalid numDigits argument." +
                    " numDigits must be in range [1, 100]");
        }
        if (n < Math.pow(10, numDigits - 1)) {
            throw new IllegalArgumentException("Error: Invalid numDigits argument." +
                    " numDigits must not be greater than the amount of digits in n.");
        }
        String lastDigits = "";
        int digit;
        int reverseDigits[] = new int[numDigits];
        for (int i = 0; i < numDigits; ++i) {
            digit = (int) n % 10;
            reverseDigits[i] = digit;
            n /= 10;
        }
        for (int i = numDigits - 1; i >= 0; --i) {
            lastDigits += reverseDigits[i];
        }
        return lastDigits;
    }

    //  Code under construction
    private Bitmap toBitmap(Card c) {
        List<Bitmap> subImages = new ArrayList();
        List<Integer> imagesMap = c.getImagesMap();
        int numImages = imagesMap.size();
        //  Construct list of subImages.
        //  Constructs each subimage based on the specifications from the Card c.
        List<Double> heights = c.getImageHeights();
        List<Double> widths = c.getImageWidths();
        List<Boolean> wordConditions = c.getIsWord();
        List<Integer> xCoordinates = makeOffsetCoordinates(c.getLeftMargins());
        List<Integer> yCoordinates = makeOffsetCoordinates(c.getTopMargins());
        for (int i = 0; i < numImages; ++i) {
            /*
                Some things that would make sense to do here:
                    -load the correct image into a bitmap (WHAT IF IT'S A WORD AND NOT AN IMAGE?)
                    -scale the bitmap
                    -apply rotations. how does this work for the bitmap? will it create empty
                        space / enlarge the dimensions to accomodate the rotation?
                    -create the correct coordinates
                        Does/should rotation affect the coordinates???
             */
            boolean isWord = wordConditions.get(i);
            if (isWord) {
                //  yet to implement.
                System.out.println("isWord == true");
                System.out.println("Iteration " + (i + 1) + ":");
            }
            else {
                /*
                    CITATION - I didn't know how to cast a Double (the wrapper class) to int before
                    reading this:
                    https://www.geeksforgeeks.org/convert-double-to-integer-in-java/
                */

                /*
                    CITATIONS:
                        -   https://stackoverflow.com/a/11437439/10752685
                        -   https://stackoverflow.com/a/9531548/10752685
                        -   https://developer.android.com/reference/android/graphics/BitmapFactory#decodeFile(java.lang.String,%20android.graphics.BitmapFactory.Options)
                */
                int imageIndex = imagesMap.get(i);
                System.out.println("Iteration " + (i + 1) + ":");
                Bitmap bitmap = createBitmapFromFile(imageIndex);

                /*
                    CITATION - The following line of code for adjusting the size of the bitmap
                    came from here: https://gamedev.stackexchange.com/a/59483
                 */
                //  -   Change the width and height
                int height = heights.get(i).intValue();
                int width = widths.get(i).intValue();
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, BILINEAR_FILTER_MODE);

                //  At the moment the canvas is useless, but it may be needed in future code
                //  especially for the word + images mode.
                Canvas canvas = new Canvas();
                bitmap = getMutableCopy(bitmap);
                canvas.setBitmap(bitmap);

                //  Make all canvas-related modifications to the bitmap.

                subImages.add(bitmap);
            }
        }
        //  DELETE --- only for testing.
        testSubBitmaps(c, subImages);
        Bitmap compositeBitmap = null;
        return compositeBitmap;
        //return createComposite(subImages);
    }

    private void testSubBitmaps(Card c, List<Bitmap> subImages) {
        int numBitmaps = subImages.size();
        List<Integer> imageIndices = c.getImagesMap();
        for (int i = 0; i < numBitmaps; ++i) {
            testSaveImage(subImages.get(i), testGetName(imageIndices.get(i)));
        }
    }

    private String testGetName(int imageIndex) {
        String imageSetPrefix = options.getImageSetPrefix();
        String resourcePrefix = imageSetPrefix + RESOURCE_DIVIDER;
        return resourcePrefix + imageIndex;
    }

    private void testSaveImage(Bitmap bitmap, @NonNull String name) {
        OutputStream fos = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File image = new File(imagesDir, name + ".jpg");
            try {
                fos = new FileOutputStream(image);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        try {
            Objects.requireNonNull(fos).close();
        } catch (IOException e){
            e.printStackTrace();
        }
        Log.e("SUCCESS?", "YES!");
    }

    private List<Integer> makeOffsetCoordinates(List<Integer> coordinates) {
        List<Integer> adjustedCoordinates = new ArrayList<>();
        for (Integer coordinate : coordinates) {
            Integer offsetCoordinate = coordinate + MARGIN_IN_PX;
            adjustedCoordinates.add(offsetCoordinate);
        }
        return adjustedCoordinates;
    }

    /**
     * @param imageIndex the index of the image as it corresponds to the particular folder
     *                 (e.g., if it's landscape set and imageNum = 2, then this corresponds with
     *                 the file a_2.jpg.)
     * @return an immutable bitmap that is a copy of the jpg image.
     */
    private Bitmap createBitmapFromFile(int imageIndex) {
        int imageSet = options.getImageSet();
        Bitmap bitmap;
        switch(imageSet) {
            case LANDSCAPE_IMAGE_SET:
                //  fall-through intentional
            case PREDATOR_IMAGE_SET:
                //  Get the resource ID of the picture
                //  (copied from GameActivity code)
                String imageSetPrefix = options.getImageSetPrefix();
                String resourcePrefix = imageSetPrefix + RESOURCE_DIVIDER;
                String resourceName = resourcePrefix + imageIndex;
                Resources globalResources = context.getResources();
                int resourceID = globalResources.getIdentifier(resourceName,
                        IMAGE_FOLDER_NAME, context.getPackageName());

                //  load the picture into a bitmap.
                bitmap = BitmapFactory.decodeResource(globalResources, resourceID);
                break;
            case FLICKR_IMAGE_SET:
                //  Not sure if I should use the other constructor since I'm not actually
                //  in an activity.
                LocalFiles localFiles = new LocalFiles(context, FLICKR_SAVED_DIR);
                File file = localFiles.getFile(imageIndex);
                String path = file.getAbsolutePath();
                System.out.println("******* imageNum = " + imageIndex + "; PATH = " + path);
                bitmap = BitmapFactory.decodeFile(path);
                break;
            default:
                throw new IllegalArgumentException("Error: Invalid imageSet. " +
                        "imageSet must be in the" +
                        " following range: [" + LANDSCAPE_IMAGE_SET + ", "
                        + FLICKR_IMAGE_SET + "].");
        }
        verifyNotNull(bitmap);
        return bitmap;
    }

    private Bitmap getMutableCopy(Bitmap bitmap) {
        /*
            CITATION - The line of code immediately below comes from here:
                https://stackoverflow.com/a/19325732/10752685

            The bitmap must be mutable in order to be set to the Canvas (because the canvas will
            modify the bitmap as its drawn to) else an IllegalStateException will be thrown.
         */
        return bitmap.copy(BITMAP_CONFIG, true);
    }

    private void verifyNotNull(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IOError(new IOException("Error: Failed to decode the file into a bitmap."));
        }
    }

    private void scale(Card c, Bitmap image, int imageNum) {
        List<Double> scalars = c.getRandScales();
        List<Double> heights = c.getImageHeights();
        List<Double> widths = c.getImageWidths();
        double scalar = scalars.get(imageNum);

        int height = scale(scalar, heights.get(imageNum));
        int width = scale(scalar, widths.get(imageNum));
        //  Not sure if this is necessary at all.
    }

    private int scale(double scalar, double length) {
        return (int) (scalar * length);
    }

    private void rotate(Bitmap image) {

    }

    //  Code under construction
    //  puts the sub images into a composite image to make the full card picture.
//    private Bitmap createComposite(List<Bitmap> subImages) {
//        /*
//
//         */
//    }

    private void createBitmaps() {
        //  would be neat to have the last image in bitmaps be an image for the back of a card.
        bitmaps = new ArrayList<>();
        for (Card c : cards) {
            bitmaps.add(toBitmap(c));
        }
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    private void setupCards() {
        cards = game.getDeck().getAllCards();
        for (Card c : cards) {
            rand.gen(c);
        }
    }
}
