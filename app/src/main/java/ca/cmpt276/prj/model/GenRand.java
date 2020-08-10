package ca.cmpt276.prj.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static ca.cmpt276.prj.model.Constants.*;

/**
 * This class calculates the widths/heights and random placements for the images on a card and
 * writes them to the card.
 */
public class GenRand {
	private final String TAG = "GenRand";

	int maxX;
	int maxY;
	int numImagesPerCard;
	LocalFiles localFiles;
	Context ctx;
	OptionsManager optionsManager;
	Resources res;

	public GenRand(Context context, int maxX, int maxY) {
		this.ctx = context;
		this.maxX = maxX;
		this.maxY = maxY;

		this.localFiles = new LocalFiles(ctx, FLICKR_SAVED_DIR);
		this.optionsManager = OptionsManager.getInstance();
		this.res = ctx.getResources();

		this.numImagesPerCard = optionsManager.getOrder() + 1;
	}

	public void gen(Card card) {
		imageSizes(card);
		imagePlacements(card);
	}

	private void imageSizes(Card card) {
		if (!(card.imageHeights.isEmpty() || card.imageWidths.isEmpty() ||
				card.topMargins.isEmpty() || card.leftMargins.isEmpty())) {
			Log.d(TAG, "gen: you are trying to write to a card that has already" +
					" been written to.");
			throw new Error("rewriting to card in GenRand");
		}

		double outputRatio = (double) maxX / maxY;
		List<Integer> imagesMap = card.getImagesMap();

		// scale and fit images, get rotated rectangles
		for (int i : imagesMap) {
			double realW;
			double realH;
			double ratio;

			if (!card.isWord.get(imagesMap.indexOf(i))) {
				// get the image widths and heights
				if (optionsManager.getImageSet() < FLICKR_IMAGE_SET) {
					Drawable image;
					String resourceName = optionsManager.getImageSetPrefix() + RESOURCE_DIVIDER + i;
					int resourceID = res.getIdentifier(resourceName, IMAGE_FOLDER_NAME,
							ctx.getPackageName());
					image = ctx.getDrawable(resourceID);
					if (image == null) {
						throw new Error("The image was null.");
					}
					realW = image.getIntrinsicWidth();
					realH = image.getIntrinsicHeight();
				} else {
					File image = localFiles.getFile(i);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(image.getAbsolutePath(), options);
					realW = options.outWidth;
					realH = options.outHeight;
				}

				ratio = realW / realH;

				if (ratio > outputRatio) { // if the image is wider than the card's ratio...
					// set size based on its relation to the height
					realH = (double) maxY / Math.log(numImagesPerCard * 25);
					realW = ratio * realH;
				} else { // else set size based on its relation to the width
					realW = (double) maxX / Math.log(numImagesPerCard * 25);
					realH = (1.0 / ratio) * realW;
				}

			} else {
				// make word buttons slightly bigger and long
				realW = maxX / Math.log(numImagesPerCard * 7);
				realH = realW / 1.7;
			}

			card.imageWidths.add(realW);
			card.imageHeights.add(realH);
		}
	}
	
