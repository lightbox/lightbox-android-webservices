/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.responses;

/** 
 * ApiResponse 
 * @author Fabien Devos
 */
public interface ApiResponse<TContent> {

	TContent getContent();

	boolean hasError();
	
	ApiException getException();
	
	Object getContext();
	
}
