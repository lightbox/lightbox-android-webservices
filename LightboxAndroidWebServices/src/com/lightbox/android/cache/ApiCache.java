/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.cache;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.util.Log;

import com.lightbox.android.utils.AndroidUtils;

/** 
 * ApiCache 
 * @author Fabien Devos
 */
public class ApiCache implements Cache<Object, String> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "ApiCache";
	
	public static final int MAX_ENTRIES_IN_MEMORY = 100;

	private final String CACHE_DIR;
	private static final Result<String> EMPTY_RESULT = new Result<String>(null, 0);

	//----------------------------------------------------------------------------
	// Singleton pattern
	private ApiCache() {
		Context context = AndroidUtils.getApplicationContext();
		if (context == null) {
			throw new IllegalStateException("You must set an Application context in ANdroidUtils before attempting to use the ApiCache.");
		}
		
		CACHE_DIR = context.getCacheDir().getAbsolutePath() + "/";
	}

	/** ApiCacheHolder is loaded on the first execution of ApiCache.getInstance() 
	 * or the first access to ApiCacheHolder.INSTANCE, not before. */
	private static class ApiCacheHolder {
		private static final ApiCache INSTANCE = new ApiCache();
	}

	/** @return a unique instance of the class */
	public static ApiCache getInstance() {
		return ApiCacheHolder.INSTANCE;
	}

	/** Not supported
	 * @throws CloneNotSupportedException (every time) */
	public Object clone() throws CloneNotSupportedException {
		// to prevent any kind of cheating
		throw new CloneNotSupportedException();
	}
	//----------------------------------------------------------------------------
	
	@Override
	public void clear() {
		clearDisk();
	}

	//----------------------------------------------
	// Memory cache methods (not supported)
	
	@Override
	public void clearMemory() {
		throw new UnsupportedOperationException("No API memory cache");
	}
		
	@Override
	public Object getFromMemory(String key) {
		throw new UnsupportedOperationException("No API memory cache");
	}
	
	@Override
	public void putInMemory(String key, Object data) {
		throw new UnsupportedOperationException("No API memory cache");
	}
	
	//----------------------------------------------
	// Disk cache methods
	
	@Override
	public boolean existOnDisk(String key) {
		try {
			return getFile(key).exists();
		} catch (IOException e) {
			Log.w(TAG, e);
			return false;
		}
	}
	
	@Override
	public Result<String> getFromDisk(String key, Object... objects) {
		Result<String> result = EMPTY_RESULT;
		
		if (key != null) {
			try {
				File file = getFile(key);
				if (file.exists()) {
					long updatedTime = file.lastModified();
					String string = FileUtils.readFileToString(file);
					result = new Result<String>(string, updatedTime);			
				}
			} catch (IOException e) {
				Log.w(TAG, e);
				// Will return empty result
			}
		}
		return result;
	}

	@Override
	public void putOnDisk(String key, String string) {
		if (key != null && string != null) {
			try {
				File file = getFile(key);
				FileUtils.writeStringToFile(file, string);
			} catch (Exception e) {
				// ignore exception since this is just a cache
				Log.w(TAG, "Unable to save api result", e);
			}
		}
	}

	public void clearFromDisk(String key) {
		if (key != null) {
			try {
				File file = getFile(key);
				FileUtils.deleteQuietly(file);
			} catch (Exception e) {
				Log.w(TAG, "Unable to clear entry", e);
			}
		}
	}
	
	private File getFile(String key) throws IOException {
		//DebugLog.d(TAG, "Encoding key for cache: " + key);
		return new File(CACHE_DIR + URLEncoder.encode(key, "UTF-8"));
	}	
	
	@Override
	public void clearDisk() {
		FileUtils.deleteQuietly(new File(CACHE_DIR));
	}
	
	@Override
	public void startDiskCleanup() {
		// TODO ApiCache clean-up
	}


}
