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

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TabHost;

import com.davidivins.checkin4me.core.Ad;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;

public class MainTabbedContainer extends TabActivity 
{
	public void onCreate(Bundle saved_instance_state) 
	{
		super.onCreate(saved_instance_state);
		GeneratedResources.generate(this);
		setContentView(GeneratedResources.getLayout("main"));
		
		Resources res = getResources();  // Resource object to get Drawables
		TabHost tab_host = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec;            // Resusable TabSpec for each tab
		Intent intent;                   // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ServiceConnection.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tab_host.newTabSpec("connect_services").setIndicator("Connect",
			res.getDrawable(GeneratedResources.getLayout("ic_tab_connect_services"))).setContent(intent);
		tab_host.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, NearbyPlaces.class);
		spec = tab_host.newTabSpec("nearby_places").setIndicator("Nearby Places",
			res.getDrawable(GeneratedResources.getLayout("ic_tab_nearby_places"))).setContent(intent);
		tab_host.addTab(spec);

		intent = new Intent().setClass(this, Settings.class);
		spec = tab_host.newTabSpec("settings").setIndicator("Settings",
			res.getDrawable(GeneratedResources.getLayout("ic_tab_settings"))).setContent(intent);
		tab_host.addTab(spec);

		// set current tab based on the service locations connected
		if (Services.getInstance(this).atLeastOneConnected(PreferenceManager.getDefaultSharedPreferences(this)))
			tab_host.setCurrentTab(1);
		else
			tab_host.setCurrentTab(0);
		
		// display the add if this is not the pro version
		Ad ad = new Ad(this);
		ad.refreshAd();
	}
}