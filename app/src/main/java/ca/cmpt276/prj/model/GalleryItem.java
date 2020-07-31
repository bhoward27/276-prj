package ca.cmpt276.prj.model;

import java.io.File;

/**
 * This class stores all data related to an item within the RecyclerView in the Flickr list.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class GalleryItem {
	private String mCaption;
	private String mId;
	private String mUrl;

	public GalleryItem(){}

	public GalleryItem(File file) {
		mId = file.getName();
	}

	public String getCaption() {
		return mCaption;
	}

	public void setCaption(String caption) {
		mCaption = caption;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	@Override
	public String toString() {
		return mCaption;
	}
}
