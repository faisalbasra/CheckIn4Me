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
package com.davidivins.checkin4me.gowalla;

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
import com.davidivins.checkin4me.util.HTTPRequest;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * GowallaAPI
 * 
 * @author david ivins
 */
public class GowallaAPI implements APIInterface
{
	private static final String TAG = "GowallaAPI";
	
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
	public GowallaAPI(Properties config, int service_id)
	{
		Log.i(TAG, "service_id = " + service_id);
		this.config = config;
		this.service_id = service_id;
		latest_locations = new ArrayList<Locale>();
		latest_checkin_status = false;
	}
	
	/**
	 * getLocationsThread
	 * 
	 * @param longitude
	 * @param latitude
	 * @param persistent_storage
	 * @return LocationThread
	 */
	public Runnable getLocationsThread(String query,String longitude, String latitude, SharedPreferences persistent_storage)
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
	 * getLatestCheckInStatuse
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
		 * @param persistent_storage
		 */
		LocationsThread(String query, String longitude, String latitude, SharedPreferences persistent_storage)
		{
			this.query               = query;
			this.longitude           = longitude;
			this.latitude            = latitude;
			this.persistent_storage  = persistent_storage;
		}

		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Retrieving Gowalla Locations");

			// build new http request
			HTTPRequest request = new HTTPRequest(
				config.getProperty("api_http_method"), config.getProperty("api_host"), 
				config.getProperty("api_locations_endpoint"));
			
			// set request headers
			request.addHeader("X-Gowalla-API-Key", config.getProperty("oauth_client_id"));
			request.addHeader("Accept", "application/" + config.getProperty("api_format"));
			
