package ca.cmpt276.prj.ui;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.FlickrFoldrImageRenamr;
import ca.cmpt276.prj.model.OptionSet;

import static ca.cmpt276.prj.model.Constants.FLICKR_PENDING_DIR;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;

/**
 * This activity loads the Photo Gallery (Flickr) fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class PhotoGalleryActivity extends SingleFragmentActivity {
	OptionSet options;

	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setTitle(getString(R.string.title_flickr_imageset_editor));

		Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_photo_gallery_activity));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Fragment createFragment() {
		initFlickrSettings();

		return PhotoGalleryFragment.newInstance();
	}

	@Override
	public void onBackPressed() {
		renameImages();
		this.finish();
	}

	// make action bar button behave like regular back button, for renaming images
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return false;
	}

	private void initFlickrSettings() {
		options = OptionSet.getInstance();
		File directory = Objects.requireNonNull(getApplicationContext())
				.getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE);
		options.setFlickrImageSetSize(Objects.requireNonNull(directory.listFiles()).length);

		// also clear garbage out of pending dir, possible if app crashed
		directory = Objects.requireNonNull(getApplicationContext())
				.getDir(FLICKR_PENDING_DIR, Context.MODE_PRIVATE);
		if (Objects.requireNonNull(directory.listFiles()).length > 0)
			for (File file : Objects.requireNonNull(directory.listFiles())) {
				if (file.delete()) {
					Log.d("clearGarbageImages", "garbage image on the disk deleted successfully!");
				}
			}
	}

	// Need to "rename images" before returning so that we have no images left in the "pending" folder
	private void renameImages() {
		FlickrFoldrImageRenamr.moveAndRenameTemp(this);
	}

}