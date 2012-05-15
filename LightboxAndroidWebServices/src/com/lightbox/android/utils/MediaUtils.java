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
package com.lightbox.android.utils;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

/** 
 * MediaUtils 
 * @author Fabien Devos
 */
public final class MediaUtils {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "MediaUtils";
	
    //------------------------------------------------------
    // Private constructor for utility class
    private MediaUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------
	


    /**
     * Convenience for constructing a {@link MediaScannerConnection}, calling
     * {@link #connect} on it, and calling {@link #scanFile} with the given
     * <var>path</var> and <var>mimeType</var> when the connection is
     * established.
     * @param context The caller's Context, required for establishing a connection to
     * the media scanner service.
     * Success or failure of the scanning operation cannot be determined until
     * {@link MediaScannerConnectionClient#onScanCompleted(String, Uri)} is called.
     * @param path path to be scanned.
     * @param mimeType Optional MIME type.
     * If mimeType is null, then the mimeType will be inferred from the file extension.
     */
    public static void scanFile(Context context, String path, String mimeType) {
    	LinkedList<String> paths = new LinkedList<String>();
    	paths.add(path);
    	scanFile(context, paths, mimeType);
    }

    /** @see #scanFile(Context, String, String) */
    public static void scanFile(Context context, Queue<String> paths, String mimeType) {
    	MediaScannerHelper mediaScannerHelper = new MediaScannerHelper(paths, mimeType);
    	mediaScannerHelper.start(context);
    }
    
    /************************************************
     * MediaScannerConnectionClientProxy 
     */
    private static class MediaScannerHelper implements MediaScannerConnectionClient {
        private Queue<String> mPaths;
        private String mMimeType;
        private MediaScannerConnection mConnection;
        
        public MediaScannerHelper(Queue<String> paths, String mimeType) {
            mPaths = paths;
            mMimeType = mimeType;
        }

        public void start(Context context) {
        	mConnection = new MediaScannerConnection(context, this);
        	mConnection.connect();
        }
        
        public void onMediaScannerConnected() {
            scanNextPath();
        }

        public void onScanCompleted(String path, Uri uri) {
            scanNextPath();
        }

        private void scanNextPath() {
        	if (mPaths.isEmpty()) {
        		mConnection.disconnect();
        	} else {
        		String path = mPaths.remove();
                mConnection.scanFile(path, mMimeType);
        	}
        }
    }
}