			// set query parameters
			if (query != null)
				request.addQueryParameterAndEncode("q", query);
			request.addQueryParameter("lat", latitude);
			request.addQueryParameter("lng", longitude);
			request.addQueryParameter("radius", "50");

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
		synchronized private void setLocationsFromJson(String json_string)
		{
			// clear locations
			latest_locations.clear();
			
			// get user's current location as doubles
			double user_longitude = Double.valueOf(persistent_storage.getString("current_longitude", "0.0"));
			double user_latitude  = Double.valueOf(persistent_storage.getString("current_latitude", "0.0"));
			
			try 
			{
				JSONObject json = new JSONObject(json_string);
				JSONArray spots = json.getJSONArray("spots");
				
				for (int i = 0; i < spots.length(); i++)
				{
					JSONObject spot = spots.getJSONObject(i);
					
					String name = spot.getString("name");
					String description = spot.getString("description");
					String checkins_url = spot.getString("checkins_url");
					String longitude = spot.getString("lng");
					String latitude = spot.getString("lat");
					
					JSONObject addr = spot.getJSONObject("address");
					String city = addr.getString("locality");
					String state = addr.getString("region");
					
					String address = "";
					String zip = "";
					
					String[] temp = checkins_url.split("\\?");
					String[] spot_id_key_value = temp[1].split("\\=");
					String spot_id = spot_id_key_value[1];
					
					Locale location = new Locale(name, description, longitude, latitude, 
							address, city, state, zip);
					location.calculateAndSetDistanceFromUser(user_longitude, user_latitude);
					location.mapServiceIdToLocationId(service_id, spot_id);
					latest_locations.add(location);
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
		private boolean token_refresh_attempted;
		
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
			this.token_refresh_attempted = false;
		}

		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Checking in with Gowalla");
			boolean checkin_status = false;

			// build new http request
			HTTPRequest request = new HTTPRequest(
				config.getProperty("oauth_http_method"), config.getProperty("api_secure_host"), 
				config.getProperty("api_checkin_endpoint"));
			
			// set request headers
			request.addHeader("X-Gowalla-API-Key", config.getProperty("oauth_client_id"));
			request.addHeader("Accept", "application/" + config.getProperty("api_format"));
			
			HashMap<Integer, String> service_id_location_id_xref = location.getServiceIdToLocationIdMap();
			String spot_id = service_id_location_id_xref.get(service_id);
			
			// set query parameters
			request.addQueryParameter("oauth_token", persistent_storage.getString("gowalla_access_token", "-1"));
			request.addQueryParameter("spot_id", spot_id);

			request.addQueryParameter("comment", "");
			request.addQueryParameter("lat", persistent_storage.getString("current_latitude", "-1"));
			request.addQueryParameter("lng", persistent_storage.getString("current_longitude", "-1"));

			// if post to twitter is enabled, post it
			if (persistent_storage.getBoolean("gowalla_post_to_twitter_default", false))
				request.addQueryParameter("post_to_twitter", "1");
			else
				request.addQueryParameter("post_to_twitter", "0");
			
			// if post to facebook is enabled, post it
			if (persistent_storage.getBoolean("gowalla_post_to_facebook_default", false))
				request.addQueryParameter("post_to_facebook", "1");
			else
				request.addQueryParameter("post_to_facebook", "0");
					
			if (!message.equals(""))
				request.addQueryParameterAndEncode("comment", message);
				
			// execute http request
			OAuthResponse response = (OAuthResponse)request.execute();
			
			// save locations
			if (response.getSuccessStatus())
				checkin_status = setStatusFromJson(response.getResponseString());
			
			if (token_refresh_attempted == false && checkin_status == false)
			{
				token_refresh_attempted = true;
				
				if (attemptToRefreshToken())
					run();
				else
					Log.i(TAG, "Token refresh failed.");
			}
		}
		
		/**
		 * setStatusFromJson
		 * 
		 * @param json
		 * @throws JSONException 
		 */
		synchronized private boolean setStatusFromJson(String json_string)
		{
			Log.i(TAG, "json_string = " + json_string);

			// clear latest status
			latest_checkin_status = false;
			
			try 
			{
				JSONObject json = new JSONObject(json_string);
				
				@SuppressWarnings("unused")
				String created_at = json.getString("created_at");
				
				// if we found a created_at time/date and didn't throw an exception, check-in succeeded
				latest_checkin_status = true;
			} 
			catch (JSONException e) 
			{
				Log.i(TAG, "JSON Exception: " + e.getMessage());
				Log.i(TAG, "Could not parse json response: " + json_string);
			}
			
			return latest_checkin_status;
		}
		
		/**
		 * attemptToRefreshToken
		 */
		private boolean attemptToRefreshToken()
		{
			Log.i(TAG, "Attempting to refresh Gowalla OAuth token");
			 
			OAuthResponse response = new OAuthResponse();
			Log.i(TAG, "refresh_token in persistent_storage = " + persistent_storage.getString("gowalla_refresh_token", "-1"));
			
			if (persistent_storage.getString("gowalla_refresh_token", "-1") != "-1")
			{
				OAuth2Request request = new OAuth2Request(
						config.getProperty("oauth_http_method"), config.getProperty("oauth_host"), 
						config.getProperty("oauth_access_token_endpoint"));
				
				request.addQueryParameter("grant_type", "refresh_token");
				request.addQueryParameter("client_id", config.getProperty("oauth_client_id"));
				request.addQueryParameter("client_secret", config.getProperty("oauth_client_secret"));
				request.addQueryParameter("refresh_token", persistent_storage.getString("gowalla_refresh_token", "-1"));

				response = (OAuthResponse)request.execute();
			}
			else
			{
				Log.e(TAG, "Attempting to complete handshake without a code");
			}
			
			return setTokenFromJson(response.getResponseString());
		 }
		
		/**
		 * setTokenFromJson
		 */
		private boolean setTokenFromJson(String json_string)
		{
			Log.i(TAG, "json_string = " + json_string);
			Editor persistent_storage_editor = persistent_storage.edit();
			boolean status = false;
			
			try
			{
				JSONObject json = new JSONObject(json_string);
				
				String access_token = json.getString("access_token");
				String refresh_token = json.getString("refresh_token");
				
				Log.i(TAG, "New Access Token = " + access_token);
				Log.i(TAG, "New Refresh Token = " + refresh_token);
				
				persistent_storage_editor.putString("gowalla_access_token", access_token);
				persistent_storage_editor.putString("gowalla_refresh_token", refresh_token);
				persistent_storage_editor.commit();

				status = true;
			}
			catch (JSONException e)
			{
				Log.i(TAG, "JSON Exception: " + e.getMessage());
				Log.i(TAG, "Could not parse json response: " + json_string);
			}
			
			return status;
		}
	}
}