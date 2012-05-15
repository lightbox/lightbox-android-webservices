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
package com.lightbox.android.bitmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.lightbox.android.bitmap.BitmapLoader.Volatility;
import com.lightbox.android.cache.BitmapCache;
import com.lightbox.android.tasks.BackgroundTaskWeak;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * BitmapLoaderTask 
 * @author Fabien Devos & Nilesh Patel
 */
/*package*/ class BitmapLoaderTask extends BackgroundTaskWeak<BitmapLoaderListener, Bitmap> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "BitmapLoaderTask";
	
	private static final int DEFAULT_CORE_POOL_SIZE = 6; //Maximum number of Threads running in parallel
    private static final int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE; // Will be ignored as long as the queue is unbounded
    private static final int KEEP_ALIVE = 10000; //(in milliseconds)
    
    private static ExecutorService sSingleThreadExecutor;
	private static ExecutorService sBitmapExecutor;

	private BitmapSource mBitmapSource;
	private BitmapSource.Type mType;
	private Config mConfig;
	private Volatility mVolatility;
	
	//----------------------------------------------
	// Constructors
	
	/**
	 * Constructor.
	 * @param listener
	 */
	public BitmapLoaderTask(BitmapSource bitmapSource, BitmapSource.Type type, Config config, Volatility volatility, BitmapLoaderListener listener) {
		super(listener, getBitmapExecutor(bitmapSource, type));
		mBitmapSource = bitmapSource;
		mType = type;
		mConfig = config;
		mVolatility = volatility;
	}
	
	private static ExecutorService getBitmapExecutor(BitmapSource bitmapSource, BitmapSource.Type type) {
		// If the file exist on disk, use a single thread executor 
		if (BitmapCache.getInstance().existOnDisk(bitmapSource.getAbsoluteFileName(type))) {
			if (sSingleThreadExecutor == null) {
				sSingleThreadExecutor = Executors.newSingleThreadExecutor(
						new BitmapLoaderThreadFactory("single thread"));
			}
			return sSingleThreadExecutor;
		} else {
			// Else use a thread pool executor
			if (sBitmapExecutor == null) {
				sBitmapExecutor = new ThreadPoolExecutor(
						DEFAULT_CORE_POOL_SIZE,
						MAXIMUM_POOL_SIZE,
						KEEP_ALIVE, TimeUnit.MILLISECONDS,
						new LinkedBlockingQueue<Runnable>(), 
						new BitmapLoaderThreadFactory("multiple threads"));
			}
			return sBitmapExecutor;
		}
	}
	
	//----------------------------------------------
	// BackgroundTask implementation

	@Override
	protected Bitmap doWorkInBackground() throws Exception {
		DebugLog.d(TAG, "Start loading bitmap: "+mBitmapSource.getUri(mType));
		return BitmapLoader.loadFromDiskOrNetwork(mBitmapSource, mType, mConfig, mVolatility);
	}

	@Override
	protected void onCompleted(Bitmap bitmap) {		
		BitmapLoaderListener listener = getRef();
		if (listener != null) {
			if (bitmap == null) {
				listener.onFailure(new NullPointerException("bitmap is null"));
			} else {
				listener.onLoaded(bitmap, mType, false);
			}
		}
	}

	@Override
	protected void onFailed(Exception exception) {
		BitmapLoaderListener listener = getRef();
		if (listener != null) {
			listener.onFailure(exception);
		}
	}

	/************************************************
	 * BitmapLoaderThreadFactory 
	 */
	private static class BitmapLoaderThreadFactory implements ThreadFactory {
		private String mType;
		/** Constructor.*/
		public BitmapLoaderThreadFactory(String type) {
			mType = type;
		}
		// -------------------------
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName(TAG + " thread | " + mType);
			thread.setPriority(Thread.NORM_PRIORITY);
			return thread;
		}
	}

}
