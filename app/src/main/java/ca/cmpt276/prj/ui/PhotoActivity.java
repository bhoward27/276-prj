package ca.cmpt276.prj.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
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


    List<Uri> mSelected;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
            final Uri imageUri = data.getData();
            Log.e("imageUri:",imageUri+"");
            String selectPhoto = getRealPathFromUri(this,imageUri);
            Log.e("selectPhoto:",selectPhoto);
            File file = new File(selectPhoto);
            localFiles.add(file,"LocalFiles");
        }
    }

    public static String getRealPathFromUri(Context context, Uri uri) {
        if(context == null || uri == null) {
            return null;
        }
        if("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context,uri);
        } else if("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context,uri);
        }
        return getRealPathFromUri_AboveApi19(context, uri);
    }

    //Uri:: file:///storage/emulated/0/DCIM/Camera/IMG_20170613_132837.jpg
    private static String getRealPathFromUri_Byfile(Context context,Uri uri){
        String uri2Str = uri.toString();
        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);
        return filePath;
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = null;

        wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * //api11-api18
     * Uri:: content://media/external/images/media/1028
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * lower api11
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

}



