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

import android.app.ExpandableListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

/**
 * Settings
 * 
 * @author david
 */
public class Settings extends ExpandableListActivity implements OnChildClickListener
{
	static private final String TAG = "Settings";
	
	/**
	 * onCreate
	 * 
	 * @param Bundle saved_instance_state
	 */
	@Override
	public void onCreate(Bundle saved_instance_state) 
	{
		super.onCreate(saved_instance_state);
		setContentView(GeneratedResources.getLayout("settings"));

		// don't list any settings if there are not any services connected
		if (Services.getInstance(this).atLeastOneConnected())
		{		
			// Set up our adapter
			SettingsAdapter adapter;
			adapter = new SettingsAdapter();
			adapter.setActivity(this);
			
			setListAdapter(adapter);
			getExpandableListView().setOnChildClickListener(this);
			getExpandableListView().setBackgroundColor(Color.WHITE);
			getExpandableListView().setCacheColorHint(Color.WHITE);
		}
	}

	/**
	 * onChildClick
	 * 
	 * @param ExpandableListView parent
	 * @param View view
	 * @param int group_position
	 * @param int child_position
	 * @param long id
	 * @return boolean
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int group_position, int child_position, long id) 
	{
		Log.i(TAG, "group_position = " + group_position);
		Log.i(TAG, "child_position = " + child_position);
		Log.i(TAG, "id = " + id);
		return true;
	}
}
