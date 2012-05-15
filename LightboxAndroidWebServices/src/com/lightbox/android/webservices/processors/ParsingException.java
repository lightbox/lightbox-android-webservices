/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.processors;


/** 
 * ParsingException 
 * @author Fabien Devos
 */
@SuppressWarnings("serial")
public class ParsingException extends Exception {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "ParsingException";
	
	/**
	 * Constructor.
	 */
	public ParsingException(Throwable t) {
		super(t);
	}

}
