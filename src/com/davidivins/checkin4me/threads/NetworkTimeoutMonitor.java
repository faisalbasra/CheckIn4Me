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
package com.davidivins.checkin4me.threads;

import com.davidivins.checkin4me.listeners.interfaces.NetworkTimeoutListener;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

/**
 * NetworkTimeoutMonitor
 * 
 * @author david
 */
public class NetworkTimeoutMonitor extends AsyncTask<Void, Void, Void>
{
	private static final String TAG      = NetworkTimeoutMonitor.class.getSimpleName();
	private static final int TEN_SECONDS = 10000;
	
	private NetworkTimeoutListener listener;
	 
	/**
	 * NetworkTimeoutMonitor
	 * 
	 * @param listener
	 */
	public NetworkTimeoutMonitor(NetworkTimeoutListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * doInBackground
	 *
	 * Waits 10 seconds
	 * 
	 * @return Void
	 */
	@Override
	protected Void doInBackground(Void ... params)
	{
		SystemClock.sleep(TEN_SECONDS);
		Log.i(TAG, "Network Location Timeout");
		return null;
    }

	/**
	 * onPostExecute
	 * 
	 * notifies the listener of a timeout.
	 * 
	 * @param nothing
	 */
	@Override
	protected void onPostExecute(Void nothing)
	{
		listener.NetworkTimeout();
	}
}