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

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;

/** 
 * IntentUtils 
 * @author Fabien Devos
 */
public final class IntentUtils {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "IntentUtils";
	
	private static final String MARKET_APP_URI_FORMAT = "market://details?id=%s";
	private static final String MARKET_WEB_URI_FORMAT = "http://market.android.com/details?id=%s";
	//private static final String AMAZON_MARKET_WEB_URI_FORMAT = "http://www.amazon.com/gp/mas/dl/android?p=%s";
	
	public static final String JPEG_MIME_TYPE = "image/jpeg";
	
    //------------------------------------------------------
    // Private constructor for utility class
    private IntentUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------

    public static Intent buildShareUrlIntent(String url, String subject) {
    	Intent shareIntent = new Intent(Intent.ACTION_SEND);
    	shareIntent.putExtra(Intent.EXTRA_TEXT, url);
    	if (subject != null) {
    		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    	}
        shareIntent.setType("text/plain");
    	return shareIntent;
    }
    
    public static Intent buildSharePhotoIntent(Context context, File photoFile, String title, String text) {
    	Intent shareIntent = null;
    	if (photoFile.exists()) {
    		shareIntent = new Intent(Intent.ACTION_SEND);
        	shareIntent.setType(JPEG_MIME_TYPE);
	    	shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
	    				
			// Subject and Text are used by the Gmail app
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
			shareIntent.putExtra(Intent.EXTRA_TEXT, text);
			
			// Title is used by the Facebook app (TODO: doesn't seams to work in latest version of the Facebook app)
			shareIntent.putExtra(Intent.EXTRA_TITLE, title);	    	
    	} 
    	return shareIntent;
    }

    public static Intent buildTakePhotoIntent(Intent intent) {
        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
        intentTakePhoto.putExtras(intent.getExtras());
        return intentTakePhoto;
    }
    
    public static boolean isTakePhotoIntent(Intent intent) {
    	String action = intent.getAction();
		return (action != null
					&& (MediaStore.ACTION_IMAGE_CAPTURE.equals(action)
						|| MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA.equals(action))
					&& intent.hasExtra(MediaStore.EXTRA_OUTPUT));
		
    }
    
    public static Intent buildPickPhotoIntent() {
		Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		return pickPhotoIntent;
    }
    
    public static Intent buildMotdIntent(Context context, String href) {
    	// Determine if it's a market intent, or just a regular URL
    	if(href.equals(String.format(MARKET_APP_URI_FORMAT, context.getPackageName()))) {
    		return buildViewLightboxMarketDetailsIntent(context);
    	} 
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	return intent.setData(Uri.parse(href));
    }
    
    public static Intent buildViewLightboxMarketDetailsIntent(Context context) {
    	return buildViewMarketDetailsIntent(context, context.getPackageName());
    }
    
    public static Intent buildViewCameraMarketDetailsIntent(Context context) {
    	return buildViewMarketDetailsIntent(context, "com.lightbox.android.camera");
    }
    
    public static Intent buildViewMarketDetailsIntent(Context context, String packageName) {    	
    	// Build intent to view app details with the market app
        Intent viewMarketAppIntent = new Intent(Intent.ACTION_VIEW);
        viewMarketAppIntent.setData(Uri.parse(String.format(MARKET_APP_URI_FORMAT, packageName)));
        
    	// If this device can resolve the intent, return it
        if (canResolveIntent(context, viewMarketAppIntent)) {
        	return viewMarketAppIntent;
        } else {
        	// Else, fall-back to the online market Intent that cannot fail
            Intent viewMarketWebIntent = new Intent(Intent.ACTION_VIEW);
            viewMarketWebIntent.setData(Uri.parse(String.format(MARKET_WEB_URI_FORMAT, packageName)));	
            return viewMarketWebIntent;
        }
    }
    
	/**
	 * Intent for launching a web browser with url
	 */
	public static Intent buildWebIntent(Context context, String url) {
		return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	}
    
	public static Intent buildMapIntent(Context context, double longitude, double latitude, String name) {
    	String queryParameter = latitude+","+longitude+(name.length() > 0 ? "("+name+")" :  "");
    	Uri mapsApplicationUri = Uri.parse("geo:"+ latitude + "," + longitude +"?z=18&q="+ queryParameter);
    	Intent mapsIntent = new Intent(android.content.Intent.ACTION_VIEW, mapsApplicationUri);
    	if(!canResolveIntent(context, mapsIntent)) {
    		// If the device doesn't have Google Maps, open the web browser as a fallback
    		mapsIntent.setData(Uri.parse("http://maps.google.com/?q=" + queryParameter));
    	}
    	return mapsIntent;
    }
	
    public static boolean canResolveIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return (resolveInfo.size() > 0);
    }
    
    public static List<ResolveInfo> getAppListWithoutCurrentOne(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		final List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);
				
		// filter out current app
		for (ResolveInfo info : appList) {
			if (context.getPackageName().equals(info.activityInfo.packageName)) {
				appList.remove(info);
				break;
			}
		}
		
		return appList;
    }
}
