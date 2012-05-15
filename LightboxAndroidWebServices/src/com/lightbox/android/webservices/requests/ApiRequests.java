/**
 * Copyright (c) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
