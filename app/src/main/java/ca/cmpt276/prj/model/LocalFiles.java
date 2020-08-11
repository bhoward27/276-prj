package ca.cmpt276.prj.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;

import static ca.cmpt276.prj.model.Constants.*;

/**
 * Makes interacting with our local images easier by adding methods to easily save, delete, and read
 * images
 */

public class LocalFiles {
	final static String TAG = "LocalFiles";

	// need options here too to set image set size
	OptionsManager options;

	File filesDir;
	List<File> files;

	public LocalFiles(Context ctx, String directory) {
		options = OptionsManager.getInstance();

		filesDir = Objects.requireNonNull(ctx)
				.getDir(directory, Context.MODE_PRIVATE);
		files = new ArrayList<>();
		try {
			files.addAll(Arrays.asList(Objects.requireNonNull(
					filesDir.listFiles())));
		} catch (NullPointerException e) {
			Log.d(TAG, "LocalFiles: filesDir: " + filesDir);
			Log.d(TAG, "LocalFiles: files is null");
		}
	}

	public LocalFiles(String directory) {
		options = OptionsManager.getInstance();

		files = new ArrayList<>();
		try {
			files.addAll(Arrays.asList(Objects.requireNonNull(
					filesDir.listFiles())));
		} catch (NullPointerException e) {
			Log.d(TAG, "LocalFiles: filesDir: " + filesDir);
			Log.d(TAG, "LocalFiles: files is null");
		}
	}

	public List<File> getFilesList() {
		return files;
	}

	public File getFile(int index) {
		return files.get(index);
	}

	public int getNumFiles() {
		return files.size();
	}

	public void writeImage(Context ctx, Bitmap image, String fileName) {
		final File imageFile = new File(filesDir, fileName); // Create image file
		new Thread(() -> {
			FileOutputStream fos = null;

			int i = 0;
			int maxRetries = 3;

			//	*******Below code should be changed before branch 69 or 70 is merged to master (whichever comes first).
			//	Instead of using break and "while (true)", it should probably update a flag variable / sentinel
			//	and the condition should be related to that variable.
			while (true) {
				try {
					fos = new FileOutputStream(imageFile);
					image.compress(Bitmap.CompressFormat.JPEG, 95, fos);

					// succeeded
					Log.i(TAG, "writeImage: image saved to >>>" + imageFile.getAbsolutePath());
					// citation: https://stackoverflow.com/a/34970752
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(ctx, ctx.getString(R.string.txt_toast_downloaded, fileName),
									Toast.LENGTH_SHORT).show();
						}
					});

					add(imageFile);

					break;
				} catch (IOException e) {
					if (++i == maxRetries) {
						Log.e(TAG, "IOException", e);
						// failed
						break;
					}
				} finally {
					try {
						fos.close();
					} catch (IOException e) {
						Log.e(TAG, "IOException", e);
					}
				}
			}
		}).start();
	}

	public Boolean remove(Context ctx, String fileName) {
		File file = new File(filesDir, fileName);
		if (file.delete()) {
			Toast.makeText(ctx, ctx.getString(R.string.txt_toast_deleted, file.getName()),
					Toast.LENGTH_SHORT).show();
			Log.i(TAG, "remove: image on the disk deleted successfully!");
			files.remove(file);
			if (options.getImageSet() <= FLICKR_IMAGE_SET) {
				options.setFlickrImageSetSize(files.size());
			}//TODO: else {
			// options.setCustomImageSetSize(imageSet, files.size());
			//}
			return true;
		}
		return false;
	}

	// Call this after adding an image to the folder through means other than writeImage
	// to update the list of files that this class knows about
	public void add(File file) {
		files.add(file);
		if (options.getImageSet() <= FLICKR_IMAGE_SET) {
			options.setFlickrImageSetSize(files.size());
		}//TODO: else {
		// options.setCustomImageSetSize(imageSet, files.size());
		//}
	}

}
