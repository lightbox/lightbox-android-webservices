/**
 * Copyright (c) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lightbox.android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.lightbox.android.bitmap.BitmapLoader;
import com.lightbox.android.bitmap.BitmapLoader.Volatility;
import com.lightbox.android.bitmap.BitmapLoaderListener;
import com.lightbox.android.bitmap.BitmapSource;
import com.lightbox.android.bitmap.BitmapSource.Type;
import com.lightbox.android.cache.BitmapCache;
import com.lightbox.android.utils.debug.DebugLog;

/**
 * RemoteThumbImageView
 * 
 * @author Nilesh Patel
 */
public class RemoteThumbImageView extends ImageView implements BitmapLoaderListener {
	/** Used to tag logs */
	private static final String TAG = "RemoteThumbImageView";

	private BitmapSource mCurrentImageSource = null;
	private BitmapLoader mBitmapLoader;

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public RemoteThumbImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param attrs
	 */
	public RemoteThumbImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public RemoteThumbImageView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * @param context
	 */
	private void init(Context context) {
		mBitmapLoader = new BitmapLoader();

		setScaleType(ScaleType.CENTER_CROP);
		ViewGroup.LayoutParams fgParams = new ViewGroup.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(fgParams);
//		setBackgroundColor(getResources().getColor(R.color.thumbBackground));
	}

	public void startLoading(BitmapSource bitmapSource) {
		if (mCurrentImageSource == null || !mCurrentImageSource.getAbsoluteFileName(Type.THM).equals(bitmapSource.getAbsoluteFileName(Type.THM))) {
			// Different image than the current one, need to load
			mCurrentImageSource = bitmapSource;
			mBitmapLoader.cancel();
			reset();
			mBitmapLoader.loadAsync(bitmapSource, Type.THM, Config.RGB_565, Volatility.DEFAULT, this);
		}
	}

	private BitmapDrawable mBitmapDrawable;
	@Override
	public void onLoaded(Bitmap bitmap, Type type, boolean isFromMemCache) {
		if (mFadeRunnable.isFading()) {
			return;
		}
		if (mBitmapDrawable == null) {
			if (isFromMemCache) {
				mBitmapDrawable = new BitmapDrawable(bitmap);
				setImageDrawable(mBitmapDrawable);
				invalidate();
			} else {
				mBitmapDrawable = new BitmapDrawable(bitmap);
				mBitmapDrawable.setAlpha(0);
				setImageDrawable(mBitmapDrawable);
				invalidate();
				mFadeRunnable.start();
			}
		}
	}

	public void loadOnlyIfInMemCache(BitmapSource bitmapSource, int position) {		
		mCurrentImageSource = bitmapSource;
		Bitmap bitmap = BitmapCache.getInstance().getFromMemory(bitmapSource.getAbsoluteFileName(Type.THM));
		if (bitmap != null && (mBitmapDrawable == null || mBitmapDrawable.getBitmap() != bitmap)) {
			mBitmapDrawable = new BitmapDrawable(bitmap);
			setImageDrawable(mBitmapDrawable);
			invalidate();
		}
	}
	
	public void cancelLoading() {
		mBitmapLoader.cancel();
		if (mFadeRunnable.isFading()) {
			mFadeRunnable.stop();
		}
	}
	
	public void resumeLoading() {
		if (mFadeRunnable.isFading()) {
			return;
		}
		if (mCurrentImageSource != null) {
			if (mBitmapDrawable != null) {
				mBitmapDrawable.setAlpha(255);
				invalidate();
			} else {
				mBitmapLoader.loadAsync(mCurrentImageSource, Type.THM, Config.RGB_565, Volatility.DEFAULT, this);
			}
		}
	}
	
	@Override
	public void onFailure(Exception e) {
		DebugLog.d(TAG, "onFailure: %s", e);

		setImageResource(android.R.color.white);
	}

	public void reset() {
		mFadeRunnable.stop();
		mBitmapDrawable = null;
		setImageDrawable(null);
		invalidate();
	}
	
	public void resetIfWrongSource(BitmapSource bitmapSource) {
		if (mCurrentImageSource == null || !bitmapSource.getAbsoluteFileName(Type.THM).equals(mCurrentImageSource.getAbsoluteFileName(Type.THM))) {
			reset();
		}
	}
	
	private FadeRunnable mFadeRunnable = new FadeRunnable();
	private static final int FADE_DURATION = 400;
	private class FadeRunnable implements Runnable {
		private boolean mIsRunning;
		private long mStartTime;
		
		public void start() {
			if (mBitmapDrawable.getPaint().getAlpha() == 255) {
				return;
			}
			
			if (mIsRunning) {
				return;
			}
			
			mIsRunning = true;
			mStartTime = System.currentTimeMillis();
			
			RemoteThumbImageView.this.post(this);
		}
		
		public void stop() {
			mIsRunning = false;
			RemoteThumbImageView.this.removeCallbacks(this);
		}
		
		public boolean isFading() {
			return mIsRunning;
		}
		
		@Override
		public void run() {
			long elapsedTime = System.currentTimeMillis() - mStartTime;
			
			int alpha = Math.min((int)(255 * (float)(elapsedTime)/FADE_DURATION), 255);
			if (mBitmapDrawable != null) {
				mBitmapDrawable.setAlpha(alpha);
				invalidate();
			}
		
			if (mIsRunning && alpha < 255) {
				RemoteThumbImageView.this.post(this);
			} else {
				mIsRunning = false;
			}
		}		
	}
}
