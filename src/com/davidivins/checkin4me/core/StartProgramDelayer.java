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

import com.davidivins.checkin4me.activities.NearbyPlaces;
import com.davidivins.checkin4me.activities.ServiceConnection;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * StartProgram
 * 
 * @author david
 */
public class StartProgramDelayer implements Runnable
{
	private Activity activity;
	
	/**
	 * StartProgramDelayer
	 * 
	 * @param activity
	 */
	public StartProgramDelayer(Activity activity)
	{
		this.activity = activity;
	}

	/**
	 * run
	 */
	public void run() 
	{
		Intent intent;
		
		// go straight to nearby places if atleast one service is connected
		if (Services.getInstance(activity).atLeastOneConnected(PreferenceManager.getDefaultSharedPreferences(activity)))
			intent = new Intent(activity, NearbyPlaces.class);
		else
			intent = new Intent(activity, ServiceConnection.class);
		
		activity.startActivity(intent);
	}
}