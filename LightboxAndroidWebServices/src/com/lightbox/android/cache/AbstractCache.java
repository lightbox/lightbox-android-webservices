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
