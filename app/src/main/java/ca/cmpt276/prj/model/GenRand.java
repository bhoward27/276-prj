package ca.cmpt276.prj.model;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static ca.cmpt276.prj.model.Constants.BUTTON_SPACING_PADDING;

/**
 * This class returns arrays of points which can construct non-overlapping rectangles (at random
 * positions)
 */
public class GenRand {
	private List<Integer> xMargins = new ArrayList<>();
	private List<Integer> yMargins = new ArrayList<>();
	private List<Rect> allRects = new ArrayList<>();
	private List<Rect> rectsAdded = new ArrayList<>();

	public GenRand() { }

	public List<Integer> getXMargins() {
		return xMargins;
	}

	public List<Integer> getYMargins() {
		return yMargins;
	}

	public void gen(List<Double> widths, List<Double> heights, int maxX, int maxY) {
		xMargins.clear();
		yMargins.clear();
		allRects.clear();
		rectsAdded.clear();

		ThreadLocalRandom rand = ThreadLocalRandom.current();

		for (int i = 0; i < widths.size(); i++) {
			Rect rect = new Rect(0,
					0,
					(int) Math.round(widths.get(i) + BUTTON_SPACING_PADDING),
					(int) Math.round(heights.get(i) + BUTTON_SPACING_PADDING));
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

			xMargins.add(newRect.left);
			yMargins.add(newRect.top);

			// if we reached 25 tries already
			if (overlaps) {
				// safety: don't get stuck in infinite loop
				if (totalRetryCount > 50) {
					throw new RuntimeException("GenRand: Can't find image placements");
				}
				rectsAdded.clear();
				xMargins.clear();
				yMargins.clear();
				i = -1;
				totalRetryCount++;
			}
		}
	}
}
