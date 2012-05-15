package com.lightbox.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageSwitcher;

import com.lightbox.android.bitmap.BitmapLoaderListener;
import com.lightbox.android.bitmap.BitmapSource;


/**
 * RemoteImageView 
 * @author Nilesh Patel
 */
public abstract class RemoteImageView extends ImageSwitcher implements BitmapLoaderListener {

	/**
	 * Constructor.
	 * @param context
	 * @param attrs
	 */
	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Constructor.
	 * @param context
	 */
	public RemoteImageView(Context context) {
		super(context);
	}
	
	public abstract void loadOnlyIfInMemCache(BitmapSource source, int position);
	
	public abstract void cancelLoading();

	public abstract void resumeLoading();
	
	public abstract void reset();
	
	
	public interface RemoteImageViewHolder {
		public void resetImageViews();
		public void startLoading(BitmapSource source);
		public void loadOnlyIfInMemCache(BitmapSource source);
		public void cancelLoading();
		public void resumeLoading();
		public void clearTouchState();
	}
}