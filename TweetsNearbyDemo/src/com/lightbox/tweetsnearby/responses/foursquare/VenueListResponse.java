/**
 * Copyright (c) 2011 Lightbox
 */
package com.lightbox.tweetsnearby.responses.foursquare;

import java.util.HashMap;
import java.util.List;

import com.lightbox.tweetsnearby.model.Venue;

/** 
 * VenueListResponse 
 * @author Fabien Devos
 */
public class VenueListResponse extends FoursquareApiResponse<List<Venue>> {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "VenueListResponse";

	protected void setResponse(ResponseWrapper response) {
		List<Venue> venues = response.getVenues();
		setContent(venues);
	}
	
	/********************
	 * ResponseWrapper 
	 */
	protected static class ResponseWrapper {
		private List<Venue> mVenues;
		private HashMap<String, Object> mGeocode;
		public List<Venue> getVenues() {
			return mVenues;
		}
		public void setVenues(List<Venue> venues) {
			mVenues = venues;
		}
		public HashMap<String, Object> getGeocode() {
			return mGeocode;
		}
		public void setGeocode(HashMap<String, Object> geocode) {
			mGeocode = geocode;
		}
	}
}
