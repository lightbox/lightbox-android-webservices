/**
 * Copyright (c) 2011 Lightbox
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
