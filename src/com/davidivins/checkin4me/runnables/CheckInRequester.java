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
import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;

import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.listeners.CheckInRequesterListener;

/**
 * CheckInRequester
 * 
 * @author david
 */
public class CheckInRequester implements Runnable
{
	Activity activity;
	CheckInRequesterListener listener;
	Handler handler;
	ArrayList<Integer> service_ids;
	Locale location;
	String message;
	SharedPreferences persistent_storage;
	HashMap<Integer, Boolean> checkin_statuses;
	
	/**
	 * CheckInRequester
	 * 
	 * @param activity
	 * @param service_ids
	 * @param persistent_storage
	 */
	public CheckInRequester(Activity activity, CheckInRequesterListener listener, Handler handler, 
			ArrayList<Integer> service_ids, Locale location, String message, SharedPreferences persistent_storage)
	{
		this.activity = activity;
		this.listener = listener;
		this.handler = handler;
		this.service_ids = service_ids;
		this.location = location;
		this.message = message;
		this.persistent_storage = persistent_storage;
		
		this.checkin_statuses = new HashMap<Integer, Boolean>();
	}
	
	/**
	 * run
	 */
	public void run() 
	{		
		checkin_statuses = Services.getInstance(activity).checkIn(service_ids, location, message, persistent_storage);	
		handler.post(listener.getCheckInCompletedCallback());
	}
	
	/**
	 * getCheckInStatuses
	 * 
	 * @return HashMap<Integer, Boolean>
	 */
	public HashMap<Integer, Boolean> getCheckInStatuses()
	{
		return checkin_statuses;
	}
}
