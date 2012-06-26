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
package com.davidivins.checkin4me.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.davidivins.checkin4me.adapters.ServiceCheckListAdapter;
import com.davidivins.checkin4me.core.*;
import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.interfaces.ServiceInterface;
import com.davidivins.checkin4me.listeners.implementations.CheckInMessageOnClickListener;
import com.davidivins.checkin4me.listeners.interfaces.CheckInRequesterListener;
import com.davidivins.checkin4me.listeners.interfaces.CleanableProgressDialogListener;
import com.davidivins.checkin4me.threads.CheckInRequester;
import com.davidivins.checkin4me.util.CleanableProgressDialog;
import com.davidivins.checkin4me.util.LocationOverlay;
import com.google.android.maps.*;

import java.io.InputStream;
import java.util.*;

/**
 * LocationDetails
 * 
 * displays location details to the user, prompts for check-in services and message.
 * 
 * @author david ivins
 */
public class LocationDetails 
	extends MapActivity 
	implements OnClickListener, DialogInterface.OnClickListener, CleanableProgressDialogListener, CheckInRequesterListener
{
	private static final String TAG                  = LocationDetails.class.getName();
	private static Properties config                 = null;
	private static ProgressDialog checking_in_dialog = null;
	private CheckInRequester checkin_requester       = null;
	private Locale current_location                  = new Locale();

	/**
	 * onCreate
	 * 
	 * called at the creation of the activity
	 * 
	 * @param saved_instance_state
	 */
	@Override
	public void onCreate(Bundle saved_instance_state)
	{
		super.onCreate(saved_instance_state);
		GeneratedResources.generate(this);
		setContentView(GeneratedResources.getLayout("location_details"));
		
		// display ad if this is not the pro version
		Ad ad = new Ad(this);
		ad.refreshAd();
		
		// get map config file if necessary
		if (config == null)
		{
			config = new Properties();
		
			try 
			{
				InputStream config_file = getResources().openRawResource(GeneratedResources.getRaw("google_maps"));
				config.load(config_file);
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "Failed to open config file");
			}
		}
		
		//
		// list stuff
		//
		ListView list_view = (ListView)findViewById(GeneratedResources.getId("location_service_list"));
		list_view.setTextFilterEnabled(true);
		list_view.setBackgroundColor(Color.WHITE);
		list_view.setCacheColorHint(Color.WHITE);
		
		// 
		// button stuff
		//
		Button button = (Button)findViewById(GeneratedResources.getId("check_in_button"));
		button.setOnClickListener(this);
		
		//
		// display any location details
		//
		displayLocationDetails();
	}
	
	/**
	 * onStop
	 * 
	 * called when the activity stops
	 */
	@Override
	public void onStop()
	{
		super.onStop();
		cleanUp();
	}
	
	/**
	 * onDialogInterruptedByBackButton
	 * 
	 * called when a cleanable dialog box displayed by this activity is cancelled by the back button 
	 */
	public void onDialogInterruptedByBackButton() 
	{
		cleanUp();
	}

	/**
	 * onDialogInterruptedBySearchButton
	 * 
	 * called when a cleanable dialog box displayed by this activity is cancelled by the search button
	 */
	public void onDialogInterruptedBySearchButton() 
	{
		cleanUp();
	}
	
	/**
	 * cleanUp
	 * 
	 * cancels any running checkins and removes and visible dialog or progress boxes
	 */
	private void cleanUp()
	{
		// cancel check-in requester if running
		if (null != checkin_requester)
			checkin_requester.cancel(true);
		
		// cancel any dialogs showing
		if (checking_in_dialog != null && checking_in_dialog.isShowing())
			checking_in_dialog.cancel();
	}
	
	/**
	 * onClick
	 * 
	 * processes a check-in button click.
	 * 
	 * @param view
	 */
	public void onClick(View view) 
	{	
		// inflate the alert view
		LayoutInflater factory = LayoutInflater.from(this);
		final View alert_view = factory.inflate(GeneratedResources.getLayout("checkin_message"), null);
        
		// build alert box, input box, and onclick listener
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		EditText input = (EditText)alert_view.findViewById(GeneratedResources.getId("checkin_message_text_entry"));
		CheckInMessageOnClickListener listener = new CheckInMessageOnClickListener(this, input);
		
		// set alert options
		alert.setView(alert_view);
		alert.setMessage("Check-In Message:");
		alert.setPositiveButton("Check-In", listener); 
		alert.setNegativeButton("Cancel", listener);
		alert.show();
	}

	/**
	 * checkIn
	 * 
	 * performs a check-in given a message and the latest location data
	 * 
	 * @param message
	 */
	public void checkIn(String message)
	{
		// cancel acquiring location dialog
		if (null != checking_in_dialog && checking_in_dialog.isShowing())
			checking_in_dialog.cancel();
		
		// get list and adapter
		ListView list_view = (ListView)findViewById(GeneratedResources.getId("location_service_list"));
		ServiceCheckListAdapter adapter = (ServiceCheckListAdapter)list_view.getAdapter();
		
		// retrieve services that were checked
		Map<Integer, Boolean> services_checked = adapter.getServicesChecked();
		List<Integer> service_ids = new ArrayList<Integer>();
		
		// pull out services checked
		Set<Integer> keys = services_checked.keySet();
		for(int key : keys)
		{
			Log.i(TAG, "service connected id = " + key + " and checked state = " + services_checked.get(key));
			
			if (services_checked.get(key))
				service_ids.add(key);
		}
		
		// if there are no services ids checked, do not check-in, alert the user
		if (service_ids.isEmpty())
		{
			Toast.makeText(this, "No services checked", Toast.LENGTH_SHORT).show();
		}
		else
		{	
			// display progress dialog to user
			checking_in_dialog = new CleanableProgressDialog(this, this, "", "Checking in...", true);
			checking_in_dialog.show();
			
			// create and start check-in thread
			SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(this);
			
			// start check-in thread
			checkin_requester = new CheckInRequester(this, this, service_ids, current_location, message, persistent_storage);
			checkin_requester.execute();
		}
	}
	
	/**
	 * checkInComplete
	 * 
	 * called at the completion of a check-in. cleans up and displays status of check-in.
	 * 
	 * @param checkin_statuses
	 */
	public void checkInComplete(Map<Integer, Boolean> checkin_statuses)
	{
		Log.i(TAG, "received check-in completed.");
    	
		// clean up
		cleanUp();
		
		// display check in dialog
		displayCheckInStatus(checkin_statuses);
	}
	
	/**
	 * displayLocationDetails
	 * 
	 * displays the latest location details to the user
	 */
	public void displayLocationDetails()
	{
		// load current location from preferences
		SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(this);
		current_location.load(persistent_storage);
		
		// load current longitude and latitude from preferences
		Double current_longitude = new Double(persistent_storage.getString("current_longitude", "0"));
		Double current_latitude  = new Double(persistent_storage.getString("current_latitude", "0"));
		
		// 
		// location name and address
		//
		TextView location_name = (TextView)findViewById(GeneratedResources.getId("location_name"));
		location_name.setText(current_location.getName());
		location_name.setTextSize(20);
		
		TextView location_description = (TextView)findViewById(GeneratedResources.getId("location_description"));
		location_description.setText(current_location.getAddress());
		
		//
		// map stuff
		//
		MapView location_map = new MapView(
			this, config.getProperty(MetaData.getInstance(this).getString("VERSION") + "_api_key"));
		location_map.setClickable(true);
		
		List<Overlay> map_overlays = location_map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(android.R.drawable.star_on);
		
		LocationOverlay location_overlay1 = new LocationOverlay(drawable, this);
		LocationOverlay location_overlay2 = new LocationOverlay(drawable, this);
		
		GeoPoint location_point = current_location.getCoordinatesAsGeoPoint();
		GeoPoint user_point = new GeoPoint((int)(current_latitude.doubleValue() * 1E6), (int)(current_longitude.doubleValue() * 1E6));
		
		OverlayItem overlayitem1 = new OverlayItem(location_point, current_location.getName(), current_location.getAddress());
		OverlayItem overlayitem2 = new OverlayItem(user_point, "You Are Here.", "Lat: " + current_latitude + "\nLong: " + current_longitude);
		
		location_overlay1.addOverlay(overlayitem1);
		location_overlay2.addOverlay(overlayitem2);
		
		map_overlays.add(location_overlay1);
		map_overlays.add(location_overlay2);
		
		MapController map_controller = location_map.getController();
		map_controller.setCenter(location_point);
		map_controller.setZoom(15);
		
		LinearLayout map_layout = (LinearLayout)findViewById(GeneratedResources.getId("location_map"));
		map_layout.addView(location_map);
		
		// 
		// service list stuff
		//
		Map<Integer, String> service_id_location_id_xref = current_location.getServiceIdToLocationIdMap();
		List<ServiceInterface> services = new ArrayList<ServiceInterface>();
		Set<Integer> service_ids = service_id_location_id_xref.keySet();
		
		for(int service_id : service_ids)
		{
			services.add(Services.getInstance(this).getServiceById(service_id));
		}
		
		ServiceCheckListAdapter adapter = new ServiceCheckListAdapter(this, GeneratedResources.getLayout("location_details_row"), services);
		
		ListView list_view = (ListView)findViewById(GeneratedResources.getId("location_service_list"));
		list_view.setAdapter(adapter);
	}
	
	/**
	 * displayCheckInStatus
	 * 
	 * displayes the check-in status to the user.
	 * 
	 * @param checkin_statuses
	 */
	public void displayCheckInStatus(Map<Integer, Boolean> checkin_statuses)
	{
		boolean some_succeeded = false;
		boolean some_failed = false;
		
		// retrieve layout inflater
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(GeneratedResources.getLayout("checkin_dialog"), 
			(ViewGroup)findViewById(GeneratedResources.getId("checkin_root")));
		LinearLayout checkin_root_dialog = (LinearLayout)layout.findViewById(GeneratedResources.getId("checkin_root"));

		// add service icons to layout
		LinearLayout successful_images = new LinearLayout(this);
		LinearLayout failure_images = new LinearLayout(this);
		
		// center icons
		successful_images.setGravity(0x11);
		failure_images.setGravity(0x11);
		
		// get icons for successful and failed check-ins
		Set<Integer> keys = checkin_statuses.keySet();
		for (int key : keys)
		{
			ImageView image = new ImageView(this);
			image.setImageResource(Services.getInstance(this).getServiceById(key).getIconDrawable());
			image.setPadding(0, 5, 5, 0);
			
			// if the check-in for this particular service was successful
			if (checkin_statuses.get(key))
			{
				successful_images.addView(image);
				some_succeeded= true;
			}
			else
			{
				failure_images.addView(image);
				some_failed = true;
			}
		}
		
		// successful checkins
		if (some_succeeded)
		{
			TextView successful_text = new TextView(this);
			successful_text.setText("We have you at " + current_location.getName() + " on:");
			successful_text.setGravity(0x11); // center text
			checkin_root_dialog.addView(successful_text);
			checkin_root_dialog.addView(successful_images);
		}
		
		// failed check-ins
		if (some_failed)
		{
			TextView failure_text = new TextView(this);
			failure_text.setText("Check-in failed at " + current_location.getName() + " on:");
			failure_text.setGravity(0x11); // center text

			if (some_succeeded) // spread them out
				failure_text.setPadding(0, 10, 0, 0);
			
			checkin_root_dialog.addView(failure_text);
			checkin_root_dialog.addView(failure_images);
		}

		// create alert dialog builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);		
		builder.setPositiveButton("OK", this);
		
		// create alert dialog
		AlertDialog alertDialog = builder.create();
		
		// set title/icon based on success or failure
		if (some_succeeded && some_failed) // some check-ins failed and some check-ins succeeded
		{
			alertDialog.setIcon(GeneratedResources.getDrawable("warning"));
			alertDialog.setTitle("Check-in Results Mixed!");
		}
		else if (some_failed) // all check-ins failed
		{
			alertDialog.setIcon(GeneratedResources.getDrawable("x"));
			alertDialog.setTitle("Check-in Failed!");
		}
		else // all check-ins succeeded
		{
			alertDialog.setIcon(GeneratedResources.getDrawable("check"));
			alertDialog.setTitle("Check-in Successful!");
		}
		
		// show check-in dialog box
		alertDialog.show(); 
	}

	/**
	 * onClick
	 * 
	 * returns the user to the location list when the ok button is hit in the check-in status dialog
	 * 
	 * @param dialog
	 * @param which
	 */
	public void onClick(DialogInterface dialog, int which) 
	{
		// return to nearby places
		finish();
	}
		
	/**
	 * isRouteDisplayed
	 * 
	 * override for isRouteDisplayed.
	 * 
	 * @return boolean
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
	
	/**
	 * onCreateOptionsMenu
	 * 
	 * displays the options menu when the options button is selected.
	 * 
	 * @param menu
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
	 * performs option selected by the user.
	 * 
	 * @param item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		boolean result = false;		
		int id = item.getItemId();
		
		// load feedback option
		if (GeneratedResources.getId("feedback") == id)
		{
			startActivity(new Intent(this, Feedback.class));
			result = true;
		}
		
		return result;
	}
}
