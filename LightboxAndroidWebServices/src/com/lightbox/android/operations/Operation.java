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
package com.lightbox.android.operations;

import java.util.List;

import com.lightbox.android.webservices.requests.ApiRequest;


/** 
 * This is the high level interface representing an Operation. An Operation is a generic asynchronous action.
 * The abstract implementation {@link AbstractOperation}n-, and the subclasses like {@link RetrieveOperation}
 * will helps transparently performing action that requires both API calls and local data modification. Theses
 * implementation will gracefully fall-back to "marking" data as deleted/updated, or retrieving data locally 
 * if the network is not available. 
 * @author Fabien Devos
 * @param <T> the type of data to operate on
 */
public interface Operation<T> {

	/********************************
	 * Universal representation of the order, useful in both SQL queries and API requests.
	 */
	public enum Order {
		ASC(true, "asc"),
		DESC(false, "desc");
		
		private boolean mBoolValue;
		private String mStrValue;
		Order(boolean boolValue, String strValue) {
			mBoolValue = boolValue;
			mStrValue = strValue;
		}
		public boolean asBool() {
			return mBoolValue;
		}
		@Override
		public String toString() {
			return mStrValue;
		}
	}
	
	//----------------------------------------------
	// Operation interface
	
	public abstract boolean isRunning();
	
	public abstract void cancel();
	
	public abstract String getId();

	public abstract ApiRequest getApiRequest();
	
	public abstract Object getContext();

	/** Must be called from main thread.
	 *  Most of the time, you won't need this method. It is provided as a convenient way to check if an operation is
	 *  already running and re-attach to it if so, with a single line of code.
	 *  <strong>Please note that the re-attachment is done automatically anyway if you simply call
	 *  {@link #executeAsync(OperationListener)} as usual.</strong>
	 *  Thus, this method is equivalent to call {@link #executeAsync(OperationListener)}
	 *  only if {@link #isRunning()} is true.
	 *  @return true if the Operation was already running and as been re-attached to the listener, false otherwise. */
	public abstract boolean reattachIfRunning(OperationListener<T> listener);

	/** <strong>Must be called from main thread.</strong> Start the asynchronous execution of this {@link Operation} 
	 * @param listener the listener to call back when finished (usually an Activity). <strong>Note that the reference
	 * to this listener will be kept weakly! It means that if you, or the system, do not hold a reference to this
	 * listener, you won't be called back.</strong>*/
	public abstract void executeAsync(OperationListener<T> listener);
	
	/**
	 * Execute this operation synchronously. Note that this will be called automatically by the
	 * {@link #executeAsync(OperationListener)} method in a <strong>background thread</strong>.
	 */
	public abstract List<T> executeSync() throws Exception;

	/**
	 * Execute only the local operation (on database or file).
	 */
	public abstract List<T> executeLocalOperationSync() throws Exception;

	/**
	 * Execute only the server operation.
	 */
	public abstract List<T> executeServerOperationSync() throws Exception;
	
}