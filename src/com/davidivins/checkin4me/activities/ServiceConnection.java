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

import com.davidivins.checkin4me.adapters.ServiceConnectionAdapter;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;

/**
 * ServiceConnection
 * 
 * @author david ivins
 */
public class ServiceConnection extends ListActivity implements OnItemClickListener
{
	private static final String TAG = "ServiceConnection";
	private static int latest_service_id_selected = 0;
	
	/**
	 * onCreate
	 * 
	 * @param Bundle savedInstanceState
	 */
	@Override
	public void onCreate(Bundle saved_instance_state)
	{
		super.onCreate(saved_instance_state);
		GeneratedResources.generate(this);

		// set the current layout for the activity
		setContentView(GeneratedResources.getLayout("service_connection"));
		
		// display list of services
		ServiceConnectionAdapter adapter = new ServiceConnectionAdapter(this, GeneratedResources.getLayout("service_connection_row"), Services.getInstance(this).getLogoDrawables());
		setListAdapter(adapter);
		
		// set list view properties
        getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(this);
		getListView().setBackgroundColor(Color.WHITE);
		getListView().setCacheColorHint(Color.WHITE);
	}
	
	/**
	 * onItemClick
	 * 
	 * @param AdapterView<?> arg0
	 * @param View view
	 * @param int position
	 * @param long row
	 */
	public void onItemClick(AdapterView<?> arg0, View view, int position, long row) 
	{
		// save position as service id for service connection activity
		Log.i(TAG, "clicked service id = " + position);

		latest_service_id_selected = position;
		
		if (Services.getInstance(this).getServiceById(position).getOAuthConnector() != null)
		{
			if (Services.getInstance(this).getServiceById(position).connected())
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(Services.getInstance(this).getServiceById(position).getName() + 
					" is already connected. Do you wish to reconnect it?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							beginAuthorization();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
				
				AlertDialog alert = builder.create();
				alert.show();
			}
			else
			{
				beginAuthorization();
			}
		}
		else
		{
			CharSequence msg = Services.getInstance(this).getServiceById(position).getName()
				+ " doesn't work yet :(";
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			Log.e(TAG, Services.getInstance(this).getServiceById(position).getName() + " service doesn't work yet :(");
		}
	}
	
	/**
	 * beginAuthorization
	 */
	private void beginAuthorization()
	{
		Intent i = new Intent(this, Authorization.class);
		i.putExtra("service_id", latest_service_id_selected);
		startActivity(i);
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
