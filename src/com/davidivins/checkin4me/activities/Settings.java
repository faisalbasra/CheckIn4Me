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

import com.davidivins.checkin4me.adapters.SettingsAdapter;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * Settings
 * 
 * **THIS CANNOT EXTEND ExpandableListActivity. Bug in Android on orientation change will cause crash.**
 * 
 * @author david
 */
public class Settings extends Activity
{
	//static private final String TAG = Settings.class.getSimpleName();
	
	/**
	 * onCreate
	 * 
	 * @param Bundle saved_instance_state
	 */
	@Override
	public void onCreate(Bundle saved_instance_state) 
	{
		super.onCreate(saved_instance_state);
		GeneratedResources.generate(this);
		setContentView(GeneratedResources.getLayout("settings"));

		// fetch list and set empty view
		ExpandableListView list = (ExpandableListView)this.findViewById(GeneratedResources.getId("settings"));
		TextView empty = (TextView)this.findViewById(GeneratedResources.getId("empty"));
		list.setEmptyView(empty);

		// don't list any settings if there are not any services connected
		if (Services.getInstance(this).atLeastOneConnected())
		{		
			// Set up our adapter and listener
			list.setAdapter(new SettingsAdapter(this));
		}
	}
	
	/**
	 * onCreateOptionsMenu
	 * 
	 * @param Menu menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(GeneratedResources.getMenu("feedback_only"), menu);	
		return true;
	}
	
	/**
	 * onOptionsItemSelected
	 * 
	 * @param MenuItem item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		boolean result = false;		
		int id = item.getItemId();
		
		if (GeneratedResources.getId("feedback") == id)
		{
			startActivity(new Intent(this, Feedback.class));
			result = true;
		}

		
		return result;
	}
}
