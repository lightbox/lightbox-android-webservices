/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.lifecycle;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/** 
 * ManagedLifeCycleActivity. Allows Activities to use {@link LifeCycleListener}s.
 * @author Fabien Devos
 */
public class ManagedLifeCycleActivity extends FragmentActivity {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "AbstractActivity";
	
	//----------------------------------------------
	// LifeCycleListeners management
	
	private ArrayList<LifeCycleListener> mLifeCycleListeners;
	
	/**
	 * Give subclasses a chance to create and add {@link LifeCycleListener}s.
	 * Only call {@link #addLifeCycleListener(LifeCycleListener)} in here.
	 */
	protected void onCreateLifeCycleListeners() {
		// Do nothing by default
	}
	
	/**
	 * Add a new {@link LifeCycleListener} to this Activity. Only call this in {@link #onCreateLifeCycleListeners()}.
	 * LifeCycleListeners will be called in the order you added them.
	 * @param lifeCycleListener
	 */
	protected void addLifeCycleListener(LifeCycleListener lifeCycleListener) {
		mLifeCycleListeners.add(lifeCycleListener);
	}

	//----------------------------------------------
	// Activity Life Cycle

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLifeCycleListeners = new ArrayList<LifeCycleListener>();
		onCreateLifeCycleListeners();
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onCreate(this, savedInstanceState);
		}
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onContentChanged(this);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onRestoreInstanceState(this, savedInstanceState);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onStart(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onResume(this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onPause(this);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onConfigurationChanged(this, newConfig);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onSaveInstanceState(this, outState);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onStop(this);
		}	
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		for(LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onActivityResult(this, requestCode, resultCode, data);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		for (LifeCycleListener lifeCycleListener : mLifeCycleListeners) {
			lifeCycleListener.onDestroy(this);
		}
		
		mLifeCycleListeners.clear();
	}

}
