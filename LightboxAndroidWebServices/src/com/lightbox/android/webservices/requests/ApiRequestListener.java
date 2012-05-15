/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.requests;


/** 
 * ApiRequestListener 
 * @author Fabien Devos
 */
public interface ApiRequestListener {
	
	void onSuccess(Object result);
	
	void onFailure(Exception e);

}
