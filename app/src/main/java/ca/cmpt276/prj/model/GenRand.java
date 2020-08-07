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

	private List<Double> rotatedWidths = new ArrayList<>();
	private List<Double> rotatedHeights = new ArrayList<>();
	private List<Integer> xMargins = new ArrayList<>();
	private List<Integer> yMargins = new ArrayList<>();
	private List<Rect> allRects = new ArrayList<>();
	private List<Rect> rectsAdded = new ArrayList<>();
	int maxX;
	int maxY;
	LocalFiles localFiles;
	Context ctx;
	OptionsManager optionsManager;
	Resources res;

	public GenRand(Context context, int maxX, int maxY) {
		this.ctx = context;
		this.maxX = maxX;
		this.maxY = maxY;

		localFiles = new LocalFiles(ctx, FLICKR_SAVED_DIR);
		optionsManager = OptionsManager.getInstance();
		res = ctx.getResources();
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
		int numImagesPerCard = imagesMap.size();
		int imageIndex = 0;
		for (int i : imagesMap) {
			double realW;
			double realH;
			double rotatedW;
			double rotatedH;
			// regular, non-flickr image setup (can be dynamic width/height)
			if (!card.isWord.get(imagesMap.indexOf(i))) {
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

				double rotatedRatio = getRotatedWHRatio(realW, realH, card.randRotations.get(imageIndex));
				double realRatio = realW / realH;

				if (rotatedRatio > outputRatio) { // if the image is wider than the card's rotatedRatio
					realH = (double) maxY / Math.log(numImagesPerCard * 20);
					realW = realRatio * realH;

					rotatedH = (double) maxY / Math.log(numImagesPerCard * 20);
					rotatedW = rotatedRatio * rotatedH;
				} else {
					realW = (double) maxX / Math.log(numImagesPerCard * 20);
					realH = (1.0 / realRatio) * realW;

					rotatedW = (double) maxX / Math.log(numImagesPerCard * 20);
					rotatedH = (1.0 / rotatedRatio) * rotatedW;
				}
				// scale image size (hard mode)
				realW *= card.randScales.get(imageIndex);
				realH *= card.randScales.get(imageIndex);

				rotatedW *= card.randScales.get(imageIndex);
				rotatedH *= card.randScales.get(imageIndex);

			} else {
				// make word buttons slightly bigger
				realW = (double) maxX / Math.log(numImagesPerCard * 10);
				realH = (double) realW / 1.5;

				rotatedW = maxX / Math.log(numImagesPerCard * 10);
				rotatedH = (double) rotatedW / 1.5;
			}

			rotatedWidths.add(rotatedW);
			rotatedHeights.add(rotatedH);

			card.imageWidths.add(realW);
			card.imageHeights.add(realH);
			imageIndex++;
		}
	}

	private void imagePlacements(Card card) {
		xMargins.clear();
		yMargins.clear();
		allRects.clear();
		rectsAdded.clear();

		ThreadLocalRandom rand = ThreadLocalRandom.current();

		for (int i = 0; i < rotatedWidths.size(); i++) {
			Rect rect = new Rect(0,
					0,
					(int) Math.round(rotatedWidths.get(i) + BUTTON_SPACING_PADDING),
					(int) Math.round(rotatedHeights.get(i) + BUTTON_SPACING_PADDING));
			allRects.add(rect);
		}

		boolean overlaps;
		int totalRetryCount = 0;
		for (int i = 0; i < allRects.size(); i++) {
			overlaps = false;
			Rect currRect = allRects.get(i);
			Rect newRect = new Rect(currRect);
			for (int tries = 0; tries < 100; tries++) {
				overlaps = false;
				newRect.offsetTo(rand.nextInt(0, maxX - currRect.width()),
						rand.nextInt(0, maxY - currRect.height()));
				for (Rect rA : rectsAdded) {
					if (Rect.intersects(newRect, rA)) {
						overlaps = true;
						break;
					}
				}

				if (!overlaps) break;
			}
			rectsAdded.add(newRect);

			xMargins.add(newRect.left);
			yMargins.add(newRect.top);

			// if we reached 25 tries already
			if (overlaps) {
				// safety: don't get stuck in infinite loop
				if (totalRetryCount > 500) {
					Log.d(TAG, "Couldn't find image placements");
					break;
					//throw new RuntimeException("GenRand: Can't find image placements");
				}
				rectsAdded.clear();
				xMargins.clear();
				yMargins.clear();
				i = -1;
				totalRetryCount++;
			}
		}

		rotatedWidths.clear();
		rotatedHeights.clear();

		card.leftMargins.addAll(xMargins);
		card.topMargins.addAll(yMargins);
	}

	// used to create a button that bounds the image with the right proportions
	// so that the rotated image isn't distorted at the end result
	// citation: https://stackoverflow.com/a/3869160
	private double getRotatedWHRatio(double width, double height, Double angleDeg) {
		double theta = angleDeg * (Math.PI/180);
		double x1 = -width/2,
				x2 = width/2,
				x3 = width/2,
				x4 = -width/2,
				y1 = height/2,
				y2 = height/2,
				y3 = -height/2,
				y4 = -height/2;

		double x11 = x1 * Math.cos(theta) + y1 * Math.sin(theta),
				y11 = -x1 * Math.sin(theta) + y1 * Math.cos(theta),
				x21 = x2 * Math.cos(theta) + y2 * Math.sin(theta),
				y21 = -x2 * Math.sin(theta) + y2 * Math.cos(theta),
				x31 = x3 * Math.cos(theta) + y3 * Math.sin(theta),
				y31 = -x3 * Math.sin(theta) + y3 * Math.cos(theta),
				x41 = x4 * Math.cos(theta) + y4 * Math.sin(theta),
				y41 = -x4 * Math.sin(theta) + y4 * Math.cos(theta);
		double x_min = Math.min(Math.min(x11,x21), Math.min(x31,x41)),
				x_max = Math.max(Math.max(x11,x21), Math.max(x31,x41));

		double y_min = Math.min(Math.min(y11,y21), Math.min(y31,y41)),
				y_max = Math.max(Math.max(y11,y21), Math.max(y31,y41));

		// return ratio
		return (x_max - x_min) / (y_max - y_min);
	}
}
