/**
 * Copyright (c) 2012 Lightbox
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
