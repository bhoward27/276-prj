package ca.cmpt276.prj.ui;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.Context;

import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.ui.PhotoGalleryFragment;
import ca.cmpt276.prj.ui.SingleFragmentActivity;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}