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
package com.davidivins.checkin4me.monitors;

import com.davidivins.checkin4me.listeners.GPSTimeoutListener;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * GPSTimeoutMonitor
 * 
 * @author david
 */
public class GPSTimeoutMonitor implements Runnable
{
	private static final String TAG      = GPSTimeoutMonitor.class.getName();
	private static final int TEN_SECONDS = 10000;
	
	private GPSTimeoutListener activity;
	private Handler handler;
	 
	/**
	 * GPSTimeoutMonitor
	 * 
	 * @param GPSTimeoutListener
	 */
	public GPSTimeoutMonitor(GPSTimeoutListener activity, Handler handler)
	{
		this.activity = activity;
		this.handler = handler;
	}
	
	/**
	 * run
	 */
	public void run()
	{
		SystemClock.sleep(TEN_SECONDS);
		
		Log.i(TAG, "GPS Location Timeout");
		
		if (null != handler)
			handler.post(activity.getGPSTimeoutCallback());
	}
	
	/**
	 * destroyHandler
	 */
	public void destroyHandler()
	{
		handler = null;
	}
}
