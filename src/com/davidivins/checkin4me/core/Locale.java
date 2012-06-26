//*****************************************************************************
//    This file is part of CheckIn4Me.  Copyright � 2010  David Ivins
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
package com.davidivins.checkin4me.core;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.google.android.maps.GeoPoint;

import java.util.HashMap;
import java.util.Set;

/**
 * Location
 * 
 * @author david ivins
 */
public class Locale 
{
	private static final String TAG = Locale.class.getSimpleName();
	private String name;
	private String description;

	private String longitude;
	private String latitude;
	
	private String address;
	private String city;
	private String state;
	private String zip;
	
	private double distance_from_user;
	
	HashMap<Integer, String> service_location_ids;
	
	/**
	 * Location
	 */
	public Locale()
	{
		name = "";
		description = "";
		
		longitude = "0.0";
		latitude = "0.0";
		
		address = "";
		city = "";
		state = "";
		zip = "";
		
		distance_from_user = 1000; // over 1000 km
		
		service_location_ids = new HashMap<Integer, String>();
	}
	
	/**
	 * Location
	 * 
	 * @param name
	 * @param description
	 * @param longitude
	 * @param latitude
	 */
	public Locale(String name, String description, String longitude, String latitude,
			String address, String city, String state, String zip)
	{
		this.name = name;
		this.description = description;
		this.longitude = longitude;
		this.latitude = latitude;
		
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		
		this.distance_from_user = 1000.0; // over 1000 km
		
		service_location_ids = new HashMap<Integer, String>();
	}
	
	/**
	 * getName 
	 * 
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * getDescription
	 * 
	 * @return String
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * getLongitude
	 * 
	 * @return String
	 */
	public String getLongitude()
	{
		return longitude;
	}
	
	/**
	 * getLatitude
	 * 
	 * @return String
	 */
	public String getLatitude()
	{
		return latitude;
	}
	
	/**
	 * getAddress
	 * 
	 * @return String
	 */
	public String getAddress()
	{
		String out_address = "";
		
		if (!address.equals(""))
			out_address += address;
		
		if (!city.equals(""))
			out_address += ". " + city;
		
		if (!state.equals(""))
			out_address += ", " + state;

		if (!zip.equals(""))
			out_address += " " + zip;

		return out_address;
	}
	
	/**
	 * calculateAndSetDistance
	 * 
	 * @param user_longitude
	 * @param user_latitude
	 */
	public void calculateAndSetDistanceFromUser(double user_longitude, double user_latitude)
	{
		this.distance_from_user = Math.abs(Algorithms.getDistance(
				Double.valueOf(longitude), Double.valueOf(latitude),
				user_longitude, user_latitude));
	}
	
	/**
	 * getDistanceFromUser
	 * 
	 * @return distance
	 */
	public double getDistanceFromUser()
	{		
		return distance_from_user;
	}
	
	/**
	 * getCoordinatesAsGeoPoint
	 * 
	 * @return GeoPoint
	 */
	public GeoPoint getCoordinatesAsGeoPoint()
	{
		Double longitude = new Double(this.longitude);
		Double latitude = new Double(this.latitude);
		return new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
	}
	
	/**
	 * mapServiceIdToLocationId
	 * 
	 * @param service_id
	 * @param location_id
	 */
	public void mapServiceIdToLocationId(int service_id, String location_id)
	{
		service_location_ids.put(service_id, location_id);
	}
	
	/**
	 * getServiceIdToLocationIdMap
	 * 
	 * @return HashMap<Integer, String>
	 */
	public HashMap<Integer, String> getServiceIdToLocationIdMap()
	{
		return service_location_ids;
	}
	
	/**
	 * store
	 * 
	 * @param persistent_storage
	 */
	public void store(SharedPreferences persistent_storage)
	{
		Editor persistent_storage_editor = persistent_storage.edit();
		int last_saved_xref_count = persistent_storage.getInt("last_saved_xref_count", 0);
		
		persistent_storage_editor.putString("current_location_name", name);
		persistent_storage_editor.putString("current_location_description", description);
		persistent_storage_editor.putString("current_location_longitude", longitude);
		persistent_storage_editor.putString("current_location_latitude", latitude);
		
		persistent_storage_editor.putString("current_location_address", address);
		persistent_storage_editor.putString("current_location_city", city);
		persistent_storage_editor.putString("current_location_state", state);
		persistent_storage_editor.putString("current_location_zip", zip);
		
		Set<Integer> keys = service_location_ids.keySet();
		int count = 0;
		
		for (int i = 0; i < last_saved_xref_count; i++)
		{
			persistent_storage_editor.remove("current_location_xref_key_" + i);
			persistent_storage_editor.remove("current_location_xref_value_" + i);
		}
		
		for (Integer key : keys)
		{
			String value = service_location_ids.get(key);
			persistent_storage_editor.putString("current_location_xref_key_" + count, key.toString());
			persistent_storage_editor.putString("current_location_xref_value_" + count, value);
			count++;
		}
		
		Log.i(TAG, "Saved " + count + " mappings");
		persistent_storage_editor.commit();
	}
	
	/**
	 * load
	 * 
	 * @param persistent_storage
	 */
	public void load(SharedPreferences persistent_storage)
	{
		Editor persistent_storage_editor = persistent_storage.edit();
		
		name = persistent_storage.getString("current_location_name", "");
		description = persistent_storage.getString("current_location_description", "");
		longitude = persistent_storage.getString("current_location_longitude", "");
		latitude = persistent_storage.getString("current_location_latitude", "");
		
		address = persistent_storage.getString("current_location_address", "");
		city = persistent_storage.getString("current_location_city", "");
		state = persistent_storage.getString("current_location_state", "");
		zip = persistent_storage.getString("current_location_zip", "");
		
		for (int i = 0; i != -1; i++) // <---  i know... :(
		{
			String key_string = persistent_storage.getString("current_location_xref_key_" + i, "");
			String value = persistent_storage.getString("current_location_xref_value_" + i, "");
			
			if (!key_string.equals("") && !value.equals(""))
			{
				Integer key = new Integer(key_string);
				service_location_ids.put(key, value);
			}
			else
			{
				Log.i(TAG, "Loaded " + i + " mappings");
				persistent_storage_editor.putInt("last_saved_xref_count", i);
				persistent_storage_editor.commit();
				break;
			}
		}
	}
}