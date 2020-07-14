package ca.cmpt276.prj.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class returns arrays of points which can construct non-overlapping rectangles (at random
 * positions)
 */
public class GenRand {
	List<Integer> xList;
	List<Integer> yList;
	int width;
	int height;
	int maxX;
	int maxY;
	int num;
	boolean failed;

	public GenRand(int width, int height, int maxX, int maxY, int num) {
		xList = new ArrayList<>(num);
		yList = new ArrayList<>(num);
		this.width = width;
		this.height = height;
		this.maxX = maxX;
		this.maxY = maxY;
		this.num = num;
		failed = false;

		generate();
	}

	public boolean isFailed() {
		return failed;
	}

	public List<Integer> getxList() {
		return xList;
	}

	public List<Integer> getyList() {
		return yList;
	}

	private void generate() {
		ThreadLocalRandom rand = ThreadLocalRandom.current();

		int xNew = rand.nextInt(0, maxX + 1);
		int yNew = rand.nextInt(0, maxY + 1);
		xList.add(xNew);
		yList.add(yNew);

		boolean overlap;
		// Safety for possible infinite loop
		int overallRetries = 0;
		for (int succesfullyPlacedImages = 1; succesfullyPlacedImages < num; succesfullyPlacedImages++) {

			xNew = rand.nextInt(0, maxX + 1);
			yNew = rand.nextInt(0, maxY + 1);
			overlap = false;
			for (int tries = 0; overallRetries < 10; tries++) {
				for (Integer x : xList) {
					if (overlap) break;
					for (Integer y : yList) {
						overlap = isOverlapping(x, y, xNew, yNew);
						if (overlap) {
							xNew = rand.nextInt(0, maxX + 1);
							yNew = rand.nextInt(0, maxY + 1);
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
					xList.add(xNew);
					yList.add(yNew);
					tries = 0;
					succesfullyPlacedImages = 1;
					overallRetries++;
				}
			}
			xList.add(xNew);
			yList.add(yNew);
		}

		if (overallRetries >= 10) {
			failed = true;
		}
	}

	private boolean isOverlapping(int x1, int y1, int x2, int y2) {
		// Check if overlapping horizontally
		if (x1 > x2+width || x2 > x1+width)
			return false;

		// Check if overlapping vertically
		if (y1 > y2+height || y2 > y1+height)
			return false;

		return true;
	}
}
