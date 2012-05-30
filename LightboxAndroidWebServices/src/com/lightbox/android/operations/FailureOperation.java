/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.operations;

import java.util.List;

/** 
 * An Operation that always fail.
 * @author Fabien Devos
 */
public class FailureOperation<T> extends AbstractOperation<T> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "FailureOperation";
	
	private Exception mException;
	
	/**
	 * Constructor.
	 */
	public FailureOperation(Exception exception) {
		super(null, null);
		mException = exception;
	}

	@Override
	public final List<T> executeSync() throws Exception {
		throw mException;
	}

	@Override
	public List<T> executeLocalOperationSync() throws Exception {
		throw mException;
	}

	@Override
	public List<T> executeServerOperationSync() throws Exception {
		throw mException;
	}
	
	

}
