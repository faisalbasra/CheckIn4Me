//*****************************************************************************
//    This file is part of CheckIn4Me.  Copyright © 2010  David Ivins
//
//    CheckIn4Me is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    CheckIn4Me is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with CheckIn4Me.  If not, see <http://www.gnu.org/licenses/>.
//*****************************************************************************
package com.davidivins.checkin4me.foursquare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.davidivins.checkin4me.comparators.LocaleDistanceComparator;
import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.interfaces.APIInterface;
import com.davidivins.checkin4me.oauth.OAuth2Request;
import com.davidivins.checkin4me.oauth.OAuthResponse;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * FoursquareAPI
 * 
 * @author david ivins
 */
public class FoursquareAPI implements APIInterface
{
	private static final String TAG = "FoursquareAPI";
	
	private int service_id;
	private Properties config;
	
	private ArrayList<Locale> latest_locations;
	private boolean latest_checkin_status;
	
	/**
	 * FoursquareAPI
	 * 
	 * @param Properties config
	 * @param int service_id
	 */
	public FoursquareAPI(Properties config, int service_id)
	{
		this.config = config;
		this.service_id = service_id;
		latest_locations = new ArrayList<Locale>();
		latest_checkin_status = false;
	}
	
	/**
	 * getLocationsThread
	 * 
	 * @param query
	 * @param longitude
	 * @param latitude
	 * @param persistent_storage
	 * @return LocationThread
	 */
	public Runnable getLocationsThread(String query, String longitude, String latitude, SharedPreferences persistent_storage)
	{
		return new LocationsThread(query, longitude, latitude, persistent_storage);
	}
	
	/**
	 * getLatestLocations
	 * 
	 * @return ArrayList<Locale>
	 */
	public ArrayList<Locale> getLatestLocations()
	{
		Collections.sort(latest_locations, new LocaleDistanceComparator());
		return latest_locations;
	}
	
	/**
	 * getCheckInThread
	 * 
	 * @param location
	 * @param persistent_storage
	 * @return CheckInThread
	 */
	public Runnable getCheckInThread(Locale location, String message, SharedPreferences persistent_storage)
	{
		latest_checkin_status = false;
		return new CheckInThread(location, message, persistent_storage);
	}
	
	/**
	 * getLatestCheckInStatus
	 * 
	 * @return boolean
	 */
	public boolean getLatestCheckInStatus()
	{
		return latest_checkin_status;
	}
	
	/**
	 * LocationsThread
	 * 
	 * @author david
	 */
	class LocationsThread implements Runnable
	{
		private String query;
		private String longitude;
		private String latitude;
		private SharedPreferences persistent_storage;
		
		/**
		 * LocationsThread
		 * 
		 * @param query
		 * @param longitude
		 * @param latitude
		 */
		LocationsThread(String query, String longitude, String latitude, SharedPreferences persistent_storage)
		{
			this.query     = query;
			this.longitude = longitude;
			this.latitude  = latitude;
			this.persistent_storage  = persistent_storage;
		}

		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Retrieving Foursquare Locations");

			// build new oauth request
			OAuth2Request request = new OAuth2Request(
					config.getProperty("api_http_method"), config.getProperty("api_host"), 
					config.getProperty("api_version") + config.getProperty("api_locations_endpoint"));
			
			// set request headers
			request.addHeader("User-Agent", "CheckIn4Me:2.0");  // TODO: set this from meta-data
			
			// set query parameters
			if (query != null)
			{
				request.addQueryParameterAndEncode("query", query);
				request.addQueryParameter("limit", "10");
			}
			else
				request.addQueryParameter("limit", "50");

			request.addQueryParameter("ll", latitude + "," + longitude);
			request.addQueryParameter("intent", "checkin");
			request.addQueryParameter("oauth_token", 
					persistent_storage.getString("foursquare_oauth_token_secret", "FOURSQUARE_ACCESS_TOKEN_HERE"));
			
			// execute http request
			OAuthResponse response = (OAuthResponse)request.execute();
			
			// save locations
			if (response.getSuccessStatus())
				setLocationsFromJson(response.getResponseString(), query);	
		}
		
