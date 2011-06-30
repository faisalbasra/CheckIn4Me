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

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.listeners.interfaces.CheckInRequesterListener;

/**
 * CheckInRequester
 * 
 * @author david
 */
public class CheckInRequester extends AsyncTask<Void, Void, HashMap<Integer, Boolean>> 
{
	Activity                 activity;
	CheckInRequesterListener listener;
	ArrayList<Integer>       service_ids;
	Locale                   location;
	String                   message;
	SharedPreferences        persistent_storage;
	
	/**
	 * constructor
	 * 
	 * @param activity
	 * @param listener
	 * @param handler
	 * @param service_ids
	 * @param location
	 * @param message
	 * @param persistent_storage
	 */
	public CheckInRequester(Activity activity, CheckInRequesterListener listener, ArrayList<Integer> service_ids, 
		Locale location, String message, SharedPreferences persistent_storage)
	{
		this.activity           = activity;
		this.listener           = listener;
		this.service_ids        = service_ids;
		this.location           = location;
		this.message            = message;
		this.persistent_storage = persistent_storage;		
	}
	
	/**
	 * doInBackground
	 *
	 * attempts to perform a check-in for each service. returns status of each attempt.
	 * 
	 * @return a hash map of ints and bools representing services and their statuses
	 */
	@Override
	protected HashMap<Integer, Boolean> doInBackground(Void ... params)
	{
		return Services.getInstance(activity).checkIn(service_ids, location, message, persistent_storage);
    }

	/**
	 * onPostExecute
	 * 
	 * notifies listener that the check-in process has completed and sends it statuses.
	 * 
	 * @param checkin_statuses
	 */
	@Override
	protected void onPostExecute(HashMap<Integer, Boolean> checkin_statuses)
	{
		listener.checkInComplete(checkin_statuses);
	}
}
