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
import android.widget.Toast;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.GalleryItem;
import ca.cmpt276.prj.model.LocalFiles;
import ca.cmpt276.prj.model.OptionsManager;
import ca.cmpt276.prj.model.PicassoImageEngine;


import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * This activity loads the Custom Photos activity and saves the resulting selected photos to the local
 * files directory.
 * Makes use of https://github.com/zhihu/Matisse to get local images
 */

public class PhotoActivity extends AppCompatActivity {
	private static final int REQUEST_CODE_CHOOSE = 23;
	private static final String TAG = "PhotoActivity";
	private static final int REQUEST_PERMISSION_CODE = 2;

	Boolean storagePermissionGranted;
	OptionsManager options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//True if Permission was granted
		storagePermissionGranted = checkSelfPermission(
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_GRANTED;

		if (storagePermissionGranted) {
			try {
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
			} catch (IllegalArgumentException e) {
				Log.d(TAG, "Failed to load images. That's unfortunate.");
				Toast.makeText(getApplicationContext(),
						"This bug shouldn't happen.",
						Toast.LENGTH_LONG).show();
				this.finish();
			} catch (RuntimeException e) {
				Log.d(TAG, "Failed to load images. That's unfortunate. This error should not occur.");
				Toast.makeText(getApplicationContext(),
						"This bug shouldn't happen.",
						Toast.LENGTH_LONG).show();
				this.finish();
			}


		} else {
			ActivityCompat.requestPermissions(PhotoActivity.this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_PERMISSION_CODE);
		}
	}

	List<Uri> mSelected;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		OptionsManager.instantiate(this);
		options = OptionsManager.getInstance();

		if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
			LocalFiles localFiles = new LocalFiles(this, FLICKR_SAVED_DIR);
			mSelected = Matisse.obtainResult(data);
			for (Uri uri : mSelected) {
				long unixTime = System.currentTimeMillis() / 1000L;

				try {
					localFiles.add(Objects.requireNonNull(getContentResolver().openInputStream(uri)),
							unixTime + "_" + mSelected.indexOf(uri) + JPG_EXTENSION);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.d(TAG, "onActivityResult: Failed to add this uri:" + uri);
					break;
				}

				Log.d(TAG, "uri: " + uri + " added");
			}
			Log.d("Matisse", "mSelected: " + mSelected);
		}
		this.finish();
	}

}



