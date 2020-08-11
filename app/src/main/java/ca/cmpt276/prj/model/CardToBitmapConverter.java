package ca.cmpt276.prj.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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

import ca.cmpt276.prj.R;

import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.IMAGE_FOLDER_NAME;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;
import static ca.cmpt276.prj.model.Constants.LANDSCAPE_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.PREDATOR_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

public class CardToBitmapConverter {
    private Context context;
    private OptionsManager options;
    private List<Card> cards;

    //    these bitmaps must be the composite bitmaps of the entire card.
    private List<Bitmap> bitmaps;
    private List<String> fileNames;
    public static final String EXPORTED_CARD_PREFIX = "card" + RESOURCE_DIVIDER;
    public static final String DRAWABLE_FOLDER_PATH = "prj\\app\\src\\main\\res\\drawable";

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

    //  Enabling bilinear filtering will make for prettier images, but at an apparently small
    //  cost to performance.
    private static final boolean BILINEAR_FILTER_MODE = true;

    //  probably shouldn't be instantiated unless user has clicked on the export button.
    public CardToBitmapConverter(Context context) {
        options = OptionsManager.getInstance();
        this.context = context;
        setupCards();
        initFileNames();
        createBitmaps();
        saveBitmaps();
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
        String postfix = RESOURCE_DIVIDER + "t" + lastDigitsOfCurrentTime + JPG_EXTENSION;
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

    private Bitmap toBitmap(Card c) {
        //  Construct each subimage based on the specifications from the Card c.
        List<Bitmap> subImages = new ArrayList();
        List<Integer> imagesMap = c.getImagesMap();
        int numImages = imagesMap.size();
        List<Double> heights = c.getImageHeights();
        List<Double> widths = c.getImageWidths();
        List<Double> scalars = c.getRandScales();
        List<Boolean> isWord = c.getIsWord();

        ImageNameMatrix imageNames = ImageNameMatrix.getInstance();

        for (int i = 0; i < numImages; ++i) {
            int imageIndex = imagesMap.get(i);
            Bitmap bitmap;

            int width = widths.get(i).intValue();
            int height = heights.get(i).intValue();

            // shouldn't need the second clause here, but it doesn't hurt
            if (!isWord.get(i) || (options.getImageSet() >= FLICKR_IMAGE_SET)) {
                System.out.println("Iteration " + (i + 1) + ":");
                bitmap = createBitmapFromFile(imageIndex);
                // (SCALE)
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        (int) Math.round(width * scalars.get(i)),
                        (int) Math.round(height * scalars.get(i)),
                        BILINEAR_FILTER_MODE);
            } else {
                // SCALE by setting text size
                bitmap = createBitmapFromWord(imageNames.getName(options.getImageSet(), imageIndex),
                        width,
                        height,
                        scalars.get(i).floatValue());
            }
            subImages.add(bitmap);
        }
        return createComposite(subImages, c);
    }

    private Bitmap createComposite(List<Bitmap> subImages, Card card) {
        List<Double> rotations = card.getRandRotations();

        // would put x and y list as argument for makeOffsetCoordinates if we wanted to use margins.
        List<Integer> xPosList = card.getLeftMargins();
        List<Integer> yPosList = card.getTopMargins();

        Bitmap bgBitmap = Bitmap.createBitmap(WIDTH_IN_PX, HEIGHT_IN_PX, Bitmap.Config.ARGB_8888);
        bgBitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bgBitmap);

        int i = 0;
        for (Bitmap bmp : subImages) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);

            // matrix: ROTATE, then TRANSLATE (scaling was done previously to avoid text blurriness)
            Matrix imageMatrix = new Matrix();
            imageMatrix.preTranslate(xPosList.get(i), yPosList.get(i));
            imageMatrix.preRotate(rotations.get(i).floatValue(),
                    (float) bmp.getWidth()/2,
                    (float) bmp.getHeight()/2);

            // draw with matrix operations
            canvas.drawBitmap(bmp, imageMatrix, paint);

            // free up memory
            bmp.recycle();
            i++;
        }

        return bgBitmap;
    }

    private String getName(int imageIndex) {
        String imageSetPrefix = options.getImageSetPrefix();
        String resourcePrefix = imageSetPrefix + RESOURCE_DIVIDER;
        return resourcePrefix + imageIndex;
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) {
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

    //  Used IF we want to have margins for the composite bitmap.
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
                LocalFiles localFiles = new LocalFiles(context, FLICKR_SAVED_DIR);
                File file = localFiles.getFile(imageIndex);
                String path = file.getAbsolutePath();
                System.out.println("******* imageNum = " + imageIndex + "; PATH = " + path);

                /*
                CITATIONS - the line immediately below for decodeFile was based off these sources:
                    -   https://stackoverflow.com/a/9531548/10752685
                    -   https://developer.android.com/reference/android/graphics/BitmapFactory#decodeFile(java.lang.String,%20android.graphics.BitmapFactory.Options)
                */
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

    private Bitmap createBitmapFromWord(String name, int width, int height, float scale) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        bitmap.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas();

        /*
            CITATION - Code immediately beneath this comment for setting bitmap
            was based off of this: https://stackoverflow.com/a/11437439/10752685
         */
        canvas.setBitmap(bitmap);

        Paint paint = new Paint();
        paint.setDither(true);

        paint.setARGB(255, 0, 0, 0);
        paint.setTextAlign(Paint.Align.CENTER);

        // hardcoded a 2 here because the text can get a bit blurry
        paint.setTextSize(( 2 + context.getResources().getDimension(R.dimen.button_text_size))
                                                                                        * scale);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        // centre text
        // CITATION: https://stackoverflow.com/a/11121873
        int xPos = (canvas.getWidth() / 2);

        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the
        // center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(name, xPos, yPos, paint);
        return bitmap;
    }

    private void verifyNotNull(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IOError(new IOException("Error: Failed to decode the file into a bitmap."));
        }
    }

    private void createBitmaps() {
        //  would be neat to have the last image in bitmaps be an image for the back of a card.
        bitmaps = new ArrayList<>();
        for (Card c : cards) {
            bitmaps.add(toBitmap(c));
        }
    }

    private void saveBitmaps() {
        int numImages = fileNames.size();
        for (int i = 0; i < numImages; ++i) {
            saveImage(bitmaps.get(i), fileNames.get(i));
        }
    }

    private void setupCards() {
        Game game = new Game();
        cards = game.getDeck().getAllCards();
        // TODO: can use margins if we want to
        GenRand rand = new GenRand(context, WIDTH_IN_PX, HEIGHT_IN_PX);
        // randomize
        for (Card c : cards) {
            rand.gen(c);
        }
    }
}
