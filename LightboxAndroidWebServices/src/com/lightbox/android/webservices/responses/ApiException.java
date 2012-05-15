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
