/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.cache;

import java.util.concurrent.ConcurrentMap;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;

/** 
 * AbstractCache 
 * @author Fabien Devos
 */
public abstract class AbstractCache<TMem, TDisk> implements Cache<TMem, TDisk> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "AbstractCache";
	
	private ConcurrentMap<String, TMem> mMemoryCache;
	private Config<TMem> mConfig;
	
	/**
	 * Constructor.
	 */
	public AbstractCache(Config<TMem> config) {
		mConfig = config;
		resetMemoryCache(mConfig.getMaximumWeightedCapacity());
	}
	
	protected void resetMemoryCache(int maximumWeightedCapacity) {
		Builder<String, TMem> builder = new ConcurrentLinkedHashMap.Builder<String, TMem>()
			.maximumWeightedCapacity(maximumWeightedCapacity);
		if (mConfig.getWeigher() != null) {
			builder.weigher(mConfig.getWeigher());
		}
		mMemoryCache = builder.build();
	}
	
	@Override
	public void clear() {
		clearMemory();
		clearDisk();
	}
	
	@Override
	public void clearMemory() {
		mMemoryCache.clear();
	}
		
	@Override
	public TMem getFromMemory(String key) {
		if (key == null) { return null; }
		return mMemoryCache.get(key);
	}
	
	@Override
	public void putInMemory(String key, TMem data) {
		if (data != null && key != null) {
			mMemoryCache.put(key, data);
		}
	}

	public void removeFromMemory(String key) {
		mMemoryCache.remove(key);
	}
}
