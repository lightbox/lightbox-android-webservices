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

import com.googlecode.concurrentlinkedhashmap.Weigher;

/** 
 * Cache 
 * @author Fabien Devos
 */
public interface Cache<TMem, TDisk> {
	
	//----------------------------------------------
	// Two level Caching

	/** Clear both memory and disk caches. */
	void clear();
	
	TMem getFromMemory(String key);
	
	void putInMemory(String key, TMem data);
	
	void clearMemory();
	
	boolean existOnDisk(String key);
	
	Result<TDisk> getFromDisk(String key, Object... objects);
	
	void putOnDisk(String key, TDisk data);
	
	void clearDisk();
	
	void startDiskCleanup();

	/****************************************
	 * Config for cache creation
	 */
	public static class Config<T> {

		private Weigher<T> mWeigher;
		private int mMaximumWeightedCapacity;
		
		//----------------------------------------------
		// Memory cache configuration

		Config<T> setWeigher(Weigher<T> weigher) {
			mWeigher = weigher;
			return this;
		}
		
		public Weigher<T> getWeigher() {
			return mWeigher;
		}
		
		Config<T> setMaximumWeightedCapacityInMemory(int maximumWeightedCapacity) {
			mMaximumWeightedCapacity = maximumWeightedCapacity;
			return this;
		}

		public int getMaximumWeightedCapacity() {
			return mMaximumWeightedCapacity;
		}
		
	}
	
	/****************************************
	 * Result coming from the cache
	 */
	public static class Result<T> {
		private long mUpdatedTime;
		private T mData;
		/**
		 * Constructor.
		 */
		public Result(T data, long updatedTime) {
			mData = data;
			mUpdatedTime = updatedTime;
		}
		public long getUpdatedTime() {
			return mUpdatedTime;
		}
		public T getData() {
			return mData;
		}
	}
	
}
