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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import android.util.Log;

import com.lightbox.android.tasks.BackgroundTask;
import com.lightbox.android.utils.debug.DebugLog;

/** 
 * BitmapFileCleanerTask 
 * @author Fabien Devos
 */
public class BitmapFileCleanerTask extends BackgroundTask<Void> {
	/** Used to tag logs */
	// @SuppressWarnings("unused")
	private static final String TAG = "BitmapFileCleanerTask";

	private static final long MAX_DISK_CACHE_SIZE = 200 * 1000 * 1000; // (in bytes)
	
	private static ExecutorService sBitmapFileCleanerExecutor;
	
	private long mCleanupDuration;
	
	/**
	 * Constructor.
	 * @param newSingleThreadExecutor
	 */
	public BitmapFileCleanerTask() {
		super(getExecutor());
	}
	
	private static ExecutorService getExecutor() {
		if (sBitmapFileCleanerExecutor == null) {
			sBitmapFileCleanerExecutor = Executors.newSingleThreadExecutor(
					new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							Thread thread = new Thread(r);
							thread.setName(TAG + " | " + thread.getName());
							thread.setPriority(Thread.MIN_PRIORITY);
							return thread;
						}
					});
		}
		return sBitmapFileCleanerExecutor;
	}

	@Override
	protected Void doWorkInBackground() throws Exception {
		mCleanupDuration = System.currentTimeMillis();
		DebugLog.d(TAG, "started file cleanup");
// TODO		
//		File photoCacheDir = new File(Photo.CACHE_DIRECTORY);
//		
//		try {
//			// If we are above the max cache size, delete files until we are below the limit
//			long cacheDirSize = FileUtils.sizeOfDirectory(photoCacheDir);
//			if (cacheDirSize > MAX_DISK_CACHE_SIZE) {
//				List<File> files = getFilesSortedByLastModifiedReverse(photoCacheDir);
//				for (File file : files) {
//					if (cacheDirSize < MAX_DISK_CACHE_SIZE) {
//						break;
//					} else {
//						cacheDirSize -= FileUtils.sizeOf(file);
//						FileUtils.deleteQuietly(file);
//					}
//				}
//			}
//		} catch (Throwable t) {
//			// We NEVER want to crash while simply performing cleaning
//			Log.w(TAG, "Unable to complete photo cache clean-up.", t);
//		}
		return null;
	}

	private static List<File> getFilesSortedByLastModifiedReverse(File dir) {
		DebugLog.d(TAG, "starting getFilesSortedByLastModifiedReverse");
		File[] filesArray = dir.listFiles();
		Arrays.sort(filesArray, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		DebugLog.d(TAG, "finshed getFilesSortedByLastModifiedReverse");
		return Arrays.asList(filesArray);
	}
	
	@Override
	protected void onCompleted(Void result) {
		// Nothing
		mCleanupDuration = System.currentTimeMillis() - mCleanupDuration;
		DebugLog.d(TAG, "finished file cleanup: "+mCleanupDuration+"ms");
	}

	@Override
	protected void onFailed(Exception e) {
		// Nothing
		mCleanupDuration = System.currentTimeMillis() - mCleanupDuration;
		DebugLog.d(TAG, "file cleanup failed: "+mCleanupDuration+"ms");
	}
}
