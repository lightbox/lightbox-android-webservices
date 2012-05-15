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
package com.lightbox.android.utils.debug;

import android.content.res.Configuration;
import android.os.Bundle;

import com.lightbox.android.lifecycle.LifeCycleListener;
import com.lightbox.android.lifecycle.ManagedLifeCycleActivity;

/** 
 * Simple {@link LifeCycleListener} that logs every call to a life cycle method.
 * @author Fabien Devos
 */
public class DebugLifeCycleListener extends LifeCycleListener {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "DebugLifeCycleListener";
	
	@Override
	protected void onCreate(ManagedLifeCycleActivity activity, Bundle savedInstanceState) {
		DebugLog.d(TAG, "onCreate()");
	}
	
	@Override
	protected void onContentChanged(ManagedLifeCycleActivity activity) {
		DebugLog.d(TAG, "onContentChanged()");
	}
	
	@Override
	protected void onRestoreInstanceState(ManagedLifeCycleActivity activity, Bundle savedInstanceState) {
		DebugLog.d(TAG, "onRestoreInstanceState()");
	}
	
	@Override
	protected void onStart(ManagedLifeCycleActivity activity) {
		DebugLog.d(TAG, "onStart()");
	}

	@Override
	protected void onResume(ManagedLifeCycleActivity activity) {
		DebugLog.d(TAG, "onResume()");
	}
	
	@Override
	protected void onConfigurationChanged(ManagedLifeCycleActivity activity, Configuration newConfig) {
		DebugLog.d(TAG, "onConfigurationChanged()");
	}

	@Override
	protected void onPause(ManagedLifeCycleActivity activity) {
		DebugLog.d(TAG, "onPause()");
	}
	
	@Override
	protected void onSaveInstanceState(ManagedLifeCycleActivity activity, Bundle outState) {
		DebugLog.d(TAG, "onSaveInstanceState()");
	}

	@Override
	protected void onStop(ManagedLifeCycleActivity activity) {
		DebugLog.d(TAG, "onStop()");
	}
	
	@Override
	protected void onDestroy(ManagedLifeCycleActivity activity) {
		DebugLog.d(TAG, "onDestroy()");
	}
}
