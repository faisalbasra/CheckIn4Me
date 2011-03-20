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
package com.davidivins.checkin4me.runnables;

import java.util.ArrayList;

import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.listeners.LocationsRetrieverListener;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;

/**
 * LocationsThread
 * 
 * @author david
 */
public class LocationsRetriever implements Runnable
{
	private Activity activity;
	private LocationsRetrieverListener listener;
	private Handler handler;
	private String query;
	private String longitude;
	private String latitude;
	private SharedPreferences persistent_storage;
	private ArrayList<Locale> locations_retrieved;
	
	/**
	 * LocationsThread
	 * 
	 * @param activity
	 * @param query
	 * @param longitude
	 * @param latitude
	 * @param persistent_storage
	 */
	public LocationsRetriever(Activity activity, LocationsRetrieverListener listener, Handler handler, String query, String longitude, String latitude, SharedPreferences persistent_storage)
	{
		this.activity = activity;
		this.listener = listener;
		this.handler = handler;
		this.query = query;
		this.longitude = longitude;
		this.latitude = latitude;
		this.persistent_storage = persistent_storage;
		
		this.locations_retrieved = new ArrayList<Locale>();
	}
	
	/**
	 * run
	 */
	public void run() 
	{
		locations_retrieved = Services.getInstance(activity).getAllLocations(query, longitude, latitude, persistent_storage);
		
		if (null != handler)
			handler.post(listener.getLocationsRetrievedCallback());
	}
	
	/**
	 * destroyHandler
	 */
	public void destroyHandler()
	{
		handler = null;
	}
	
	/**
	 * getThreadName
	 * 
	 * @return String
	 */
	public String getThreadName()
	{
		return "LocationsThread";
	}
	
	/**
	 * getLocations
	 * 
	 * @return ArrayList<Locale>
	 */
	public ArrayList<Locale> getLocationsRetrieved()
	{
		return locations_retrieved;
	}
}