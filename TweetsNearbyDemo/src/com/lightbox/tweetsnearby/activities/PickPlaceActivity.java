/**
 * Copyright (c) 2012 Lightbox
 */
package com.lightbox.tweetsnearby.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lightbox.android.webservices.requests.ApiRequest;
import com.lightbox.android.webservices.requests.ApiRequestListener;
import com.lightbox.android.webservices.requests.ApiRequests;
import com.lightbox.tweetsnearby.R;
import com.lightbox.tweetsnearby.model.Venue;

/** 
 * PickPlaceActivity 
 * @author Fabien Devos
 */
public class PickPlaceActivity extends Activity implements ApiRequestListener, OnItemClickListener {
	/** Used to tag logs */
	@SuppressWarnings("unused")
	private static final String TAG = "PickPlaceActivity";
	
	public static final String PLACE_NAME_KEY = "com.lightbox.wsdemo.activities.PickPlaceActivity.placeName";

	private VenueListAdapter mAdapter;
	private List<Venue> mVenues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.places_list);
		
		mVenues = new ArrayList<Venue>();
        mAdapter = new VenueListAdapter(this, mVenues);
        ((ListView) findViewById(R.id.list)).setAdapter(mAdapter);
        ((ListView) findViewById(R.id.list)).setOnItemClickListener(this);
        
        String placeName = getIntent().getStringExtra(PLACE_NAME_KEY);
        retrievePlaces(placeName);
	}
	
	//----------------------------------------------
	// Retrive places
	
	private void retrievePlaces(String placeName) {
        ApiRequest apiRequest = ApiRequests.defaultApiRequestFactory(this).createApiRequest("getFoursquarePlaces");
        apiRequest.addUrlParameter("near", placeName);
        apiRequest.addUrlParameter("client_id", "SFL4QT4ZJQUAWW0EHXJB4NDZKLG4YYIFXTATQTZAKKW5G1VN");
        apiRequest.addUrlParameter("client_secret", "JBDNFXVPWHX2P4OTXWOSPNDMMQ25P4YWLM0PU4UOPER5B5CK");
        apiRequest.addUrlParameter("v", "20111028");
        apiRequest.addUrlParameter("limit", "20");
		apiRequest.executeAsync(this);
	}
	
	@Override
	public void onFailure(Exception exception) {
		Toast.makeText(this, String.format("Unable to retrieve places: %s", exception), Toast.LENGTH_LONG).show();		
	}

	@Override
	public void onSuccess(Object result) {
		@SuppressWarnings("unchecked")
		List<Venue> venues = (List<Venue>) result;
		mVenues.clear();
		mVenues.addAll(venues);
		mAdapter.notifyDataSetChanged();		
	}
	
	//----------------------------------------------
	// On Click Place

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Venue venue = mAdapter.getItem(position);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.PLACE_NAME_KEY, venue.getName());
		intent.putExtra(MainActivity.LATITUDE_KEY, venue.getLocation().getLat());
		intent.putExtra(MainActivity.LONGITUDE_KEY, venue.getLocation().getLng());
		startActivity(intent);
	}	
	
	/********************************
	 * TweetListAdapter 
	 */
	private static class VenueListAdapter extends ArrayAdapter<Venue> {

		public VenueListAdapter(Context context, List<Venue> venues) {
			super(context, android.R.layout.simple_list_item_1, android.R.id.text1, venues);
		}
				
	}

}