		/**
		 * setLocationsFromJson
		 * 
		 * @param json
		 */
		synchronized private void setLocationsFromJson(String json_string, String query)
		{
			Log.i(TAG, "json_string = " + json_string);
			if (null != query) Log.i(TAG, "query = " + query);
			
			// clear locations list
			latest_locations.clear();
			
			// get user's current location as doubles
			double user_longitude = Double.valueOf(persistent_storage.getString("current_longitude", "0.0"));
			double user_latitude  = Double.valueOf(persistent_storage.getString("current_latitude", "0.0"));
			
			// default to nearby places
			String type = "nearby";
			
			// if a query exists, look for the "places" group instead of "nearby"
			if (query != null)
				type = "places";
				
			try 
			{
				// get the json response string as a json object
				JSONObject full_response = new JSONObject(json_string);
				JSONObject response = full_response.getJSONObject("response");
				JSONArray groups = response.getJSONArray("groups");
				
				// loop through groups and find the group that is either the query results or the nearby places
				for (int i = 0; i < groups.length(); i++)
				{
					JSONObject current_object = groups.getJSONObject(i);
					
					// check the type of the current group
					if (current_object.getString("type").equals(type))
					{
						// get this group's venues
						JSONArray venues = current_object.getJSONArray("items");
						
						// store each venue as a new locale
						for (int j = 0; j < venues.length(); j++)
						{
							// get venue information
							JSONObject venue = venues.getJSONObject(j);
							
							String venue_id    = venue.getString("id");
							String name        = venue.getString("name");
							String description = "";
							
							// get venue location information
							JSONObject venue_location = venue.getJSONObject("location");
							
							String latitude  = venue_location.getString("lat");
							String longitude = venue_location.getString("lng");
							
							String address   = (venue_location.has("address")) ? venue_location.getString("address") : "";
							//String cross_street   = (venue_location.has("crossStreet")) ? venue_location.getString("crossStreet") : "";
							String city      = (venue_location.has("city")) ? venue_location.getString("city") : "";
							String state     = (venue_location.has("state")) ? venue_location.getString("state") : "";
							String zip       = (venue_location.has("postalCode")) ? venue_location.getString("postalCode") : "";
							//String distance   = (venue_location.has("distance")) ? venue_location.getString("distance") : "";
							//String country   = (venue_location.has("country")) ? venue_location.getString("country") : "";

							// create a new locale object with the venue's data
							Locale location = new Locale(name, description, longitude, latitude,
									address, city, state, zip);
							location.calculateAndSetDistanceFromUser(user_longitude, user_latitude);
							location.mapServiceIdToLocationId(service_id, venue_id);
							
							// add the new locale to the latest locations list
							latest_locations.add(location);	
						}

						break;
					}
				}
			} 
			catch (JSONException e) 
			{
				Log.e(TAG, "JSON Exception: " + e.getMessage());
				Log.e(TAG, "Could not parse json response: " + json_string);
			}
		}
	}
	
	/**
	 * CheckInThread
	 * 
	 * @author david
	 */
	class CheckInThread implements Runnable
	{
		private Locale location;
		private String message;
		private SharedPreferences persistent_storage;
		
		/**
		 * CheckInThread
		 * 
		 * @param location
		 * @param persistent_storage
		 */
		CheckInThread(Locale location, String message, SharedPreferences persistent_storage)
		{
			this.location = location;
			this.message = message;
			this.persistent_storage = persistent_storage;
		}

		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Checking in on Foursquare");

			// build new oauth request
			OAuth2Request request = new OAuth2Request(
					config.getProperty("api_checkin_http_method"), config.getProperty("api_host"), 
					config.getProperty("api_version") + config.getProperty("api_checkin_endpoint"));
			
			// set request headers
			request.addHeader("User-Agent", "CheckIn4Me:1.0"); // TODO: get this from meta-data 
			
			// set query parameters
			request.addQueryParameter("oauth_token", 
					persistent_storage.getString("foursquare_oauth_token_secret", "FOURSQUARE_ACCESS_TOKEN_HERE"));
			
			HashMap<Integer, String> service_id_location_id_xref = location.getServiceIdToLocationIdMap();
			String vid = service_id_location_id_xref.get(service_id);
			
			request.addQueryParameter("venueId", vid);
			request.addQueryParameter("ll", persistent_storage.getString("current_latitude", "CURRENT_LATITUDE_HERE") + "," +
					 persistent_storage.getString("current_longitude", "CURRENT_LONGITUDE_HERE"));
			
			// initialize broadcast to public
			String broadcast = "public";
			
			// if post to facebook is enabled, post it
			if (persistent_storage.getBoolean("foursquare_post_to_facebook_default", false))
				broadcast += ",facebook";
			
			// if post to twitter is enabled, add it to broadcast
			if (persistent_storage.getBoolean("foursquare_post_to_twitter_default", false))
				broadcast += ",twitter";

			// add broadcast preferences to request
			request.addQueryParameterAndEncode("broadcast", broadcast);
			
			if (!message.equals(""))
				request.addQueryParameterAndEncode("shout", message);
			
			// execute http request
			OAuthResponse response = (OAuthResponse)request.execute();
			
			// save locations
			if (response.getSuccessStatus())
				setLocationsFromJson(response.getResponseString());	
		}
		
		/**
		 * setLocationsFromJson
		 * 
		 * @param json
		 */
		synchronized private void setLocationsFromJson(String json_string)
		{
			Log.i(TAG, "json_string = " + json_string);
			
			latest_checkin_status = false;
			
			try 
			{
				// get the json response string as a json object
				JSONObject full_response = new JSONObject(json_string);
				JSONObject response = full_response.getJSONObject("response");
				JSONObject checkin_info = response.getJSONObject("checkin");
				
				// get checkin status from fields returned in json response
				latest_checkin_status = checkin_info.has("id") && checkin_info.has("createdAt");
			} 
			catch (JSONException e) 
			{
				Log.i(TAG, "JSON Exception: " + e.getMessage());
				Log.i(TAG, "Could not parse json response: " + json_string);
			}
		}
	}
}
