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
	private List<Integer> xMargins = new ArrayList<>();
	private List<Integer> yMargins = new ArrayList<>();
	private List<Rect> allRects = new ArrayList<>();
	private List<Rect> rectsAdded = new ArrayList<>();

	public GenRand() {
	}

	public void gen(Context ctx, Card card, int maxX, int maxY) {
		if (!(card.imageHeights.isEmpty() || card.imageWidths.isEmpty() ||
				card.topMargins.isEmpty() || card.leftMargins.isEmpty())) {
			Log.d("GenRand", "gen: you are trying to write to a card that has already" +
					" been written to.");
			throw new Error("rewriting to card in GenRand");
		}

		LocalFiles localFiles = new LocalFiles(ctx, FLICKR_SAVED_DIR);
		OptionsManager optionsManager = OptionsManager.getInstance();
		Resources res = ctx.getResources();

		double outputRatio = (double) maxX / maxY;
		List<Integer> imagesMap = card.getImagesMap();
		int numImagesPerCard = imagesMap.size();
		for (int i : imagesMap) {
			double w;
			double h;
			// regular, non-flickr image setup (can be dynamic width/height)
			if (!card.isWord.get(imagesMap.indexOf(i))) {
				double ratio;
				if (optionsManager.getImageSet() < FLICKR_IMAGE_SET) {
					Drawable image;
					String resourceName = optionsManager.getImageSetPrefix() + RESOURCE_DIVIDER + i;
					Log.d("GenRand", "gen: " + resourceName);
					int resourceID = res.getIdentifier(resourceName, IMAGE_FOLDER_NAME,
							ctx.getPackageName());
					image = ctx.getDrawable(resourceID);
					if (image == null) {
						throw new Error("The image was null.");
					}
					ratio = (double) image.getIntrinsicWidth() / image.getIntrinsicHeight();
				} else {
					File image = localFiles.getFile(i);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(image.getAbsolutePath(), options);
					ratio = (double) options.outWidth / options.outHeight;
				}

				if (ratio > outputRatio) { // if the image is wider than the card's ratio
					h = (double) maxY / Math.log(numImagesPerCard * 20);
					w = ratio * h;
				} else {
					w = (double) maxX / Math.log(numImagesPerCard * 20);
					h = (1.0 / ratio) * w;
				}
			} else {
				// make word buttons slightly bigger
				w = maxX / Math.log(numImagesPerCard * 10);
				h = (double) w / 1.5;
			}

			card.imageWidths.add(w);
			card.imageHeights.add(h);
		}

		xMargins.clear();
		yMargins.clear();
		allRects.clear();
		rectsAdded.clear();

		ThreadLocalRandom rand = ThreadLocalRandom.current();

		for (int i = 0; i < card.imageWidths.size(); i++) {
			Rect rect = new Rect(0,
					0,
					(int) Math.round(card.imageWidths.get(i) + BUTTON_SPACING_PADDING),
					(int) Math.round(card.imageHeights.get(i) + BUTTON_SPACING_PADDING));
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
				if (totalRetryCount > 100) {
					throw new RuntimeException("GenRand: Can't find image placements");
				}
				rectsAdded.clear();
				xMargins.clear();
				yMargins.clear();
				i = -1;
				totalRetryCount++;
			}
		}

		card.leftMargins.addAll(xMargins);
		card.topMargins.addAll(yMargins);
	}
}
