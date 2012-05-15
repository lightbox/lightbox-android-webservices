package com.lightbox.tweetsnearby.model;

import java.util.List;

/** 
 * Venue 
 * @author Fabien Devos
 */
public class Venue {
	private String mId;
	private String mName;
	private Location mLocation;
	private List<Category> mCategories;
	
    public String getId() {
		return mId;
	}
	public void setId(String id) {
		mId = id;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public Location getLocation() {
		return mLocation;
	}
	public void setLocation(Location location) {
		mLocation = location;
	}
	public List<Category> getCategories() {
		return mCategories;
	}
	public void setCategories(List<Category> categories) {
		mCategories = categories;
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
	/**************************************
	 * Category
	 */
	public static class Category {
		private String mName;
		public String getName() {
			return mName;
		}
		public void setName(String name) {
			mName = name;
		}
	}
	/**************************************
	 * Location
	 */
	public static class Location {
    	private String mAddress;
    	private double mLat;
    	private double mLong;
    	private int    mDistance;
    	private String mCrossStreet;
    	private String mPostalCode;
    	private String mCity;
    	private String mState;
    	private String mCountry;
		public String getAddress() {
			return mAddress;
		}
		public void setAddress(String address) {
			mAddress = address;
		}
		public double getLat() {
			return mLat;
		}
		public void setLat(double lat) {
			mLat = lat;
		}
		public double getLng() {
			return mLong;
		}
		public void setLng(double l) {
			mLong = l;
		}
		public int getDistance() {
			return mDistance;
		}
		public void setDistance(int distance) {
			mDistance = distance;
		}
		public String getCrossStreet() {
			return mCrossStreet;
		}
		public void setCrossStreet(String crossStreet) {
			mCrossStreet = crossStreet;
		}
		public String getPostalCode() {
			return mPostalCode;
		}
		public void setPostalCode(String postalCode) {
			mPostalCode = postalCode;
		}
		public String getCity() {
			return mCity;
		}
		public void setCity(String city) {
			mCity = city;
		}
		public String getState() {
			return mState;
		}
		public void setState(String state) {
			mState = state;
		}
		public String getCountry() {
			return mCountry;
		}
		public void setCountry(String country) {
			mCountry = country;
		}
    }
    
}