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
package com.davidivins.checkin4me.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.davidivins.checkin4me.facebook.FacebookService;
import com.davidivins.checkin4me.foursquare.FoursquareService;
import com.davidivins.checkin4me.gowalla.GowallaService;
import com.davidivins.checkin4me.interfaces.ServiceInterface;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

/**
 * Services
 * 
 * @author david ivins
 */
public class Services
{
	private static final String TAG = "Services";
	private static Services instance;	
	private ArrayList<ServiceInterface> services;
	
	/**
	 * Services
	 * 
	 * @param resources
	 */
	private Services(Resources resources)
	{
		int service_count = 0;
		
		services = new ArrayList<ServiceInterface>();
		services.add(new FacebookService(resources, service_count++));
		services.add(new FoursquareService(resources, service_count++));
		services.add(new GowallaService(resources, service_count++));
		
		// add debug conditional here.
		//services.add(new BrightkiteService(resources, service_count++));	
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
			instance = new Services(activity.getResources());
		
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
	 * getServicesAsArrayList
	 * 
	 * @return ArrayList<Service>
	 */
	public ArrayList<ServiceInterface> getServicesAsArrayList()
	{
		return services;
	}
	
	/**
	 * getConnectedServicesAsArrayList
	 * 
	 * @param SharedPreferences settings
	 * @return ArrayList<Service>
	 */
	public ArrayList<ServiceInterface> getConnectedServicesAsArrayList(SharedPreferences settings)
	{
		ArrayList<ServiceInterface> connected_services = new ArrayList<ServiceInterface>();
		
		for (ServiceInterface service : services)
		{
			if (service.connected(settings))
				connected_services.add(service);
		}
		return connected_services;
	}
	
	/**
	 * getLogoDrawables
	 * 
	 * @return ArrayList<Integer>
	 */
	public ArrayList<Integer> getLogoDrawables()
	{
		ArrayList<Integer> drawables = new ArrayList<Integer>();
		
		for (ServiceInterface service : services)
		{
			drawables.add(service.getLogoDrawable());
		}
		
		return drawables;
	}
	
	/**
	 * atLeastOneConnected
	 * 
	 * @param prefs
	 * @return boolean
	 */
	public boolean atLeastOneConnected(SharedPreferences prefs)
	{
		boolean result = false;
		
		for (ServiceInterface service : services)
		{
			if (service.connected(prefs))
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
	 * @param String query
	 * @param String longitude
	 * @param String latutude
	 * @param SharedParameters settings
	 * @return ArrayList<Locale>
	 */
	public ArrayList<Locale> getAllLocations(String query, String longitude, String latitude, SharedPreferences settings)
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<ArrayList<Locale>> location_lists = new ArrayList<ArrayList<Locale>>();
		
		// get location request threads
		for (ServiceInterface service : services)
		{
			if (service.connected(settings))
			{
				Log.i(TAG, "Creating thread for service " + service.getName());
				threads.add(new Thread(service.getAPIInterface().getLocationThread(query, longitude, latitude, settings), service.getName()));
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
			if (service.connected(settings))
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
	 * @param settings
	 */
	public HashMap<Integer, Boolean> checkIn(ArrayList<Integer> service_ids, Locale location, SharedPreferences settings)
	{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		HashMap<Integer, Boolean> checkin_statuses = new HashMap<Integer, Boolean>();
		
		// get location request threads
		for (int service_id : service_ids)
		{
			ServiceInterface service = getServiceById(service_id);
			if (service.connected(settings))
				threads.add(new Thread(service.getAPIInterface().getCheckInThread(location, settings), service.getName()));			
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
			if (service.connected(settings))
				checkin_statuses.put(service.getId(), service.getAPIInterface().getLatestCheckInStatus());
		}
		
		return checkin_statuses;
	}
}