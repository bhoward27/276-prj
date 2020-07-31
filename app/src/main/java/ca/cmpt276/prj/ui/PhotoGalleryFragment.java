package ca.cmpt276.prj.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.FlickrFetchr;
import ca.cmpt276.prj.model.GalleryItem;
import ca.cmpt276.prj.model.LocalFiles;
import ca.cmpt276.prj.model.OptionsManager;
import ca.cmpt276.prj.model.QueryPreferences;
import ca.cmpt276.prj.model.ThumbnailDownloader;

import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;

/**
 * This activity loads Flickr images (from the internet) into a RecyclerView.
 * The user can select images to place in the Flickr image set from this fragment by clicking an
 * image in the RecyclerView.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */


public class PhotoGalleryFragment extends Fragment {
	private static final String TAG = "PhotoGalleryFragment";

	private RecyclerView mPhotoRecyclerView;
	private Context mContext;
	private List<GalleryItem> mItems = new ArrayList<>();
	private List<Target> targetList = new ArrayList<>();
	private SparseBooleanArray checkedItems = new SparseBooleanArray();
	private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
	private LocalFiles localFiles;

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		updateItems();

		mContext = getContext();
		localFiles = new LocalFiles(mContext, FLICKR_SAVED_DIR);

		Handler responseHandler = new Handler();
		mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
		mThumbnailDownloader.setThumbnailDownloadListener(
				new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
					@Override
					public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
						Drawable drawable = new BitmapDrawable(getResources(), bitmap);
						photoHolder.bindDrawable(drawable);
					}
				}
		);
		mThumbnailDownloader.start();
		mThumbnailDownloader.getLooper();
		Log.i(TAG, "Background thread started");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

		mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
		mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

		Toast.makeText(mContext, getString(R.string.txt_please_wait), Toast.LENGTH_LONG).show();

		setupAdapter();

		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mThumbnailDownloader.clearQueue();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mThumbnailDownloader.quit();
		Log.i(TAG, "Background thread destroyed");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.fragment_photo_gallery, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_item_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				Log.d(TAG, "QueryTextSubmit: " + s);
				QueryPreferences.setStoredQuery(getActivity(), s);
				updateItems();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				Log.d(TAG, "QueryTextChange: " + s);
				return false;
			}
		});

		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = QueryPreferences.getStoredQuery(getActivity());
				searchView.setQuery(query, false);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_clear:
				QueryPreferences.setStoredQuery(getActivity(), null);
				updateItems();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateItems() {
		String query = QueryPreferences.getStoredQuery(getActivity());
		new FetchItemsTask(query).execute();
	}

	private void setupAdapter() {
		if (isAdded()) {
			mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
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

	private class PhotoHolder extends RecyclerView.ViewHolder {
		private ImageView mItemImageView;
		public CheckBox mCheckBox;

		public PhotoHolder(View itemView) {
			super(itemView);

			mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
			mCheckBox = itemView.findViewById(R.id.checkBox);
		}

		public void bindDrawable(Drawable drawable) {
			mItemImageView.setImageDrawable(drawable);
		}

	}

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

		private List<GalleryItem> mGalleryItems;

		public PhotoAdapter(List<GalleryItem> galleryItems) {
			mGalleryItems = galleryItems;
		}

		private View.OnClickListener mOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int itemPosition = mPhotoRecyclerView.getChildLayoutPosition(v);

				// Save the image, mark the checkbox, and don't let user download the same image again
				CheckBox cb = v.findViewById(R.id.checkBox);
				checkedItems.put(itemPosition, !checkedItems.get(itemPosition));

				if (checkedItems.get(itemPosition)) {
					cb.setChecked(true);
					saveImage(itemPosition);
				} else {
					cb.setChecked(false);
					deleteImage(itemPosition);
				}

				//v.setOnClickListener(null);
			}
		};

		@Override
		public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
			view.setOnClickListener(mOnClickListener);
			return new PhotoHolder(view);
		}

		@Override
		public void onBindViewHolder(PhotoHolder photoHolder, int position) {
			GalleryItem galleryItem = mGalleryItems.get(position);
			Drawable placeholder = getResources().getDrawable(R.drawable.placeholder, null);

			photoHolder.bindDrawable(placeholder);
			photoHolder.mCheckBox.setChecked(checkedItems.get(position));

			mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());

			// free memory pointer
			targetList.clear();
		}

		@Override
		public int getItemCount() {
			return mGalleryItems.size();
		}
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

		private String mQuery;

		public FetchItemsTask(String query) {
			mQuery = query;
		}

		@Override
		protected List<GalleryItem> doInBackground(Void... params) {

			if (mQuery == null) {
				return new FlickrFetchr().fetchRecentPhotos();
			} else {
				return new FlickrFetchr().searchPhotos(mQuery);
			}
		}

		@Override
		protected void onPostExecute(List<GalleryItem> items) {
			mItems = items;
			setupAdapter();
			checkedItems.clear();
		}

	}

	//
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