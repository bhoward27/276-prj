package ca.cmpt276.prj.model;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ca.cmpt276.prj.BuildConfig;

/**
 * This class returns arrays of points which can construct non-overlapping rectangles (at random
 * positions)
 */
public class GenRand {
	private GenRand() {
	}

	public static List<List<Double>> gen(List<Double> widths, List<Double> heights, double maxX, double maxY, int numImages, int offset) {
		List<Double> xList = new ArrayList<>();
		List<Double> yList = new ArrayList<>();
		ThreadLocalRandom rand = ThreadLocalRandom.current();

		double xNew = rand.nextDouble(0, maxX + 1 - widths.get(0 + offset));
		double yNew = rand.nextDouble(0, maxY + 1 - heights.get(0 + offset));
		xList.add(xNew);
		yList.add(yNew);

		boolean overlap;
		// Safety for possible infinite loop
		int overallRetries = 0;
		for (int imageI = 1; imageI < numImages; imageI++) {

			xNew = rand.nextDouble(0, maxX + 1 - widths.get(imageI + offset));
			yNew = rand.nextDouble(0, maxY + 1 - heights.get(imageI + offset));
			overlap = false;
			for (int tries = 0; overallRetries < 10; tries++) {
				for (Double x : xList) {
					if (overlap) break;
					for (Double y : yList) {
						double width = widths.get(xList.indexOf(x) + offset);
						double height = heights.get(yList.indexOf(y) + offset);
						double newWidth = widths.get(imageI + offset);
						double newHeight = heights.get(imageI + offset);

						overlap = isOverlapping(x, width, y, height, xNew, newWidth, yNew, newHeight);
						if (overlap) {
							xNew = rand.nextDouble(0, maxX + 1 - widths.get(imageI + offset));
							yNew = rand.nextDouble(0, maxY + 1 - heights.get(imageI + offset));
							break;
						}
					}
				}
				if (!overlap) break;
				overlap = false;

				// Reset so it doesn't get stuck unable to find valid placements
				if (tries >= 50) {
					xList.clear();
					yList.clear();
					xNew = rand.nextDouble(0, maxX + 1 - widths.get(imageI + offset));
					yNew = rand.nextDouble(0, maxY + 1 - heights.get(imageI + offset));
					xList.add(xNew);
					yList.add(yNew);
					tries = 0;
					imageI = 1;
					overallRetries++;
				}
			}
			xList.add(xNew);
			yList.add(yNew);
		}

		if (overallRetries >= 10) {
			Log.d("GenRand", "Something broke.");
			throw new UnknownError("too many retries");
		}
		if (BuildConfig.DEBUG && xList.size() != numImages || yList.size() != numImages) {
			throw new AssertionError("Assertion failed");
		}

		List<List<Double>> finalList = new ArrayList<>();
		finalList.add(0, xList);
		finalList.add(1, yList);
		return finalList;
	}

	private static boolean isOverlapping(double x1, double x1width, double y1, double y1height, double x2, double x2width, double y2, double y2height) {
		// Check if overlapping horizontally
		if (x1 > x2+x2width || x2 > x1+x1width)
			return false;

		// Check if overlapping vertically
		if (y1 > y2+y2height || y2 > y1+y1height)
			return false;

		return true;
	}
}
