package ca.cmpt276.prj.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import ca.cmpt276.prj.model.OptionSet;
import ca.cmpt276.prj.model.QueryPreferences;
import ca.cmpt276.prj.model.ThumbnailDownloader;

import static ca.cmpt276.prj.model.Constants.FLICKR_PENDING_DIR;
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
    private OptionSet options;
    private Context mContext;
    private List<GalleryItem> mItems = new ArrayList<>();
    private List<Target> targetList = new ArrayList<>();
    private SparseBooleanArray checkedItems = new SparseBooleanArray();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		updateItems();

		options = OptionSet.getInstance();
		mContext = getContext();

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
		File directory = Objects.requireNonNull(getContext())
				.getDir(FLICKR_PENDING_DIR, Context.MODE_PRIVATE);
		int numUserImages = Objects.requireNonNull(directory.listFiles()).length;
		String fileName = mItems.get(itemPosition).getId() + JPG_EXTENSION;
		if (numUserImages > 0) {
			File myImageFile = new File(directory,
					fileName);
			if (myImageFile.delete()) {
				options.removePossibleFlickrImageNames(fileName);
				Log.d("deleteImage", "image on the disk deleted successfully!");
			}
		}
	}

	public void saveImage(int itemPosition) {
		GalleryItem item = mItems.get(itemPosition);

		String fileName = mItems.get(itemPosition).getId() + JPG_EXTENSION;
		//  What if the extension is .png for the image from Flickr? UH OH.
		Picasso.get().load(item.getUrl()).into(picassoImageTarget(mContext,
				fileName,
				item));
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
	private Target picassoImageTarget(Context context, final String imageName, GalleryItem item) {
		Log.d("picassoImageTarget", " picassoImageTarget");
		ContextWrapper cw = new ContextWrapper(context);
		final File directory = cw.getDir(FLICKR_PENDING_DIR, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
		targetList.clear();
		targetList.add(0, new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
				new Thread(() -> {
					final File myImageFile = new File(directory, imageName); // Create image file
					FileOutputStream fos = null;
					int i = 0;
					int maxRetries = 3;
					while (true) {
						try {
							fos = new FileOutputStream(myImageFile);
							bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
							break;
						} catch (IOException e) {
							if (++i == maxRetries) Log.e(TAG, "IOException", e);
						} finally {
							try {
								fos.close();
							} catch (IOException e) {
								Log.e(TAG, "IOException", e);
							}
						}
					}
					options.addPossibleFlickrImageNames(imageName);
					Looper.prepare();

					// citation: https://stackoverflow.com/a/34970752
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(mContext, getString(R.string.txt_toast_downloaded, item.getUrl()),
									Toast.LENGTH_SHORT).show();
						}
					});

					Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());
				}).start();
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