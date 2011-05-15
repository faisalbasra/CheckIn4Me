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

import java.io.InputStream;
import java.util.Properties;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import com.davidivins.checkin4me.listeners.AdmobListener;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class Ad 
{
	private static final String TAG          = "Ad";
		
	private Activity activity                = null;
	private Bundle meta_data                 = null;
	private Properties config                = null;
	
	private static Ad latest_instance        = null;
	private AdView ad                        = null;
	private static Location current_location = null;
	
	/**
	 * Ad
	 * 
	 * @param activity
	 */
	public Ad(Activity activity)
	{
		latest_instance = null;
		this.activity = activity;
		
		try
		{
			ApplicationInfo app_info = 
				activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
			meta_data = app_info.metaData;
		}
		catch(Exception e)
		{
			Log.i(TAG, "Failed to get app info");
			meta_data = null;
		}
		
		if (null == meta_data || !meta_data.getBoolean("IS_PRO_VERSION", false))
		{
			initializeConfig();
		}
		else
		{
			Log.i(TAG, "No ADMOB config file read. This is CheckIn4Me pro");
		}
		
		latest_instance = this;
	}
	
	/**
	 * initializeConfig
	 */
	private void initializeConfig()
	{
		if (null == config)
		{
			config = new Properties();
			
			try 
			{
				InputStream config_file = activity.getResources().openRawResource(GeneratedResources.getRaw("admob"));
				config.load(config_file);
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "Failed to open config file");
			}
		}
	}
	
	/**
	 * setLocation
	 */
	public static void setLocation(Location location)
	{
		current_location = location;
		
		// refresh the latest instance with new location
		if (latest_instance != null)
			latest_instance.refreshAd();
	}
	
	/**
	 * refreshAd
	 */
	public void refreshAd()
	{
		if (null == meta_data || !meta_data.getBoolean("IS_PRO_VERSION", false))
		{
			// get the main content view
			LinearLayout main_layout = (LinearLayout)activity.findViewById(GeneratedResources.getId("main_layout"));
			
			if (null != main_layout)
			{
				// if an ad doesn't exist yet, create it
				if (null == ad)
				{			
					// create a new 
					ad = new AdView(activity, AdSize.BANNER, config.getProperty("publisher_id", "-1"));
					main_layout.addView(ad, 0);
				}
				
				// create a new request
				AdRequest request = new AdRequest();
				
				// put in testing mode if this is the debug version of the app
				if (meta_data.getString("VERSION").equals("debug"))
					request.setTesting(true);
				
				// set the user's current location
				if (null != current_location)
					request.setLocation(current_location);
				
				// set the ad visibility and listener
				ad.setVisibility(View.VISIBLE);
				ad.setAdListener(new AdmobListener());
				
				// load the ad
				ad.loadAd(request);
			}
		}
		else
		{
			Log.i(TAG, "No AD added. This is CheckIn4Me pro");
		}
	}
}
