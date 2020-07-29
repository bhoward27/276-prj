package ca.cmpt276.prj.model;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_NAME_PREFIX;
import static ca.cmpt276.prj.model.Constants.FLICKR_PENDING_DIR;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * Reorganizes image files in the download folders to be compatible with the GameActivity and also
 * to be able to delete the files from the Fragment.
 */
public class FlickrFoldrImageRenamr {
	private FlickrFoldrImageRenamr() {
	}

	public static void moveAndRenameTemp(Context ctx) {
		OptionSet options = OptionSet.getInstance();
		File preDirectory = Objects.requireNonNull(ctx)
				.getDir(FLICKR_PENDING_DIR, Context.MODE_PRIVATE);
		File postDirectory = Objects.requireNonNull(ctx)
				.getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE);
		int numUserImages = Objects.requireNonNull(preDirectory.listFiles()).length;
		if (numUserImages > 0) {
			int index = Objects.requireNonNull(postDirectory.listFiles()).length;
			for (String imageName : options.getPossibleFlickrImageNames()) {
				File myImageFile = new File(preDirectory,
						imageName);
				File destRenamedFile = new File(postDirectory,
						FLICKR_IMAGE_NAME_PREFIX + index + JPG_EXTENSION);
				if (myImageFile.renameTo(destRenamedFile)) {
					Log.d("renameImage", "image " + imageName + " has been renamed to " +
							destRenamedFile);
					index++;
				}
			}
			options.clearPossibleFlickrImageNames();
		}
		options.setFlickrImageSetSize(Objects.requireNonNull(postDirectory.listFiles()).length);
	}

	public static void makeFileNamesConsistent(Context ctx) {
		OptionSet options = OptionSet.getInstance();
		File directory = Objects.requireNonNull(ctx)
				.getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE);
		// create consistent increasing naming scheme
		int index = 0;
		List<File> fileList = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
		Collections.sort(fileList, new SortByFileName());
		for (File fileName : fileList) {
			Log.d("renameImage", "fileName: " + fileName);
			File newFileName = new File(directory,
					FLICKR_IMAGE_NAME_PREFIX + index + JPG_EXTENSION);
			if (fileName.renameTo(newFileName)) {
				Log.d("renameImage", "image " + fileName + " has been renamed to " +
						newFileName);
			}
			index++;
		}
		options.setFlickrImageSetSize(Objects.requireNonNull(directory.listFiles()).length);
	}
}
