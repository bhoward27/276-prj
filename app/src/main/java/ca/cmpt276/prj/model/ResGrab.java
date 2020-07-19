package ca.cmpt276.prj.model;

import android.content.Context;

public class ResGrab {
	private ResGrab() {}

	public static int getID(Context context, int imageSet, int index) {
		int resourceID = 0;

		return resourceID;
	}

	public static String getWord(Context context, int imageSet, int index) {
		int ID = getID(context, imageSet, index);
		String imageWord = context.getResources().getResourceName(ID);
		String[] imageNameParts = imageWord.split("_");
		// this part of the imageWord is the word
		imageWord = imageNameParts[imageNameParts.length-1];

		return imageWord;
	}

	public static String getAlpha(int index) {
		return index >= 0 && index < 26 ? String.valueOf((char)(index + 97)) : null;
	}

}
