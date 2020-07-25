package ca.cmpt276.prj.model;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.util.Log;

import androidx.constraintlayout.solver.widgets.Rectangle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ca.cmpt276.prj.BuildConfig;

import static ca.cmpt276.prj.model.Constants.BUTTON_SPACING_PADDING;

/**
 * This class returns arrays of points which can construct non-overlapping rectangles (at random
 * positions)
 */
public class GenRand {
	private GenRand() {
	}

	public static List<List<Integer>> gen(double[] widths, double[] heights, int maxX, int maxY, List<Integer> imagesOnCard) {
		ThreadLocalRandom rand = ThreadLocalRandom.current();

		List<Integer> xList = new ArrayList<>();
		List<Integer> yList = new ArrayList<>();
		List<Rect> allRects = new ArrayList<>();
		List<Rect> rectsAdded = new ArrayList<>();

		for (int i : imagesOnCard) {
			Rect rect = new Rect(0,
					0,
					(int) Math.round(widths[i] + BUTTON_SPACING_PADDING),
					(int) Math.round(heights[i] + BUTTON_SPACING_PADDING));
			allRects.add(rect);
		}

		boolean overlaps;
		int totalRetryCount = 0;
		for (int i = 0; i < allRects.size(); i++) {
			overlaps = false;
			Rect currRect = allRects.get(i);
			Rect newRect = new Rect(currRect);
			for (int tries = 0; tries < 50; tries++) {
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

			xList.add(newRect.left);
			yList.add(newRect.top);

			// if we reached 25 tries already
			if (overlaps) {
				// safety: don't get stuck in infinite loop
				if (totalRetryCount > 50) {
					throw new RuntimeException("GenRand: Can't find image placements");
				}
				rectsAdded.clear();
				xList.clear();
				yList.clear();
				i = -1;
				totalRetryCount++;
			}
		}

		List<List<Integer>> finalList = new ArrayList<>();
		finalList.add(0, xList);
		finalList.add(1, yList);
		return finalList;
	}
}
