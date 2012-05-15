/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/** 
 * AndroidUtils 
 * @author Fabien Devos
 */
public final class AndroidUtils {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "AndroidUtils";
		
	private static Context sApplicationContext = null;
	
    //------------------------------------------------------
    // Private constructor for utility class
    private AndroidUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }
    //------------------------------------------------------

    //----------------------------------------------
	// Set/Get application context
    
    public static void setApplicationContext(Context context) {
    	sApplicationContext = context.getApplicationContext();
    }

    public static Context getApplicationContext() {
    	return sApplicationContext;
    }
    
    //----------------------------------------------
	// Retrieving useful informations

    /**
     * @return the ANDROID_ID that identify the device, or the "emulator" string on the emulator.
     */
    public static String getAndroidId(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (androidId == null || androidId.length() <= 0) {
            androidId = "emulator";
        }
        return androidId;
    }

	/**
	 * @return the name of the app (as defined in the "label" attribute in the manifest)
	 */
	public static String getApplicationLabel() {
		Context context = sApplicationContext;
		String applicationLabel = "Unknown app";
		if (context != null) {
			applicationLabel = getApplicationLabel(context);
		}
		return applicationLabel;
	}
    
	/**
	 * @return the name of the app (as defined in the "label" attribute in the manifest)
	 */
	public static String getApplicationLabel(Context context) {
		ApplicationInfo appInfo = context.getApplicationInfo();
    	return (String) context.getPackageManager().getApplicationLabel(appInfo);
	}
    
	/**
	 * @return the version name of the application
	 */
	public static String getVersionName(Context context) {
		String versionName = "unknown version";
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.w(TAG, e);
		}
		return versionName;
	}
	
	/**
	 * @return the version code of the application
	 */
	public static int getVersionCode() {
		Context context = sApplicationContext;
		int versionCode = -1;
		if (context != null) {
			versionCode = getVersionCode(context);
		}
		return versionCode;
	}
	
	/**
	 * @return the version code of the application
	 */
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionCode = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			Log.w(TAG, e);
		}
		return versionCode;
	}
	
	/**
	 * @return Android SDK, Version, Manufacturer, Model, Device
	 */
	public static String getDeviceInfo() {
		String info = "Android SDK: "
			+ android.os.Build.VERSION.SDK_INT + " Version: "
			+ android.os.Build.VERSION.RELEASE + " Manufacturer: "
			+ android.os.Build.MANUFACTURER + " Model: "
			+ android.os.Build.MODEL + " Device: "
			+ android.os.Build.DEVICE;
		return info;
	}
	
    
    /**
     * @return true if the app is debuggable, false otherwise
     */
    public static boolean isDebuggable(Context context) {
        return ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }
    
    /** 
     * @return true if this device has a camera  
     */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    
    /**
     * @return true if this device has Amazon Market App installed
     */
    public static boolean hasAmazonMarketApp(Context context) {
		try {
			context.getPackageManager().getPackageInfo("com.amazon.venezia", 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
    }
    
    //----------------------------------------------
	// Input
    
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
