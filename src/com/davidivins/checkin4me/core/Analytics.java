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
import android.util.Log;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import java.io.InputStream;
import java.util.Properties;

/**
 * Analytics
 * 
 * @author david
 */
public class Analytics 
{
	private static final String TAG = Analytics.class.getSimpleName();
	private Properties config;
	private GoogleAnalyticsTracker tracker;
	
	/**
	 * Analytics
	 * 
	 * @param activity
	 */
	public Analytics(Activity activity)
	{
		config = new Properties();
		
		try 
		{
			InputStream config_file = activity.getResources().openRawResource(GeneratedResources.getRaw("analytics"));
			config.load(config_file);
			
			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.start(config.getProperty("ua_number", "-1"), activity);
			
			Log.i(TAG, "Started tracker for ua_number: " + config.getProperty("ua_number", "-1"));

		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Failed to open config file");
		}
	}
	
	/**
	 * getTracker
	 * 
	 * @return GoogleAnalyticsTracker
	 */
	public GoogleAnalyticsTracker getTracker()
	{	
		return tracker;
	}
}