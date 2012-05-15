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
package com.lightbox.android.location;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.lightbox.android.utils.debug.DebugLog;

/** 
 * LocationHelper 
 * @author Fabien Devos & Nilesh Patel
 */
public class LocationHelper implements android.location.LocationListener {
	/** Used to tag logs */
	//@SuppressWarnings("unused")
	private static final String TAG = "LocationHelper";

	private static long LOCATION_VALIDITY_DURATION_MS = 2 * 60 * 1000; // 2 min 
	
	private WeakReference<LocationListener> mListenerWeak;
	private LocationManager mLocationManager;
	
	private final static int LOCATION_TIMEOUT_MS = 30 * 1000; // 30 seconds
	private Handler mHandler;
	
	/**
	 * Constructor.
	 */
	public LocationHelper(Context context, LocationListener listener) {
		mListenerWeak = new WeakReference<LocationListener>(listener);
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	private boolean isGpsLocationProviderEnabled() {
		try {
        	return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        	DebugLog.d(TAG, e.getMessage());
        }
		
		return false;
	}
	
	private boolean isNetworkLocationProviderEnabled() {
		try {
        	return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
        	DebugLog.d(TAG, e.getMessage());
        }
		
		return false;
	}
	
	//----------------------------------------------
	// Public API
	
	/** Start retrieving location, caching it for a few minutes. */
	public void startRetrievingLocation() {		
		Location location = getLastKnownLocation();
		// If the last known location is recent enough, just return it, else request location updates
		if (location != null && (System.currentTimeMillis() - location.getTime()) <= LOCATION_VALIDITY_DURATION_MS) {
			LocationListener listener = mListenerWeak.get();
			if (listener != null) {
				listener.onFoundLocation(location);
			}
		} else {
			// Don't start listeners if no provider is enabled
	        if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) {
	        	LocationListener listener = mListenerWeak.get();
				if (listener != null) {
					listener.onUnableToFindLocation(new IllegalStateException("No provider available"));
				}
	            return;
	        }
	                
	        if (isGpsLocationProviderEnabled()) {
	            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	        }
	        if (isNetworkLocationProviderEnabled()) {
	        	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	        }
	        
	        mHandler = new Handler();
	        mHandler.postDelayed(new GetLastLocation(this), LOCATION_TIMEOUT_MS);
		}
	}
	
	public void stopRetrievingLocation() {
		mLocationManager.removeUpdates(this);
	}
	
	/** Get last known location even if this location is old. */
	public Location getLastKnownLocation() {		
		Location netLoc = null;
        Location gpsLoc = null;
        if (isGpsLocationProviderEnabled()) {
            gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (isNetworkLocationProviderEnabled()) {
            netLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // If there are both values use the latest one
        if (gpsLoc != null && netLoc != null){
            if (gpsLoc.getTime() > netLoc.getTime()) {
                return gpsLoc;
            } else {
                return netLoc;
            }
        }

        if (gpsLoc != null) {
        	return gpsLoc;
        }
        if (netLoc != null) {
            return netLoc;
        }
    	return null;
	}
	
	/** Get address from location */
	public static String getReverseGeocodedAddress(Context context, Location location) {
		String addrStr = "";
		
		Geocoder geocoder = new Geocoder(context);
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			StringBuilder addressStrBuilder = new StringBuilder();
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				for (int i = 0; i < address.getMaxAddressLineIndex() && i < 1; i++) {
					addressStrBuilder.append(address.getAddressLine(i) + " ");
				}
				addrStr = addressStrBuilder.toString();
			}
		} catch (IOException e) {
			Log.w(TAG,"Unable to retrieve reverse geocoded address.", e);
		}
		return addrStr;
	}
	
	public static String getReverseGeocodedApproxArea(Context context, Location location) {
		return getReverseGeocodedApproxArea(context, location.getLongitude(), location.getLatitude());
	}
	
	public static String getReverseGeocodedApproxArea(Context context, double longitude, double latitude) {
		String area = null;
		
		Geocoder geocoder = new Geocoder(context);
		try {
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 3);
			int numAddresses = addresses.size();
			for (int i = 0; i < numAddresses; i++) {
				Address address = addresses.get(i);
				
				if (area == null) {
					area = address.getSubLocality();
				}
				if (area == null) {
					area = address.getLocality();
				}
				if (area == null) {
					area = address.getSubAdminArea();
				}
				if (area == null) {
					area = address.getAdminArea();
				}
				
				if (area != null) {
					return area;
				}
			}
		} catch (IOException e) {
			Log.w(TAG,"Unable to retrieve reverse geocoded address.", e);
		}
		return "";
	}
	
	//----------------------------------------------
	// Location Listener

	@Override
	public void onLocationChanged(Location location) {
		// Only update once
		mLocationManager.removeUpdates(this);
		
		LocationListener listener = mListenerWeak.get();
		if (listener != null) {
			listener.onFoundLocation(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) { //Only if both are no longer available
			mLocationManager.removeUpdates(this);
			
			LocationListener listener = mListenerWeak.get();
			if (listener != null) {
				listener.onUnableToFindLocation(new IOException("Unable to find location, provider disabled: " + provider));
			}
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		// Nothing to do
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) { //Only if both are no longer available
				// Only update once
				mLocationManager.removeUpdates(this);
				
				LocationListener listener = mListenerWeak.get();
				if (listener != null) {
					listener.onUnableToFindLocation(new IOException("Unable to find location, provider unavailable or out of service: " + provider));
				}
			}
		}
	}
	
	private static class GetLastLocation implements Runnable {
		WeakReference<LocationHelper> mRef;

		public GetLastLocation(LocationHelper helper) {
			mRef = new WeakReference<LocationHelper>(helper);
		}

		@Override
		public void run() {
			LocationHelper helper = mRef.get();

			if (helper != null) {

				helper.mLocationManager.removeUpdates(helper);

				Location location = helper.getLastKnownLocation();
				LocationListener listener = helper.mListenerWeak.get();
				if (listener != null) {
					if (location != null) {
						listener.onFoundLocation(location);
					} else {
						listener.onUnableToFindLocation(new Exception("No last known location"));
					}
				}
			}
		}
	}
	
}
