package ca.cmpt276.prj.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.GalleryItem;
import ca.cmpt276.prj.model.OptionSet;
import ca.cmpt276.prj.model.QueryPreferences;

import static ca.cmpt276.prj.model.Constants.FLICKR_PREFIX;
import static ca.cmpt276.prj.model.Constants.FLICKR_SAVED_DIR;
import static ca.cmpt276.prj.model.Constants.JPG_EXTENSION;
import static ca.cmpt276.prj.model.Constants.RESOURCE_DIVIDER;

/**
 * This activity loads Flickr images (from your locally downloaded files) into a RecyclerView.
 * The user can add and remove images from the Flickr image set in this fragment by clicking an
 * image in the RecyclerView.
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */


public class EditImageSetFragment extends Fragment {
    private static final String TAG = "EditImageSetFragment";

    private RecyclerView mPhotoRecyclerView;
    private OptionSet options;
    private Context mContext;
    private List<GalleryItem> mItems = new ArrayList<>();
    private SparseBooleanArray checkedItems = new SparseBooleanArray();

    public static EditImageSetFragment newInstance() {
        return new EditImageSetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();

        options = OptionSet.getInstance();
        mContext = getContext();

        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flickr_editor, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume(){
        super.onResume();

        updateItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_flickr_editor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {

            case R.id.menu_item_add:
                Log.d(TAG, "onOptionsItemSelected: ");
                Intent intent = new Intent(getActivity(), PhotoGalleryActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_clear_images:
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
                .getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE);
        int numUserImages = Objects.requireNonNull(directory.listFiles()).length;
        String fileName = mItems.get(itemPosition).getId();
        Log.d(TAG, "filename: " + fileName);
        if (numUserImages > 0) {
            File myImageFile = new File(directory,
                    fileName);
            if (myImageFile.delete()) {
                options.removePossibleFlickrImageNames(fileName);
                Toast.makeText(mContext, getString(R.string.txt_toast_deleted, fileName),
                        Toast.LENGTH_SHORT).show();
                Log.d("deleteImage", "image on the disk deleted successfully!");
            }
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        public ImageView mItemImageView;
        public CheckBox mCheckBox;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = itemView.findViewById(R.id.item_image_view);
            mCheckBox = itemView.findViewById(R.id.checkBox);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<PhotoHolder> mPhotoHolders = new ArrayList<>();
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

                Drawable placeholder = getResources().getDrawable(R.drawable.placeholder, null);
                File imageFile = new File("");

                Picasso.get()
                        .load(imageFile)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(mPhotoHolders.get(itemPosition).mItemImageView);

                if (!checkedItems.get(itemPosition)) {
                    cb.setChecked(false);
                    deleteImage(itemPosition);
                }

                checkedItems.put(itemPosition, true);
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
            mPhotoHolders.add(photoHolder);

            GalleryItem galleryItem = mGalleryItems.get(position);
            galleryItem.setId(FLICKR_PREFIX + RESOURCE_DIVIDER + position + JPG_EXTENSION);
            mGalleryItems.set(position, galleryItem);
            Drawable placeholder = getResources().getDrawable(R.drawable.placeholder, null);
            //photoHolder.bindDrawable(placeholder);
            String pathName = mContext.getDir(FLICKR_SAVED_DIR, Context.MODE_PRIVATE) + "/" +
                    FLICKR_PREFIX + RESOURCE_DIVIDER + position + JPG_EXTENSION;
            File imageFile = new File(pathName);

            Picasso.get()
                    .load(imageFile)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(photoHolder.mItemImageView);

            photoHolder.mCheckBox.setChecked(!checkedItems.get(position));
        }

        public PhotoHolder getPhotoHolder(int position) {
            return mPhotoHolders.get(position);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {


        public FetchItemsTask(String query) {

        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            List<GalleryItem> gItems = new ArrayList<>();
            for (int i = 0; i < options.getNumFlickrImages(); i++) {
                gItems.add(new GalleryItem());
            }

            return gItems;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }

    }
}