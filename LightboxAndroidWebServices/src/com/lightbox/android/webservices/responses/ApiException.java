/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.responses;

/** 
 * ApiException 
 * @author Fabien Devos
 */
@SuppressWarnings("serial")
public class ApiException extends Exception {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "ApiException";
	
	private int mCode;

	//----------------------------------------------
	// Constructors
	
	/**
	 * Constructor.
	 */
	public ApiException(int code, String message) {
		super(message);
		mCode = code;
	}
	
	//----------------------------------------------
	// Getters

	/**
	 * @return the code
	 */
	public int getCode() {
		return mCode;
	}
	
	
}
