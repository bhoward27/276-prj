package ca.cmpt276.prj.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.GalleryItem;
import ca.cmpt276.prj.model.LocalFiles;
import ca.cmpt276.prj.model.PicassoImageEngine;


import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * This activity loads the Photo fragment.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class PhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String TAG = "PhotoActivity";
    private static final int REQUEST_PERMISSION_CODE = 2;

    private Context mContext;
    private List<GalleryItem> mItems = new ArrayList<>();
    private List<Target> targetList = new ArrayList<>();
    private LocalFiles localFiles;
    Boolean storagePermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_photo);

        //mContext = getContext();
        mContext = PhotoActivity.this;
        localFiles = new LocalFiles(mContext, FLICKR_SAVED_DIR);

//        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(
//                R.string.title_photo_activity));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //True if Permission was granted
        storagePermissionGranted = checkSelfPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if(storagePermissionGranted){
            Matisse.from(PhotoActivity.this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(9)
                            .theme(R.style.Matisse_Dracula)
                            .imageEngine(new PicassoImageEngine())
                            .gridExpectedSize(360)
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            //.thumbnailScale(0.8f)
                            .forResult(REQUEST_CODE_CHOOSE);

        }else{
            ActivityCompat.requestPermissions(PhotoActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);

        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_PERMISSION_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Matisse.from(PhotoActivity.this)
//                            .choose(MimeType.ofImage())
//                            .countable(true)
//                            .maxSelectable(9)
//                            .theme(R.style.Matisse_Dracula)
//                            //.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                            .imageEngine(new PicassoEngine())
//                            .gridExpectedSize(120)
//                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                            .thumbnailScale(0.8f)
//                            .forResult(REQUEST_CODE_CHOOSE);
//                } else {
//                    ActivityCompat.requestPermissions(PhotoActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            REQUEST_PERMISSION_CODE);
//
//                }
//        }
//    }


    List<Uri> mSelected;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
        }
    }

    // citation: https://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    public void deleteImage(int itemPosition) {
        localFiles.remove(mContext, mItems.get(itemPosition).getId() + JPG_EXTENSION);
    }

    public void saveImage(int itemPosition) {
        GalleryItem item = mItems.get(itemPosition);

        // Note: all Flickr images are .JPG
        String fileName = mItems.get(itemPosition).getId() + JPG_EXTENSION;
        Picasso.get().load(item.getUrl()).into(picassoImageTarget(fileName));
    }

    // citation https://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    private Target picassoImageTarget(final String imageName) {
        // add to array to keep reference in memory
        targetList.add(0, new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                localFiles.writeImage(mContext, bitmap, imageName);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed: ");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                }
            }
        });
        return targetList.get(0);
    }

}



