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
package com.lightbox.android.cache;

import java.io.File;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.concurrentlinkedhashmap.Weigher;
import com.lightbox.android.bitmap.BitmapFileCleanerTask;
import com.lightbox.android.bitmap.BitmapUtils;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * BitmapCache 
 * @author Fabien Devos
 */
public class BitmapCache extends AbstractCache<Bitmap, Bitmap> {
	/** Used to tag logs */
	// @SuppressWarnings("unused")
	private static final String TAG = "BitmapCache";
	
	private static final int DEFAULT_MAX_MEM_CACHE_SIZE = 128 * 128 * 4 * 100; // 100 128x128 bitmaps (around 6.5 Mb)

	private int mCurrentMaxMemCacheSize = DEFAULT_MAX_MEM_CACHE_SIZE;

	//----------------------------------------------------------------------------
	// Singleton pattern
	private BitmapCache() {
		super((new Config<Bitmap>())
				.setMaximumWeightedCapacityInMemory(DEFAULT_MAX_MEM_CACHE_SIZE)
				.setWeigher(new BitmapWeigher()));
	}

	/** BitmapCacheHolder is loaded on the first execution of BitmapCache.getInstance() 
	 * or the first access to BitmapCacheHolder.INSTANCE, not before. */
	private static class BitmapCacheHolder {
		private static final BitmapCache INSTANCE = new BitmapCache();
	}

	/** @return a unique instance of the class */
	public static BitmapCache getInstance() {
		return BitmapCacheHolder.INSTANCE;
	}

	/** Not supported
	 * @throws CloneNotSupportedException (every time) */
	public Object clone() throws CloneNotSupportedException {
		// to prevent any kind of cheating
		throw new CloneNotSupportedException();
	}
	//----------------------------------------------------------------------------
	
	//----------------------------------------------
	// Disk cache methods
	
	@Override
	public boolean existOnDisk(String absoluteFileName) {
		return (new File(absoluteFileName)).exists();
	}
	
	@Override
	public Result<Bitmap> getFromDisk(String absoluteFileName, Object... objects) {
		Result<Bitmap> result = null;
		
		android.graphics.Bitmap.Config config = (objects.length == 0) ? null : (android.graphics.Bitmap.Config) objects[0];
				
		if (absoluteFileName != null) {
			Bitmap bitmap = BitmapUtils.readBitmapFromFile(absoluteFileName, config);
			// The bitmap never expires
			result = new Result<Bitmap>(bitmap, System.currentTimeMillis());			
		} else {
			result = new Result<Bitmap>(null, 0);
		}
		return result;
	}
	
	public void decreaseMemoryCacheSize() {
		clearMemory();
		mCurrentMaxMemCacheSize = (int) (mCurrentMaxMemCacheSize * 0.7f);
		resetMemoryCache(mCurrentMaxMemCacheSize);
	}

	@Override
	public void putOnDisk(String absoluteFileName, Bitmap bitmap) {
		if (absoluteFileName != null && bitmap != null) {
			try {
				BitmapUtils.writeBitmapInFile(absoluteFileName, bitmap);
				
				DebugLog.d(TAG, "Bitmap saved on disk. Absolute file name: %s", absoluteFileName);
			} catch (Exception e) {
				// ignore exception since this is just a cache
				Log.w(TAG, "Unable to save bitmap", e);
			}
		}
	}
	
	@Override
	public void clearDisk() {
		// TODO
		//FileUtils.deleteQuietly(new File(Photo.CACHE_DIRECTORY));
	}
	
	@Override
	public void startDiskCleanup() {
		BitmapFileCleanerTask cleanupTask = new BitmapFileCleanerTask();
		cleanupTask.execute();
	}
	
	//----------------------------------------------
	// Get from memory
	@Override
	public Bitmap getFromMemory(String key) {
		Bitmap bitmap = super.getFromMemory(key);
		if (bitmap != null && bitmap.isRecycled()) {
			// Good coding should mean this should never happen!
			DebugLog.d(TAG, "Found recycled bitmap in the cache! Key="+key);
			removeFromMemory(key);
			return null;
		}
		
		return bitmap;
	}

	
	/************************************************************
	 * BitmapWeigher 
	 */
	private static class BitmapWeigher implements Weigher<Bitmap> {
		@Override
		public int weightOf(Bitmap bitmap) {
			return bitmap.getWidth() * bitmap.getHeight() * (bitmap.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2);//4;
		}
	}

}
