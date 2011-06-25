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
package com.davidivins.checkin4me.monitors;

import com.davidivins.checkin4me.listeners.NetworkTimeoutListener;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * NetworkTimeoutMonitor
 * 
 * @author david
 */
public class NetworkTimeoutMonitor implements Runnable
{
	private static final String TAG            = NetworkTimeoutMonitor.class.getName();
	private static final int FIFTHTEEN_SECONDS = 15000;
	
	private NetworkTimeoutListener activity;
	private Handler handler;
	 
	/**
	 * NetworkTimeoutMonitor
	 * 
	 * @param NetworkTimeoutListener
	 */
	public NetworkTimeoutMonitor(NetworkTimeoutListener activity, Handler handler)
	{
		this.activity = activity;
		this.handler = handler;
	}
	
	/**
	 * run
	 */
	public void run()
	{
		SystemClock.sleep(FIFTHTEEN_SECONDS);
		
		Log.i(TAG, "Network Location Timeout");
		
		if (null != handler)
			handler.post(activity.getNetworkTimeoutCallback());
	}
	
	/**
	 * destroyHandler
	 */
	public void destroyHandler()
	{
		handler = null;
	}
}