package ca.cmpt276.prj.model;

import java.io.File;
import java.util.Comparator;

import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * This is a helper class used for sorting in ascending order of downloaded Flickr file names
 *
 */

public class SortByFileName implements Comparator<File> {
	public int compare(File a, File b)
	{
		String aName = a.getName();
		String bName = b.getName();
		int aInt = Integer.parseInt(aName.substring(2, aName.length()-JPG_EXTENSION.length()));
		int bInt = Integer.parseInt(bName.substring(2, bName.length()-JPG_EXTENSION.length()));

		return aInt - bInt;
	}
}
