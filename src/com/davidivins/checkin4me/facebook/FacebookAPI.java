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
package com.davidivins.checkin4me.facebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.interfaces.APIInterface;
import com.davidivins.checkin4me.oauth.OAuthResponse;
import com.davidivins.checkin4me.util.HTTPRequest;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * GowallaAPI
 * 
 * @author david ivins
 */
public class FacebookAPI implements APIInterface
{
	private static final String TAG = "FacebookAPI";
	
	private int service_id;
	private Properties config;
	
	private ArrayList<Locale> latest_locations;
	private boolean latest_checkin_status;
	
	/**
	 * GowallaAPI
	 * 
	 * @param Properties config
	 * @param int service_id
	 */
	public FacebookAPI(Properties config, int service_id)
	{
		Log.i(TAG, "service_id = " + service_id);
		this.config = config;
		this.service_id = service_id;
		latest_locations = new ArrayList<Locale>();
		latest_checkin_status = false;
	}
	
	/**
	 * getLocationThread
	 * 
	 * @param longitude
	 * @param latitude
	 * @param persistent_storage
	 * @return LocationThread
	 */
	public Runnable getLocationThread(String query,String longitude, String latitude, SharedPreferences persistent_storage)
	{
		return new LocationThread(query, longitude, latitude, persistent_storage);
	}
	
	/**
	 * getLatestLocations
	 * 
	 * @return ArrayList<Locale>
	 */
	public ArrayList<Locale> getLatestLocations()
	{
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
	 * getLatestCheckInStatuse
	 * 
	 * @return boolean
	 */
	public boolean getLatestCheckInStatus()
	{
		return latest_checkin_status;
	}
	
	/**
	 * LocationThread
	 * 
	 * @author david
	 */
	class LocationThread implements Runnable
	{
		private String query;
		private String longitude;
		private String latitude;
		private SharedPreferences persistent_storage;
		
		/**
		 * LocationThread
		 * 
		 * @param query
		 * @param longitude
		 * @param latitude
		 */
		LocationThread(String query, String longitude, String latitude, SharedPreferences persistent_storage)
		{
			this.query = query;
			this.longitude = longitude;
			this.latitude = latitude;
			this.persistent_storage = persistent_storage;
		}

		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Retrieving Facebook Locations");

			// build new http request
			HTTPRequest request = new HTTPRequest(
				config.getProperty("api_http_method"), config.getProperty("api_host"), 
				config.getProperty("api_locations_endpoint"));
			
			// set query parameters
			request.addQueryParameter("access_token", persistent_storage.getString("facebook_access_token", "FACEBOOK_ACCESS_TOKEN_HERE"));
			if (query != null)
				request.addQueryParameter("q", query);
			request.addQueryParameter("type", "place");
			request.addQueryParameter("center", latitude + "," + longitude);
			request.addQueryParameter("distance", "1000");
			
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
		 * @throws JSONException 
		 */
		private void setLocationsFromJson(String json_string)
		{
			// clear locations
			latest_locations.clear();
			
			try 
			{
				JSONObject json = new JSONObject(json_string);
				JSONArray spots = json.getJSONArray("data");
				
				for (int i = 0; i < spots.length(); i++)
				{
					JSONObject spot    = spots.getJSONObject(i);
					
					if (spot.has("id") && spot.has("name"))
					{
						JSONObject details = spot.getJSONObject("location");
						
						String location_id = spot.getString("id");
						String name        = spot.getString("name");
						String description = "";
						
			            String street      = details.has("street")    ? details.getString("street")    : "";
			            String city        = details.has("city")      ? details.getString("city")      : "";
			            String state       = details.has("state")     ? details.getString("state")     : "";
			            //String country     = details.has("country")   ? details.getString("country")   : "";
			            String zip         = details.has("zip")       ? details.getString("zip")       : "";
			            String latitude    = details.has("latitude")  ? details.getString("latitude")  : "";
			            String longitude   = details.has("longitude") ? details.getString("longitude") : "";
						
						Locale location = new Locale(name, description, longitude, latitude, 
								street, city, state, zip);
						location.mapServiceIdToLocationId(service_id, location_id);
						latest_locations.add(location);
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
	 * LocationThread
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
			this.persistent_storage = persistent_storage;
			this.message = message;
		}

		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Checking in with Facebook");

			String coordinates = "{\"latitude\":\"" 
				+ persistent_storage.getString("current_latitude", "CURRENT_LATITUDE_HERE")
				+ "\",\"longitude\":\"" 
				+ persistent_storage.getString("current_longitude", "CURRENT_LONGITUDE_HERE") 
				+ "\"}";
			
			// build new http request
			HTTPRequest request = new HTTPRequest(
				config.getProperty("api_checkin_http_method"), config.getProperty("api_host"), 
				config.getProperty("api_checkin_endpoint"));
			
			HashMap<Integer, String> service_id_location_id_xref = location.getServiceIdToLocationIdMap();
			String place_id = service_id_location_id_xref.get(service_id);
			
			// set query parameters
			request.addQueryParameter("access_token", 
					persistent_storage.getString("facebook_access_token", "FACEBOOK_ACCESS_TOKEN_HERE"));
			
			if (!message.equals(""))
				request.addQueryParameter("message", message);

			request.addQueryParameter("place", place_id);
			request.addQueryParameterAndEncode("coordinates", coordinates);
			
			// execute http request
			OAuthResponse response = (OAuthResponse)request.execute();
			
			// save locations
			if (response.getSuccessStatus())
				setStatusFromJson(response.getResponseString());
		}
		
		/**
		 * setStatusFromJson
		 * 
		 * @param json
		 */
		private void setStatusFromJson(String json_string)
		{
			// clear latest status
			latest_checkin_status = false;
			
			try 
			{
				JSONObject json = new JSONObject(json_string);
				
				if (json.has("id"))
					latest_checkin_status = true;
			} 
			catch (JSONException e) 
			{
				Log.i(TAG, "JSON Exception: " + e.getMessage());
				Log.i(TAG, "Could not parse json response: " + json_string);
			}	
		}
	}
}