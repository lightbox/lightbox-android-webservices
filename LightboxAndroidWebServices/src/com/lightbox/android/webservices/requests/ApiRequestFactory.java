/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.requests;

/** 
 * ApiRequestFactory 
 * @author Fabien Devos
 */
public interface ApiRequestFactory {
	
	/**
	 * Create a new {@link ApiRequest}, already set up according to the method name provided.
	 * You will only need to add URL parameters, body and so on before using the ApiRequest. 
	 */
	ApiRequest createApiRequest(String methodName);

	/**
	 * Override the base URL for certain requests, according to a "key". See implementation for details on how the
	 * key is actually used to match certain requests.
	 */
	void overrideBaseUrlForKey(String key, String baseUrl);

	/**
	 * Retrieve the base URL for the given key. Regardless of whether it was overridden or not.
	 */
	String retrieveBaseUrlForKey(String key);
}
