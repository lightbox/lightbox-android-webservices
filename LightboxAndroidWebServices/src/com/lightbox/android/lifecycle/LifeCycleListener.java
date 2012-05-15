/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.lifecycle;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/** 
 * LifeCycleListener.
 * Designed for inheritance. get called every time the associated Activity's life cycle event occurs.
 * @author Fabien Devos
 */
public abstract class LifeCycleListener {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "LifeCycleListener";
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onCreate(ManagedLifeCycleActivity activity, Bundle savedInstanceState) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onContentChanged(ManagedLifeCycleActivity activity) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onRestoreInstanceState(ManagedLifeCycleActivity activity, Bundle savedInstanceState) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onStart(ManagedLifeCycleActivity activity) {
		// Do nothing by default
	}

	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onResume(ManagedLifeCycleActivity activity) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onConfigurationChanged(ManagedLifeCycleActivity activity, Configuration newConfig) {
		// Do nothing by default
	}

	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onPause(ManagedLifeCycleActivity activity) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onSaveInstanceState(ManagedLifeCycleActivity activity, Bundle outState) {
		// Do nothing by default
	}

	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onStop(ManagedLifeCycleActivity activity) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onDestroy(ManagedLifeCycleActivity activity) {
		// Do nothing by default
	}
	
	/** Get called every time the corresponding method is called on the associated activity. Do nothing by default. */
	protected void onActivityResult(ManagedLifeCycleActivity activity, int requestCode, int resultCode, Intent data) {
		// Do nothing by default
	}

}
