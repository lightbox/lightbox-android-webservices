/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.tasks;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

/** 
 * A {@link BackgroundTask} that helps you keep a {@link WeakReference} to an Activity, Service, or whatever you want.
 * This will helps you avoid memory leaks.
 * @author Fabien Devos
 */
public abstract class BackgroundTaskWeak<TReference, TResult> extends BackgroundTask<TResult> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "BackgroundTaskWeak";

	private WeakReference<TReference> mWeakReference;

	//----------------------------------------------
	// Constructors
	
	/**
	 * Constructor.
	 */
	public BackgroundTaskWeak(TReference ref) {
		this(ref, null);
	}

	/**
	 * Constructor.
	 */
	public BackgroundTaskWeak(TReference ref, ExecutorService executor) {
		super(executor);
		mWeakReference = new WeakReference<TReference>(ref);
	}

	//----------------------------------------------
	// Reference

	@Override
	public void cancel() {
		super.cancel();
		mWeakReference.clear();
	}
	
	/**
	 * @return The weakly referenced object, or null if it doesn't exist anymore.
	 */
	public TReference getRef() {
		return mWeakReference.get();
	}
	
	/**
	 * Set an object as weakly referenced. Will erase the previously set one.
	 * @param ref the reference to set
	 */
	public void setRef(TReference ref) {
		mWeakReference.clear();
		mWeakReference = new WeakReference<TReference>(ref);
	}
	

}
