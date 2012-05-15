/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.webservices.requests;

import android.content.Context;

/** 
 * Factory and utility methods for {@link ApiRequest}s.
 * @author Fabien Devos
 */
public final class ApiRequests {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "ApiRequests";
	
	private static ApiRequestFactory sDefaultApiRequestFactory;
	
    //------------------------------------------------------
    // Private constructor for utility class
    private ApiRequests() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------

    public static ApiRequestFactory defaultApiRequestFactory(Context context) {
    	if (sDefaultApiRequestFactory == null) {
    		sDefaultApiRequestFactory = new JacksonApiRequestFactory(context);
    	}
		return sDefaultApiRequestFactory;
    }
    
    public static void setDefaultApiRequestFactory(ApiRequestFactory defaultApiRequestFactory) {
    	sDefaultApiRequestFactory = defaultApiRequestFactory;
    }
    
}