	private void imagePlacements(Card card) {
		ThreadLocalRandom rand = ThreadLocalRandom.current();

		List<Integer> xMargins = new ArrayList<>();
		List<Integer> yMargins = new ArrayList<>();

		List<Double> cardRotations = new ArrayList<>();
		List<Double> cardScales = new ArrayList<>();

		// add random rotations and scaling, for difficulty
		double rotateBound = 0;
		double scaleLowerBound = 1;
		double scaleUpperBound = 1;

		if (optionsManager.getDifficulty() > EASY) {
			rotateBound = 360;
			if (optionsManager.getDifficulty() > MEDIUM) {
				scaleLowerBound = SCALE_LOWER_BOUND;
				scaleUpperBound = SCALE_UPPER_BOUND;
			}
		}

		double randScale = scaleLowerBound;
		double randRotate = rotateBound;
		int randOffsetX = 0;
		int randOffsetY = 0;

		List<Rect> allRects = new ArrayList<>();
		List<Rect> rectsAdded = new ArrayList<>();

		for (int i = 0; i < numImagesPerCard; i++) {
			Rect rect = new Rect(0,
					0,
					(int) Math.round(card.imageWidths.get(i)),
					(int) Math.round(card.imageHeights.get(i)));
			allRects.add(rect);
		}

		Rect cardRect = new Rect(0, 0, maxX, maxY);

		boolean overlaps;
		int totalRetryCount = 0;
		for (int i = 0; i < allRects.size(); i++) {
			overlaps = false;
			Rect currRect = allRects.get(i);
			Rect newRect = new Rect(currRect);

			for (int tries = 0; tries < 100; tries++) {
				overlaps = false;
				newRect = new Rect(currRect);

				// avoid getting nextDouble with equal upper and lower bounds (difficulty dependent)
				randScale =
						scaleLowerBound == scaleUpperBound
								? 1
								: rand.nextDouble(scaleLowerBound, scaleUpperBound);
				randRotate =
						rotateBound == 0
								? 0
								: rand.nextDouble(0, rotateBound);

				// resize
				newRect.right *= randScale;
				newRect.bottom *= randScale;

				randOffsetX = rand.nextInt(0, maxX - newRect.width());
				randOffsetY = rand.nextInt(0, maxY - newRect.height());

				// reposition
				newRect.offsetTo(randOffsetX, randOffsetY);

				// rotate (get rotated bounding rectangle)
				newRect = getRotatedBoundingRect(newRect, randRotate);

				for (Rect rA : rectsAdded) {
					// check if intersecting with already placed, or if out of bounds
					if (Rect.intersects(newRect, rA) || !cardRect.contains(newRect)) {
						overlaps = true;
						break;
					}
				}

				if (!overlaps) break;
			}

			rectsAdded.add(newRect);

			// these transformations resulted in non-overlapping rectangles so far,
			// so add them to the lists
			xMargins.add(randOffsetX);
			yMargins.add(randOffsetY);
			cardRotations.add(randRotate);
			cardScales.add(randScale);

			// if we reached 100 tries already
			if (overlaps) {
				// safety: don't get stuck in infinite loop (this is a bruteforce algorithm after all)
				if (totalRetryCount > 500000) {
					Log.d(TAG, "Couldn't find image placements");
					throw new RuntimeException("GenRand: Can't find image placements");
				}
				// then restart with a fresh set of rectangles
				rectsAdded.clear();
				xMargins.clear();
				yMargins.clear();
				cardRotations.clear();
				cardScales.clear();
				i = -1;
				totalRetryCount++;
			}
		}

		card.leftMargins.addAll(xMargins);
		card.topMargins.addAll(yMargins);
		card.randRotations.addAll(cardRotations);
		card.randScales.addAll(cardScales);
	}

	// used to create a button that bounds the image with the right proportions
	// so that the rotated image isn't distorted at the end result
	// using code from: https://stackoverflow.com/a/3869160
	private Rect getRotatedBoundingRect(Rect orig, Double angleDeg) {
		double radians = angleDeg * (Math.PI/180);

		double width = orig.width();
		double height = orig.height();

		double x1 = -width/2,
				x2 = width/2,
				x3 = width/2,
				x4 = -width/2,
				y1 = height/2,
				y2 = height/2,
				y3 = -height/2,
				y4 = -height/2;

		double x11 = x1 * Math.cos(radians) + y1 * Math.sin(radians),
				y11 = -x1 * Math.sin(radians) + y1 * Math.cos(radians),
				x21 = x2 * Math.cos(radians) + y2 * Math.sin(radians),
				y21 = -x2 * Math.sin(radians) + y2 * Math.cos(radians),
				x31 = x3 * Math.cos(radians) + y3 * Math.sin(radians),
				y31 = -x3 * Math.sin(radians) + y3 * Math.cos(radians),
				x41 = x4 * Math.cos(radians) + y4 * Math.sin(radians),
				y41 = -x4 * Math.sin(radians) + y4 * Math.cos(radians);

		double x_min = Math.min(Math.min(x11,x21), Math.min(x31,x41)),
				x_max = Math.max(Math.max(x11,x21), Math.max(x31,x41));

		double y_min = Math.min(Math.min(y11,y21), Math.min(y31,y41)),
				y_max = Math.max(Math.max(y11,y21), Math.max(y31,y41));

		// move back to original coordinates
		x_min += orig.centerX();
		x_max += orig.centerX();
		y_min += orig.centerY();
		y_max += orig.centerY();

		return new Rect((int) Math.round(x_min),
				(int) Math.round(y_min),
				(int) Math.round(x_max),
				(int) Math.round(y_max));
	}
}
