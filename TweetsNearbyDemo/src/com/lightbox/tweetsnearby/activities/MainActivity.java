package com.lightbox.tweetsnearby.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lightbox.android.location.LocationHelper;
import com.lightbox.android.location.LocationListener;
import com.lightbox.android.tasks.BackgroundTaskWeak;
import com.lightbox.android.utils.IntentUtils;
import com.lightbox.android.views.RemoteThumbImageView;
import com.lightbox.android.webservices.requests.ApiRequest;
import com.lightbox.android.webservices.requests.ApiRequestListener;
import com.lightbox.android.webservices.requests.ApiRequests;
import com.lightbox.tweetsnearby.R;
import com.lightbox.tweetsnearby.model.Tweet;

public class MainActivity extends Activity implements ApiRequestListener, LocationListener, OnItemClickListener {
	
	public static final String PLACE_NAME_KEY = "com.lightbox.wsdemo.activities.MainActivity.placeName";
	public static final String LATITUDE_KEY = "com.lightbox.wsdemo.activities.MainActivity.latitude";
	public static final String LONGITUDE_KEY = "com.lightbox.wsdemo.activities.MainActivity.longitude";
	
	private static final String RADIUS = "10km";
	
	private TweetListAdapter mAdapter;
	private List<Tweet> mTweets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTweets = new ArrayList<Tweet>();
        mAdapter = new TweetListAdapter(this, mTweets);
        ((ListView) findViewById(R.id.list)).setAdapter(mAdapter);
        ((ListView) findViewById(R.id.list)).setOnItemClickListener(this);
        
        String placeName = getIntent().getStringExtra(PLACE_NAME_KEY);
        double latitude = getIntent().getDoubleExtra(LATITUDE_KEY, Double.NaN);
        double longitude = getIntent().getDoubleExtra(LONGITUDE_KEY, Double.NaN);		
        
        if (placeName != null && ! Double.isNaN(latitude) && ! Double.isNaN(longitude)) {
        	((TextView) findViewById(R.id.textViewLabel)).setText("Tweets around " + placeName + ":");
        	retrieveTweetsAround(latitude, longitude);
        } else {
        	retrieveLocation();
        }
    }

    //----------------------------------------------
	// 

    public void onOkButtonClicked(View v) {
    	String placeName= ((EditText) findViewById(R.id.editTextSearch)).getText().toString();
    	
    	Intent intent = new Intent(this, PickPlaceActivity.class);
    	intent.putExtra(PickPlaceActivity.PLACE_NAME_KEY, placeName);
    	startActivity(intent);
    }
    
    //----------------------------------------------
	// Retrieving location
    
    private void retrieveLocation() {
    	LocationHelper locationHelper = new LocationHelper(this, this);
    	locationHelper.startRetrievingLocation();
    }
    
    @Override
    public void onUnableToFindLocation(Exception exception) {
    	Toast.makeText(this, String.format("Unable to retrieve your location: %s", exception), Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onFoundLocation(Location location) {
    	// Get human-readable location
    	(new GetReverseGeocodedAreaTask(this, location)).execute();

    	retrieveTweetsAround(location.getLatitude(), location.getLongitude());
    }
    
    //----------------------------------------------
	// Retrieving tweets
    
    private void retrieveTweetsAround(double latitude, double longitude) {
    	String geocode = String.format(Locale.US, "%f,%f,%s", latitude, longitude, RADIUS);
    	
        ApiRequest apiRequest = ApiRequests.defaultApiRequestFactory(this).createApiRequest("searchTwitter");
        apiRequest.addUrlParameter("geocode", geocode);
        apiRequest.addUrlParameter("lang", Locale.getDefault().getLanguage());
		apiRequest.executeAsync(this);
    }
    
	@Override
	public void onFailure(Exception exception) {
		Toast.makeText(this, String.format("Unable to retrieve tweets: %s", exception), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSuccess(Object result) {
		@SuppressWarnings("unchecked")
		List<Tweet> tweets = (List<Tweet>) result;
		mTweets.clear();
		mTweets.addAll(tweets);
		mAdapter.notifyDataSetChanged();
	}
	
	//----------------------------------------------
	// OnItemClick
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Tweet tweet = mTweets.get(position);
		startActivity(IntentUtils.buildWebIntent(this, "http://twitter.com/"+tweet.getFromUser()));
	}

	/********************************
	 * GetReverseGeocodedAreaTask 
	 */
	private static class GetReverseGeocodedAreaTask extends BackgroundTaskWeak<MainActivity, String> {
		private Location mLocation;
		public GetReverseGeocodedAreaTask(MainActivity ref, Location location) {
			super(ref);
			mLocation = location;
		}

		@Override
		protected String doWorkInBackground() throws Exception {
			String area = null;
			Context context = getRef();
			if (context != null) {
				area = LocationHelper.getReverseGeocodedApproxArea(context, mLocation);
			}
			return area;
		}

		@Override
		protected void onCompleted(String area) {
			Context context = getRef();
			if (context != null && area != null && area.length() > 0) {
				((TextView) ((MainActivity) context).findViewById(R.id.textViewLabel)).setText("Tweets around you (" + area + "):");
			}
		}

		@Override
		protected void onFailed(Exception e) {
			// Ignore
		}
		
	}
	
	/********************************
	 * TweetListAdapter 
	 */
	private static class TweetListAdapter extends ArrayAdapter<Tweet> {

		public TweetListAdapter(Context context, List<Tweet> tweets) {
			super(context, 0, 0, tweets);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.tweet_item, null);
			}
			Tweet tweet = getItem(position);
			((TextView) view.findViewById(R.id.textViewTweet)).setText(tweet.getText());
			((RemoteThumbImageView) view.findViewById(R.id.remoteImageViewProfile)).startLoading(tweet);
			return view;
		}
				
	}

}