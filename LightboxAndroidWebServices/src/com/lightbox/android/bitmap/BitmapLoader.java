/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.lightbox.android.cache.BitmapCache;
import com.lightbox.android.network.HttpHelper;
import com.lightbox.android.network.HttpHelper.HttpMethod;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * This class allows you to load a bitmap, synchronously or in the background, taking care for you of memory caching,
 * disk caching, and background task.
 * @see BitmapSource
 * @author Fabien Devos & Nilesh Patel
 */
public class BitmapLoader {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "BitmapLoader";

	private BitmapLoaderTask mBitmapLoaderTask;
	
	//----------------------------------------------
	// Bitmap Loading API
	
	public enum Volatility {
		DEFAULT,
		DO_NOT_CACHE,
	}
	
	public static Bitmap load(BitmapSource bitmapSource, BitmapSource.Type type, Config config, Volatility volatility) throws IOException {
		// Try to get bitmap from memory cache
		Bitmap bitmap = BitmapCache.getInstance().getFromMemory(bitmapSource.getAbsoluteFileName(type));
		
		// Else load from file on disk, or network
		if (bitmap == null) {
			bitmap = loadFromDiskOrNetwork(bitmapSource, type, config, volatility);
		}
		
		return bitmap;
	}

	public void loadAsync(BitmapSource bitmapSource, BitmapSource.Type type, Config config, Volatility volatility, BitmapLoaderListener listener) {
		// Try to get bitmap from memory cache on main thread
		Bitmap bitmap = BitmapCache.getInstance().getFromMemory(bitmapSource.getAbsoluteFileName(type));
				
		if (bitmap == null) {
			DebugLog.d(TAG, "Unable to get Bitmap from memory cache, fetching from disk or network. Title: %s - Type: %s - Absolute file name: %s", bitmapSource.getTitle(), type, bitmapSource.getAbsoluteFileName(type));

			mBitmapLoaderTask = new BitmapLoaderTask(bitmapSource, type, config, volatility, listener);
			mBitmapLoaderTask.execute();
		} else {
			listener.onLoaded(bitmap, type, true); 
		}		
	}
	
	public void cancel() {
		if (mBitmapLoaderTask != null) {
			mBitmapLoaderTask.cancel();
		}
	}
	
	//----------------------------------------------
	// Loading
	
	/*package*/ static Bitmap loadFromDiskOrNetwork(BitmapSource bitmapSource, BitmapSource.Type type, Config config, Volatility volatility) throws IOException {
		Bitmap bitmap = null;
		
		try {
			bitmap = loadFromDiskOrNetworkWithOutOfMemoryRisk(bitmapSource, type, config, volatility);
			
		} catch (OutOfMemoryError e) {
			DebugLog.d(TAG, "OutOfMemoryError! Trying to reduce the size of the bitmap memory cache.");
			BitmapCache.getInstance().decreaseMemoryCacheSize();
		}
		
		return bitmap;
	}
	
	/*package*/ static Bitmap loadFromDiskOrNetworkWithOutOfMemoryRisk(BitmapSource bitmapSource, BitmapSource.Type type, Config config, Volatility volatility) throws IOException {
		// Try to load it from disk
		Bitmap bitmap = BitmapCache.getInstance().getFromDisk(bitmapSource.getAbsoluteFileName(type), config).getData();
		
		// Else try to get it from network
		if (bitmap == null) {
			//Log.e(TAG, "CALLING SERVER file: %s" + bitmapSource.getAbsoluteFileName(type));
			DebugLog.d(TAG, "Unable to get Bitmap from disk cache, fetching from network. Title: %s - Type: %s - Absolute file name: %s", bitmapSource.getTitle(), type, bitmapSource.getAbsoluteFileName(type));

			bitmap = getBitmapFromNetwork(bitmapSource, type, config);
		}
		
		// Put in memory cache
		if (volatility == Volatility.DEFAULT) {
			BitmapCache.getInstance().putInMemory(bitmapSource.getAbsoluteFileName(type), bitmap);
		}
		
		return bitmap;
	}
		
	//----------------------------------------------
	// Bitmap from Network

	private static Bitmap getBitmapFromNetwork(BitmapSource bitmapSource, BitmapSource.Type type, Config config) {
		Bitmap bitmap = null;
		URI uri = bitmapSource.getUri(type);
		if (uri != null) {
			FileOutputStream fos = null;
			try {
				File file = new File(bitmapSource.getAbsoluteFileName(type));
				
				// Ensure that the directory exist
				file.getParentFile().mkdirs();
				
				HttpResponse httpResponse = HttpHelper.getInstance().call(HttpMethod.GET, uri, null);
				
				fos = new FileOutputStream(file);
				
				IOUtils.copy(httpResponse.getEntity().getContent(), fos);
				
				bitmap = BitmapCache.getInstance().getFromDisk(bitmapSource.getAbsoluteFileName(type), config).getData();
			} catch (IOException e) {
				DebugLog.d("", e.getMessage());
				// Return null
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
		return bitmap;
	}
	
}
