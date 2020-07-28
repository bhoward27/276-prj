package ca.cmpt276.prj.ui;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.util.Objects;

import ca.cmpt276.prj.model.OptionSet;

import static ca.cmpt276.prj.model.Constants.FLICKR_PENDING_DIR;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;
import static ca.cmpt276.prj.ui.PhotoGalleryFragment.FLICKR_IMAGE_NAME_PREFIX;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    OptionSet options;
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
                Log.d("clearGarbageImages","garbage image on the disk deleted successfully!");
            }
        }
    }

    private void renameImages() {
        File preDirectory = Objects.requireNonNull(getApplicationContext())
                .getDir(FLICKR_PENDING_DIR, Context.MODE_PRIVATE);
        File postDirectory = Objects.requireNonNull(getApplicationContext())
                .getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE);
        int numUserImages = Objects.requireNonNull(preDirectory.listFiles()).length;
        if (numUserImages > 0) {
            int index = options.getNumImagesInImageSet();
            for (String imageName : options.getPossibleFlickrImageNames()) {
                File myImageFile = new File(preDirectory,
                        imageName);
                File destRenamedFile = new File(postDirectory,
                        FLICKR_IMAGE_NAME_PREFIX + index + JPG_EXTENSION);
                if (myImageFile.renameTo(destRenamedFile)) {
                    Log.d("renameImage","image " + imageName + " has been renamed");
                    index++;
                }
            }
            options.setFlickrImageSetSize(Objects.requireNonNull(postDirectory.listFiles()).length);
            options.clearPossibleFlickrImageNames();
        }
    }

}