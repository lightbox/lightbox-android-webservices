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
