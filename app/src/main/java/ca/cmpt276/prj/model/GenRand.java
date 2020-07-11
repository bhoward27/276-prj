package ca.cmpt276.prj.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GenRand {
	List<Integer> xList;
	List<Integer> yList;
	int width;
	int height;
	int maxX;
	int maxY;
	int num;

	public GenRand(int width, int height, int maxX, int maxY, int num) {
		xList = new ArrayList<>(num);
		yList = new ArrayList<>(num);
		this.width = width;
		this.height = height;
		this.maxX = maxX;
		this.maxY = maxY;
		this.num = num;
	}

	public void generate() {
		ThreadLocalRandom rand = ThreadLocalRandom.current();

		int xNew = rand.nextInt(0, maxX + 1);
		int yNew = rand.nextInt(0, maxY + 1);
		xList.add(xNew);
		yList.add(yNew);

		boolean overlap;
		for (int success = 1; success < num; success++) {
			xNew = rand.nextInt(0, maxX + 1);
			yNew = rand.nextInt(0, maxY + 1);
			overlap = false;
			for (int tries = 0; tries < 50; tries++) {
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
				// reset so it doesn't get stuck
				if (tries == 49) {
					Log.d("genRand", "generate: tries == 49");
					xList.clear();
					yList.clear();
					xList.add(xNew);
					yList.add(yNew);
					tries = 0;
				}
			}
			xList.add(xNew);
			yList.add(yNew);
		}

	}

	public List<Integer> getxList() {
		return xList;
	}

	public List<Integer> getyList() {
		return yList;
	}

	private boolean isOverlapping(int x1, int y1, int x2, int y2) {

		// If one rectangle is on left side of other
		if (x1 > x2+width || x2 > x1+width)
			return false;

		// If one rectangle is above other
		if (y1 > y2+height || y2 > y1+height)
			return false;

		return true;
	}
}
