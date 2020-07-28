package ca.cmpt276.prj.ui;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.Context;

import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.ui.PhotoGalleryFragment;
import ca.cmpt276.prj.ui.SingleFragmentActivity;

/**
 * This activity loads the Photo Gallery (Flickr) fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class PhotoGalleryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}