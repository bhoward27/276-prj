package ca.cmpt276.prj.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.FlickrFoldrImageRenamr;


/**
 * This activity loads the Photo Gallery (Flickr) fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class EditImageSetActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setTitle(getString(R.string.title_flickr_imageset_editor));
    }

    @Override
    protected Fragment createFragment() {
        initFlickrSettings();

        return EditImageSetFragment.newInstance();
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
        FlickrFoldrImageRenamr.makeFileNamesConsistent(this);
    }

    // Need to "rename images" before returning so that we have no images left in the "pending" folder
    private void renameImages() {
        FlickrFoldrImageRenamr.makeFileNamesConsistent(this);
    }

}