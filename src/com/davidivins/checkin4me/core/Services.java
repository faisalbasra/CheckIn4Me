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

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import com.davidivins.checkin4me.facebook.FacebookService;
import com.davidivins.checkin4me.foursquare.FoursquareService;
import com.davidivins.checkin4me.interfaces.ServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Services
 * 
 * @author david ivins
 */
public class Services
{
	private static final String TAG = Services.class.getSimpleName();
	private static Services instance;	
	private List<ServiceInterface> services;
	
	/**
	 * Services
	 * 
	 * @param resources
	 */
	private Services(SharedPreferences persistent_storage, Resources resources)
	{
		int service_count = 0;
		
		services = new ArrayList<ServiceInterface>();
		services.add(new FacebookService(service_count++, persistent_storage, resources));
		services.add(new FoursquareService(service_count++, persistent_storage, resources));
		//services.add(new GooglePlacesService(service_count++, persistent_storage, resources));
		//services.add(new GowallaService(service_count++, persistent_storage, resources));
	}
	
	/**
	 * getInstance
	 * 
	 * @param activity
	 * @return Services
	 */
	static public Services getInstance(Activity activity)
	{
		if (null == instance)
			instance = new Services(
					PreferenceManager.getDefaultSharedPreferences(activity), activity.getResources());
		
		return instance;
	}
	
	/**
	 * getServiceById
	 * 
	 * @param id
	 * @return Service
	 */
	public ServiceInterface getServiceById(int id)
	{
		return services.get(id);
	}
	
	/**
	 * getServicesAsList
	 * 
	 * @return List<Service>
	 */
	public List<ServiceInterface> getServicesAsList()
	{
		return services;
	}
	
	/**
	 * getServicesWithSettingsAsList
	 */
	public List<ServiceInterface> getConnectedServicesWithSettingsAsList()
	{
		List<ServiceInterface> services_with_settings = new ArrayList<ServiceInterface>();
		
		for (ServiceInterface service : services)
		{
			if (service.hasSettings() && service.connected())
				services_with_settings.add(service);
		}
		
		return services_with_settings;
	}
	
	/**
	 * getConnectedServicesAsList
	 * 
	 * @return List<Service>
	 */
	public List<ServiceInterface> getConnectedServicesAsList()
	{
		List<ServiceInterface> connected_services = new ArrayList<ServiceInterface>();
		
		for (ServiceInterface service : services)
		{
			if (service.connected())
				connected_services.add(service);
		}
		return connected_services;
	}
	
	/**
	 * getLogoDrawables
	 * 
	 * @return List<Integer>
	 */
	public List<Integer> getLogoDrawables()
	{
		List<Integer> drawables = new ArrayList<Integer>();
		
		for (ServiceInterface service : services)
		{
			drawables.add(service.getLogoDrawable());
		}
		
		return drawables;
	}
	
	/**
	 * atLeastOneConnected
	 * 
	 * @return boolean
	 */
	public boolean atLeastOneConnected()
	{
		boolean result = false;
		
		for (ServiceInterface service : services)
		{
			if (service.connected())
			{
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * getAllLocations
	 * 
	 * @param query
	 * @param longitude
	 * @param latitude
	 * @param persistent_storage
	 * @return List<Locale>
	 */
	public List<Locale> getAllLocations(String query, String longitude, String latitude, SharedPreferences persistent_storage)
	{
		List<Thread> threads = new ArrayList<Thread>();
		List<List<Locale>> location_lists = new ArrayList<List<Locale>>();
		
		// get location request threads
		for (ServiceInterface service : services)
		{
			if (service.connected())
			{
				Log.i(TAG, "Creating thread for service " + service.getName());
				threads.add(new Thread(service.getAPIInterface().getLocationsThread(query, longitude, latitude, persistent_storage), service.getName()));
			}
		}
		
		// start threads
		for (Thread thread : threads)
		{
			thread.start();
		}
		
		// join threads
		for (Thread thread : threads)
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				Log.i(TAG, thread.getName() + " thread is interrupted already");
			}
		}
		
		// get latest locations
		for (ServiceInterface service : services)
		{
			if (service.connected())
				location_lists.add(service.getAPIInterface().getLatestLocations());
		}
		
		// merge locations
		return Algorithms.mergeLocations(location_lists);
	}
	
	/**
	 * checkIn
	 * 
	 * @param service_ids
	 * @param location
	 * @param persistent_storage
	 */
	public Map<Integer, Boolean> checkIn(List<Integer> service_ids, Locale location, String message, SharedPreferences persistent_storage)
	{
		List<Thread> threads = new ArrayList<Thread>();
		Map<Integer, Boolean> checkin_statuses = new HashMap<Integer, Boolean>();
		
		// get location request threads
		for (int service_id : service_ids)
		{
			ServiceInterface service = getServiceById(service_id);
			if (service.connected())
				threads.add(new Thread(service.getAPIInterface().getCheckInThread(location, message, persistent_storage), service.getName()));			
		}
		
		// start threads
		for (Thread thread : threads)
		{
			thread.start();
		}
		
		// join threads
		for (Thread thread : threads)
		{
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{
				Log.i(TAG, thread.getName() + " thread is interrupted already");
			}
		}
		
		// get latest locations
		for (int service_id : service_ids)
		{
			ServiceInterface service = getServiceById(service_id);
			
			if (service.connected())
				checkin_statuses.put(service.getId(), service.getAPIInterface().getLatestCheckInStatus());
		}
		
		return checkin_statuses;
	}
}