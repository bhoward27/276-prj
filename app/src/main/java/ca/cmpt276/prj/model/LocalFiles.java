package ca.cmpt276.prj.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	Context context;

	public LocalFiles(Context ctx, String directory) {
		context = ctx;
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

					files.add(imageFile);
					options.setFlickrImageSetSize(files.size());

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
			if (options.getImageSet() <= CUSTOM_IMAGE_SET) {
				options.setFlickrImageSetSize(files.size());
			}
			return true;
		}
		return false;
	}

	public Boolean remove(String fileName) {
		File file = new File(filesDir, fileName);
		if (file.delete()) {
			Log.i(TAG, "remove: image on the disk deleted successfully!");
			files.remove(file);
			if (options.getImageSet() <= CUSTOM_IMAGE_SET) {
				options.setFlickrImageSetSize(files.size());
			}
			return true;
		}
		return false;
	}

	// Citation: https://www.baeldung.com/convert-input-stream-to-a-file
	public void add(InputStream fileIS, String name) {
		File tempFile = new File(filesDir, name + "TEMP");

		byte[] buffer = new byte[0];
		try {
			buffer = new byte[fileIS.available()];
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "add: Failed!");
			return;
		}
		try {
			fileIS.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "add: Failed!");
			return;
		}

		OutputStream outStream;
		try {
			outStream = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d(TAG, "add: Failed!");
			return;
		}
		try {
			outStream.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "add: Failed!");
			return;
		}

		OutputStream destFileOS;
		String fileName = filesDir.getAbsolutePath() + "/" + name;

		try {
			destFileOS = new FileOutputStream(filesDir.getAbsolutePath() + "/" + name);
			Log.d(TAG, "PATH+NAME: " + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d(TAG, "add: Failed! (destfile)");
			return;
		}

		// resize and convert to JPG
		Bitmap bmp;
		bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

		float ratio = (float) bmp.getWidth() / bmp.getHeight();

		bmp = Bitmap.createScaledBitmap(bmp,
				Math.round(IMAGE_RESIZE_PIXELS_HEIGHT * ratio),
				IMAGE_RESIZE_PIXELS_HEIGHT,
				true);
		bmp.compress(Bitmap.CompressFormat.JPEG, 95, destFileOS);

		File destFile = new File(fileName);
		// remove the temporary (non-resized PNG/JPG)
		remove(tempFile.getName());

		// if we got here then all the try catch blocks succeeded.
		files.add(destFile);
		options.setFlickrImageSetSize(files.size());
		Log.d(TAG, "add: Added!");
	}

}
