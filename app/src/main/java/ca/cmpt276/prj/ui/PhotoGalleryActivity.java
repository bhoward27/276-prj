package ca.cmpt276.prj.ui;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.Objects;

import ca.cmpt276.prj.R;

/**
 * This activity loads the Photo Gallery (Flickr) fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class PhotoGalleryActivity extends SingleFragmentActivity {
	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setTitle(getString(R.string.title_flickr_imageset_editor));

		Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_photo_gallery_activity));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Fragment createFragment() {
		return PhotoGalleryFragment.newInstance();
	}

	@Override
	public void onBackPressed() {
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
}