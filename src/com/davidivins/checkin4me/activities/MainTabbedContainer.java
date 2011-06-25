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
package com.davidivins.checkin4me.activities;

import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.davidivins.checkin4me.core.Ad;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;

public class MainTabbedContainer extends TabActivity
{
	private static final String TAG                  = MainTabbedContainer.class.getName();

	// tabs
	public static final int SERVICE_CONNECTION_TAB   = 0;
	public static final int NEARBY_PLACES_TAB        = 1;
	public static final int SETTINGS_TAB             = 2;
		
	public void onCreate(Bundle saved_instance_state) 
	{
		super.onCreate(saved_instance_state);
		GeneratedResources.generate(this);
		Log.i(TAG, "" + GeneratedResources.getLayout("main"));
		setContentView(GeneratedResources.getLayout("main"));
		
		Resources res = getResources();  // Resource object to get Drawables
		TabHost tab_host = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec;            // Resusable TabSpec for each tab
		Intent intent;                   // Reusable Intent for each tab

		// service connection tab
		intent = new Intent().setClass(this, ServiceConnection.class);
		spec = tab_host.newTabSpec("connect_services").setIndicator("Connect",
			res.getDrawable(GeneratedResources.getLayout("ic_tab_connect_services"))).setContent(intent);
		tab_host.addTab(spec);

		// nearby places tab
		intent = new Intent().setClass(this, NearbyPlaces.class);
		spec = tab_host.newTabSpec("nearby_places").setIndicator("Nearby Places",
			res.getDrawable(GeneratedResources.getLayout("ic_tab_nearby_places"))).setContent(intent);
		tab_host.addTab(spec);

		// settings tab
		intent = new Intent().setClass(this, Settings.class);
		spec = tab_host.newTabSpec("settings").setIndicator("Settings",
			res.getDrawable(GeneratedResources.getLayout("ic_tab_settings"))).setContent(intent);
		tab_host.addTab(spec);

		// if tab to set is specified
		int tab_to_display = this.getIntent().getIntExtra("tab_to_display", -1);
		if (-1 != tab_to_display)
		{			
			tab_host.setCurrentTab(tab_to_display);
		}
		// check if this is a search on nearby places
		else if (Intent.ACTION_SEARCH.equals(getIntent().getAction()) && 
			getIntent().getStringExtra(SearchManager.QUERY) != null)
		{			
			Log.i(TAG, "query = " + getIntent().getStringExtra(SearchManager.QUERY));	
			this.getIntent().putExtra("query", getIntent().getStringExtra(SearchManager.QUERY));
			tab_host.setCurrentTab(NEARBY_PLACES_TAB);
		}
		else
		{
			// set current tab based on the service locations connected
			if (Services.getInstance(this).atLeastOneConnected())
				tab_host.setCurrentTab(NEARBY_PLACES_TAB);
			else
				tab_host.setCurrentTab(SERVICE_CONNECTION_TAB);
		}
		
		// display the add if this is not the pro version
		Ad ad = new Ad(this);
		ad.refreshAd();
	}
	
	/**
	 * onSearchRequested
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onSearchRequested() 
	{
		Log.i(TAG, "search requested");
		
		boolean result = false;
		
		if (null != getTabHost() && NEARBY_PLACES_TAB == getTabHost().getCurrentTab())
			result = super.onSearchRequested();
		
		return result;
	}
}