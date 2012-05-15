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
