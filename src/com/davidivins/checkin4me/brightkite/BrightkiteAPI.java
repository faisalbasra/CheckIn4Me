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
package com.davidivins.checkin4me.brightkite;

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
import com.davidivins.checkin4me.oauth.OAuth1Request;
import com.davidivins.checkin4me.oauth.OAuthResponse;
import com.davidivins.checkin4me.util.HTTPRequest;

import android.content.SharedPreferences;
import android.util.Log;

/**
* BrightkiteAPI
* 
* @author david ivins
*/
public class BrightkiteAPI implements APIInterface
{
	private static final String TAG = "BrightkiteAPI";
	
	private int service_id;
	private Properties config;
	
	private ArrayList<Locale> latest_locations;
	private boolean latest_checkin_status;
	
	/**
	* BrightkiteAPI
	* 
	* @param Properties config
	* @param int service_id
	*/
	public BrightkiteAPI(Properties config, int service_id)
	{
		this.config = config;
		this.service_id = service_id;
		latest_locations = new ArrayList<Locale>();
		latest_checkin_status = false;
	}
	
	/**
	* getLocationThread
	* 
	* @param query
	* @param longitude
	* @param latitude
	* @param settings
	* @return LocationThread
	*/
	public Runnable getLocationThread(String query, String longitude, String latitude, SharedPreferences settings)
	{
		return new LocationThread(query, longitude, latitude);
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
	* @param settings
	* @return CheckInThread
	*/
	public Runnable getCheckInThread(Locale location, SharedPreferences settings)
	{
		latest_checkin_status = false;
		return new CheckInThread(location, settings);
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
	* LocationThread
	* 
	* @author david
	*/
	class LocationThread implements Runnable
	{
		private String query;
		private String longitude;
		private String latitude;
		
		/**
		 * LocationThread
		 * 
		 * @param query
		 * @param longitude
		 * @param latitude
		 */
		LocationThread(String query, String longitude, String latitude)
		{
			this.query     = query;
			this.longitude = longitude;
			this.latitude  = latitude;
		}
	
		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Retrieving Brightkite Locations");
	
			// build new oauth request
			HTTPRequest request = new HTTPRequest(
					config.getProperty("api_http_method"), config.getProperty("api_host"), 
					config.getProperty("api_locations_endpoint") + "." + 
					config.getProperty("api_data_format"));
			
			// set request headers
			request.addHeader("User-Agent", "CheckIn4Me:1.0");  // TODO: set this from meta-data
			
			// set query parameters
			if (query != null)
				request.addQueryParameter("q", query);

			request.addQueryParameter("limit", "50");
			request.addQueryParameter("clat", latitude);
			request.addQueryParameter("clng", longitude);
			request.addQueryParameter("cacc", "100");
			
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
		private void setLocationsFromJson(String json_string, String query)
		{
			Log.i(TAG, "json_string = " + json_string);
			latest_locations.clear();
			
			try 
			{
				// get the json response string as a json object
				JSONArray spots = new JSONArray(json_string);
				
				// loop through groups and find the group that is either the query results or the nearby places
				for (int i = 0; i < spots.length(); i++)
				{
					JSONObject current_spot = spots.getJSONObject(i);
					
					// get id from spot url
					if (current_spot.has("id") && current_spot.has("name"))
					{
						String id = current_spot.getString("id");
						String name = current_spot.getString("name");
						String description = "";
						
						String latitude  = current_spot.has("latitude") ? current_spot.getString("latitude") : "";
						String longitude = current_spot.has("longitude") ? current_spot.getString("longitude") : "";
						
						String address   = (current_spot.has("street")) ? current_spot.getString("street") : "";
						String city      = (current_spot.has("city")) ? current_spot.getString("city") : "";
						String state     = (current_spot.has("state")) ? current_spot.getString("state") : "";
						String zip       = (current_spot.has("postalCode")) ? current_spot.getString("postalCode") : "";
						//String distance   = (venue_location.has("distance")) ? venue_location.getString("distance") : "";
						//String country   = (current_spot.has("country")) ? current_spot.getString("country") : "";
		
						// create a new locale object with the venue's data
						Locale location = new Locale(name, description, longitude, latitude,
								address, city, state, zip);
						location.mapServiceIdToLocationId(service_id, id);
						
						// add the new locale to the latest locations list
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
	* CheckInThread
	* 
	* @author david
	*/
	class CheckInThread implements Runnable
	{
		private Locale location;
		private SharedPreferences settings;
		
		/**
		 * CheckInThread
		 * 
		 * @param location
		 * @param settings
		 */
		CheckInThread(Locale location, SharedPreferences settings)
		{
			this.location = location;
			this.settings = settings;
		}
	
		/**
		 * run
		 */
		public void run() 
		{
			Log.i(TAG, "Checking in on Brightkite");
	
			// build new oauth request
			OAuth1Request request = new OAuth1Request(
					config.getProperty("oauth_client_secret", "OAUTH_CLIENT_SECRET") + "&" + 
					settings.getString("brightkite_oauth_token_secret", "BRIGHTKITE_OAUTH_TOKEN_SECRET"),
					config.getProperty("api_checkin_http_method"), config.getProperty("api_host"), 
					config.getProperty("api_checkin_endpoint") + "." + config.getProperty("api_data_format"));
			
			// set request headers
			request.addHeader("User-Agent", "CheckIn4Me:1.0"); // TODO: get this from meta-data 
			
			// set query parameters
			request.addQueryParameter("oauth_consumer_key", config.getProperty("oauth_client_id"));
			request.addQueryParameter("oauth_nonce", request.generateNonce());
			request.addQueryParameter("oauth_signature_method", config.getProperty("oauth_signature_method"));
			request.addQueryParameter("oauth_token", settings.getString("brightkite_oauth_token", "BRIGHTKITE_OAUTH_TOKEN"));
			request.addQueryParameter("oauth_timestamp", request.generateTimestamp());
			request.addQueryParameter("oauth_version", config.getProperty("oauth_version"));
			
			HashMap<Integer, String> service_id_location_id_xref = location.getServiceIdToLocationIdMap();
			String place_id = service_id_location_id_xref.get(service_id);
			
			//request.addQueryParameter("object[place_id]", place_id);
			request.addQueryParameterAndEncode("object[place_id]", place_id);
			request.addQueryParameterAndEncode("object[share_with]", "everybody");
			request.addQueryParameterAndEncode("object[body]", "");

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
		private void setLocationsFromJson(String json_string)
		{
			Log.i(TAG, "json_string = " + json_string);
			
			latest_checkin_status = false;
			
			try 
			{
				// get the json response string as a json object
				JSONObject response = new JSONObject(json_string);
				
				// get checkin status from fields returned in json response
				latest_checkin_status = response.has("id") && response.has("created_at");
			} 
			catch (JSONException e) 
			{
				Log.i(TAG, "JSON Exception: " + e.getMessage());
				Log.i(TAG, "Could not parse json response: " + json_string);
			}
		}
	}
}
