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

import java.util.ArrayList;

import com.davidivins.checkin4me.adapters.SettingsAdapter;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.interfaces.ServiceInterface;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * LocationDetails
 * 
 * @author david ivins
 */
public class Settings extends ListActivity implements OnItemClickListener
{
	boolean displaying_settings;
	ArrayList<ServiceInterface> services;
	
	/**
	 * onCreate
	 * 
	 * @param saved_instance_state
	 */
	@Override
	public void onCreate(Bundle saved_instance_state)
	{
		super.onCreate(saved_instance_state);		
		setContentView(GeneratedResources.getLayout("settings"));
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		displaying_settings = false;
		services = Services.getInstance(this).getConnectedServicesWithSettingsAsArrayList(settings);

		displayServices();
		
		getListView().setTextFilterEnabled(true);
		getListView().setBackgroundColor(Color.WHITE);
		getListView().setCacheColorHint(Color.WHITE);
	}
	
	/**
	 * displayServices
	 */
	public void displayServices()
	{
		displaying_settings = false;

		final String[] services = { "Facebook Settings", "Foursquare Settings", "Gowalla Settings", "Visit the Feedback Site" };
		SettingsAdapter adapter = new SettingsAdapter(
				this, GeneratedResources.getLayout("settings_row"), services);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		this.setTitle("Settings");
	}
	
	/**
	 * displaySettings
	 * 
	 * @param service_id
	 */
	public void displaySettings(int service_id)
	{
		displaying_settings = true;
		final String[] settings = { "Post to Facebook", "Post to Twitter" };

		SettingsAdapter adapter = new SettingsAdapter(
				this, GeneratedResources.getLayout("settings_row"), settings);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(null);
		this.setTitle("Service Settings");
	}
	
	/**
	 * onItemClick
	 * 
	 * @param AdapterView<?> adapter_view
	 * @param View view
	 * @param int  
	 * @param long arg3
	 */
	public void onItemClick(AdapterView<?> adapter_view, View view, int position, long arg3) 
	{
		displaySettings(position);
	}
	
	/**
	 * onBackPressed
	 */
	@Override
	public void onBackPressed()
	{
		if (displaying_settings)
		{
			displayServices();
		}
		else
		{
			super.onBackPressed();
		}
	}
}
