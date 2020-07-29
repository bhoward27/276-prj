package ca.cmpt276.prj.ui;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Objects;

import ca.cmpt276.prj.model.FlickrFoldrImageRenamr;
import ca.cmpt276.prj.model.OptionSet;

import static ca.cmpt276.prj.model.Constants.FLICKR_IMAGE_NAME_PREFIX;
import static ca.cmpt276.prj.model.Constants.FLICKR_PENDING_DIR;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * This activity loads the Photo Gallery (Flickr) fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class EditImageSetActivity extends SingleFragmentActivity {
    OptionSet options;
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