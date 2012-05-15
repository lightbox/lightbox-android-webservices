/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.processors;

/** 
 * GenerationException 
 * @author Fabien Devos
 */
@SuppressWarnings("serial")
public class GenerationException extends Exception {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "GenerationException";
	
	/**
	 * Constructor.
	 */
	public GenerationException(Throwable t) {
		super(t);
	}
}
