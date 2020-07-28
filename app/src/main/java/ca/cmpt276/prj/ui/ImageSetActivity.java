package ca.cmpt276.prj.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.GridLayoutManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.prj.R;
import ca.cmpt276.prj.model.GalleryItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ca.cmpt276.prj.model.FlickrFetchr;
import ca.cmpt276.prj.model.QueryPreferences;
import ca.cmpt276.prj.model.ThumbnailDownloader;

public class ImageSetActivity extends AppCompatActivity {
    private static final String TAG = "Image Set";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<.PhotoHolder> mThumbnailDownloader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoGalleryFragment.PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoGalleryFragment.PhotoHolder photoHolder, Bitmap bitmap) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.activity_image_set, menu);
        MenuItem addItem = menu.add(R.id.add_photos);
        MenuItem deleteItem = menu.removeItem(R.id.delete_image);

        int id = addItem.getItemId();

        if(id == R.id.add_photos) {
            Intent intent = new Intent(MainMenuActivity.this.PhotoGalleryActivity.class);
            startActivity(intent);
            return true;
        }

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                Log.d(TAG, "QueryTextSubmit: " + s);
//                QueryPreferences.setStoredQuery(getActivity(), s);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                Log.d(TAG, "QueryTextChange: " + s);
//                return false;
//            }
        });
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<.PhotoHolder> {

        private List<GalleryItem> mGalleryItems;
//        private OnPhotoClickListener mlistener;

//        public interface OnPhotoClickListener {
//            void onPhotoClick(int position);
//        }

//        public void setOnPhotoClickListener(OnPhotoClickListener listener){
//            mlistener = listener;
//        }

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public .PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoGalleryFragment.PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(.PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.a_2);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

    }

}
