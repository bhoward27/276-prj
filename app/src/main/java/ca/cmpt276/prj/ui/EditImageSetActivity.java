package ca.cmpt276.prj.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import ca.cmpt276.prj.R;


/**
 * This activity loads the Edit Flickr Image Set fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class EditImageSetActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setTitle(getString(R.string.title_image_set));

		Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_image_set));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Fragment createFragment() {
		return EditImageSetFragment.newInstance();
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