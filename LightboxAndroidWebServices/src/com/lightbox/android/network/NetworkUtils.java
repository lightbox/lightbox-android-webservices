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
package com.lightbox.android.network;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

/** 
 * NetworkUtils 
 * @author Fabien Devos
 */
public final class NetworkUtils {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "NetworkUtils";
	
    //------------------------------------------------------
    // Private constructor for utility class
    private NetworkUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------
    
    public static boolean isNetworkException(Exception e) {
    	return e instanceof UnknownHostException
    		|| e instanceof SocketTimeoutException
    		|| e instanceof HttpHostConnectException
    		|| e instanceof ConnectionPoolTimeoutException
    		|| e instanceof SSLPeerUnverifiedException;    	
    }
}
