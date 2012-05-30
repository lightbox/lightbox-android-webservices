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

import android.util.Log;

import com.lightbox.android.tasks.BackgroundTaskWeak;
import com.lightbox.android.utils.AndroidUtils;

/** 
 * OperationTask 
 * @author Fabien Devos
 */
public class OperationTask<T> extends BackgroundTaskWeak<OperationListener<T>, List<T>> {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "OperationTask";
	
	private AbstractOperation<T> mOpseration;
	
	/**
	 * Constructor.
	 * @param ref
	 */
	public OperationTask(AbstractOperation<T> operation, OperationListener<T> listener) {
		super(listener);
		
		mOpseration = operation;
	}

	/** Must be called on main thread */
	public void setListener(OperationListener<T> listener) {
		setRef(listener);
	}
	
	//----------------------------------------------
	// Task implementation
	
	@Override
	protected List<T> doWorkInBackground() throws Exception {
		return mOpseration.performExecuteSync();
	}

	@Override
	protected void onCompleted(List<T> result) {
		OperationListener<T> listener = getRef();
		if (listener != null) {
			try {
				listener.onSuccess(mOpseration, result);
			} catch (IllegalStateException e) {
				Log.w(TAG, e);
			}
		}
	}
	
	@Override
	protected void onProgressPublished(int progress, List<T> tmpResult) {
		OperationListener<T> listener = getRef();
		if (listener != null) {
			try {
				listener.onSuccess(mOpseration, tmpResult);
			} catch (IllegalStateException e) {
				Log.w(TAG, e);
			}
		}
	}

	@Override
	protected void onFailed(Exception e) {
		OperationListener<T> listener = getRef();
		if (listener != null) {
			try {
				listener.onFailure(mOpseration, e);
			} catch (IllegalStateException ex) {
				Log.w(TAG, ex);
			}
		}

		if (AndroidUtils.isDebuggable(AndroidUtils.getApplicationContext())) {
			e.printStackTrace();
		}
	}
	

	

}
