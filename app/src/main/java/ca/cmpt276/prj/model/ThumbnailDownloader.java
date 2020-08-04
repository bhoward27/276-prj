package ca.cmpt276.prj.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class performs the internal operations required to fetch the thumbnails from Flickr
 * Citation: Android Programming: The Big Nerd Ranch Guide (3rd Edition)
 * Code downloaded from: https://opencoursehub.cs.sfu.ca/bfraser/solutions/276/android/BigNerdRanch-AndroidProgramming3e-Code.zip
 */

public class ThumbnailDownloader<T> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;

	private boolean mHasQuit = false;
	private Handler mRequestHandler;
	private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
	private Handler mResponseHandler;
	private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

	public interface ThumbnailDownloadListener<T> {
		void onThumbnailDownloaded(T target, Bitmap bitmap);
	}

	public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
		mThumbnailDownloadListener = listener;
	}

	public ThumbnailDownloader(Handler responseHandler) {
		super(TAG);
		mResponseHandler = responseHandler;
	}

	@Override
	protected void onLooperPrepared() {
		mRequestHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_DOWNLOAD) {
					T target = (T) msg.obj;
					Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
					handleRequest(target);
				}
			}
		};
	}

	@Override
	public boolean quit() {
		mHasQuit = true;
		return super.quit();
	}

	public void queueThumbnail(T target, String url) {
		Log.i(TAG, "Got a URL: " + url);

		if (url == null) {
			mRequestMap.remove(target);
		} else {
			mRequestMap.put(target, url);
			mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
					.sendToTarget();
		}
	}

	public void clearQueue() {
		mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
		mRequestMap.clear();
	}

	private void handleRequest(final T target) {
		try {
			final String url = mRequestMap.get(target);

			if (url == null) {
				return;
			}

			byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
			final Bitmap bitmap = BitmapFactory
					.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			Log.i(TAG, "Bitmap created");

			mResponseHandler.post(new Runnable() {
				public void run() {
					if (mRequestMap.get(target) != url ||
							mHasQuit) {
						return;
					}

					mRequestMap.remove(target);
					mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
				}
			});
		} catch (IOException ioe) {
			Log.e(TAG, "Error downloading image", ioe);
		}
	}
}