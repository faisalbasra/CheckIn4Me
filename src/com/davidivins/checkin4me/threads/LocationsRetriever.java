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
package com.davidivins.checkin4me.threads;

import android.os.AsyncTask;
import android.app.Activity;
import android.content.SharedPreferences;

import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.listeners.interfaces.LocationsRetrieverListener;
import com.davidivins.checkin4me.core.Services;

import java.util.ArrayList;

/**
 * LocationsRetriever
 * 
 * @author david
 */
public class LocationsRetriever extends AsyncTask<Void, Void,  ArrayList<Locale>> 
{
	private Activity activity;
	private LocationsRetrieverListener listener;
	private String query;
	private String longitude;
	private String latitude;
	private SharedPreferences persistent_storage;
	
	/**
	 * LocationsRetriever
	 * 
	 * @param activity
	 * @param query
	 * @param longitude
	 * @param latitude
	 * @param persistent_storage
	 */
	public LocationsRetriever(Activity activity, LocationsRetrieverListener listener, String query, String longitude, String latitude, SharedPreferences persistent_storage)
	{
		this.activity           = activity;
		this.listener           = listener;
		this.query              = query;
		this.longitude          = longitude;
		this.latitude           = latitude;
		this.persistent_storage = persistent_storage;		
	}
	
	/**
	 * doInBackground
	 *
	 * attempts to retrieve locations from all services.
	 * 
	 * @return an array list of locales
	 */
	@Override
	protected ArrayList<Locale> doInBackground(Void ... params)
	{
		return Services.getInstance(activity).getAllLocations(query, longitude, latitude, persistent_storage);
    }

	/**
	 * onPostExecute
	 * 
	 * updates the listener's locations.
	 * 
	 * @param locations
	 */
	@Override
	protected void onPostExecute(ArrayList<Locale> locations)
	{
		listener.updateLocations(locations);
	}
}