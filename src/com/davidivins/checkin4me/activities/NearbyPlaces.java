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

import com.davidivins.checkin4me.adapters.LocaleAdapter;
import com.davidivins.checkin4me.core.Ad;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.listeners.CleanableProgressDialogListener;
import com.davidivins.checkin4me.listeners.GPSTimeoutListener;
import com.davidivins.checkin4me.listeners.LocationsRetrieverListener;
import com.davidivins.checkin4me.listeners.NetworkTimeoutListener;
import com.davidivins.checkin4me.monitors.GPSTimeoutMonitor;
import com.davidivins.checkin4me.monitors.NetworkTimeoutMonitor;
import com.davidivins.checkin4me.runnables.LocationsRetriever;
import com.davidivins.checkin4me.util.CleanableProgressDialog;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * NearbyPlaces
 * 
 * @author david ivins
 */
public class NearbyPlaces extends ListActivity 
	implements OnItemClickListener, LocationsRetrieverListener, GPSTimeoutListener, 
		NetworkTimeoutListener, CleanableProgressDialogListener, LocationListener
{
	private static final String TAG = "NearbyPlaces";
	
	private static ProgressDialog progress_dialog          = null;
	private Thread locations_thread                        = null;
	private Thread gps_timeout_thread                      = null;
	private Thread network_timeout_thread                  = null;
	private LocationsRetriever locations_runnable          = null;
	private GPSTimeoutMonitor gps_timeout_runnable         = null;
	private NetworkTimeoutMonitor network_timeout_runnable = null;
	private boolean timeouts_cancelled                     = false;
	
	private Runnable locations_retrieved_callback          = null;
	private Runnable gps_timeout_callback                  = null;
	private Runnable network_timeout_callback              = null;
	private Runnable keyboard_gone_callback                = null;
	private Runnable display_progress_dialog               = null;
	private Handler handler                                = null;

	private ArrayList<Locale> current_locations            = null;
	private String current_longitude                       = null;
	private String current_latitude                        = null;
	private String current_query                           = null;
		
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
		
		// if no services are connected, display error message, otherwise, process intent
		if (Services.getInstance(this).atLeastOneConnected())
		{
			String pending_query = null;
			boolean query_changed = false;
			
			// check if latest query is a change from the previous query
			if (null != getParent())
			{
				pending_query = getParent().getIntent().getStringExtra("query");
				query_changed = !queriesAreTheSame(current_query, pending_query);
			}
			
			// store pending query as current query
			if (null != pending_query)
				current_query = pending_query.trim();
			
			Log.i(TAG, "current_query = " + current_query);
			
			// if a list doesn't exist yet
			if (null == getListAdapter() || query_changed)
			{
				// if we don't have coordinates, request them. otherwise, just build the location list
				if (null == current_locations || null == current_longitude || null == current_latitude || query_changed)
					requestCoordinates(query_changed);
				else
					setLocationsList();
		
				// set up gui
				setContentView(GeneratedResources.getLayout("nearby_places"));
				getListView().setTextFilterEnabled(true);
				getListView().setOnItemClickListener(this);
			}
		}
		else
		{
			setContentView(GeneratedResources.getLayout("nearby_places"));
			TextView empty_list = (TextView)findViewById(android.R.id.empty);
			empty_list.setText("No services connected!");
		}
	}
	
	/**
	 * onStop
	 */
	public void onStop()
	{
		super.onStop();
		cleanUp();
	}
	
	/**
	 * onPause
	 */
	public void onPause()
	{
		super.onPause();
		cleanUp();
	}
	
	/**
	 * onDestroy
	 */
	public void onDestroy()
	{
		super.onDestroy();
		cleanUp();
	}
	
	/**
	 * setup
	 */
	private void setup()
	{		
		if (null == handler)
			handler = new Handler();
		
		// acts as callback from locations thread
		if (null == locations_retrieved_callback)
		{
			locations_retrieved_callback = new Runnable() 
			{
				public void run() 
				{
					newLocationsAvailable();
				}
			};
		}
		
		// acts as callback from gps thread
		if (null == gps_timeout_callback)
		{
			gps_timeout_callback = new Runnable()
			{
				public void run()
				{
					if (!timeouts_cancelled)
						GPSTimeout();
				}
			};
		}
		
		// acts as callback from network thread
		if (null == network_timeout_callback)
		{
			network_timeout_callback = new Runnable()
			{
				public void run()
				{
					if (!timeouts_cancelled)
						NetworkTimeout();
				}
			};
		}
		
		// acts as callback from keyboard gone event
		if (null == keyboard_gone_callback)
		{
			keyboard_gone_callback = new Runnable()
			{
				public void run()
				{
					NetworkTimeout();
				}
			};
		}
	}
	
	/**
	 * cleanUp
	 */
	private void cleanUp()
	{
		// cleanup timeout threads
		timeouts_cancelled = true;
		killTimeouts();
		
		// stop the gps when pausing the activity
		stopLocationListener();
		
		// cancel location thread		
		if (null != locations_thread)
		{
			if (null != handler)
				handler.removeCallbacks(locations_retrieved_callback);
			
			if (null != locations_runnable)
				locations_runnable.destroyHandler();
			
			locations_retrieved_callback = null;
		}
		
		if (null != display_progress_dialog)
		{
			if (null != handler)
				handler.removeCallbacks(display_progress_dialog);
			
			display_progress_dialog = null;
		}
		
		if (null != handler)
			handler = null;
		
		// cancel any dialogs showing
		cancelProgressDialog();
	}
	
	/**
	 * killTimeouts
	 */
	private void killTimeouts()
	{
		if (null != gps_timeout_thread)
		{
			if (null != handler)
				handler.removeCallbacks(gps_timeout_callback);
			
			if (null != gps_timeout_runnable)
				gps_timeout_runnable.destroyHandler();
			gps_timeout_callback = null;
		}
		
		if (null != network_timeout_thread)
		{
			if (null != handler)
				handler.removeCallbacks(network_timeout_callback);
			
			if (null != network_timeout_runnable)
				network_timeout_runnable.destroyHandler();
			
			network_timeout_callback = null;
		}
	}
    
	/**
	 * requestCoordinates
	 */
	public void requestCoordinates(boolean query_changed)
	{
		setup();
		timeouts_cancelled = false;
		
		// cancel acquiring location dialog
		cancelProgressDialog();

		// check that gps location is available
		if (((LocationManager)this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(
				LocationManager.GPS_PROVIDER))
		{
			// start timeout thread for gps coordinates
			gps_timeout_runnable = new GPSTimeoutMonitor(this, handler);
			gps_timeout_thread = new Thread(gps_timeout_runnable, "GPSTimeoutMonitor");
			gps_timeout_thread.start();
				
			// Register the listener with the Location Manager to receive location updates
			stopLocationListener();
			startGPSLocationListener();
			
			// display acquiring gps progress dialog
			if (current_query == null || !query_changed)
			{
				startProgressDialog("Acquiring GPS Location...");
			}
			else
			{
				// delay displaying the progress dialog by 1 second to allow for a soft
				// keyboard (from a possible search) to be hidden first. if the progress
				// dialog shows up before the keyboard is hidden, the dialog will be killed
				// upon hiding the keyboard.
				display_progress_dialog = new Runnable() 
				{ 
					public void run()
					{
						startProgressDialog("Acquiring GPS Location...");	
					}
				};
				
				handler.postDelayed(display_progress_dialog, 700);
			}
		}
		else
		{
			GPSTimeout();
		}
	}

	/**
	 * GPSTimeout
	 */
	public void GPSTimeout()
	{
		Log.i(TAG, "GPSTimeout");
		
		// cancel acquiring location dialog
		cancelProgressDialog();
		
		// cancel updates for gps
		stopLocationListener();
		
		// get network info
		NetworkInfo mobile_network_info = 
			((ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi_network_info = 
			((ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				
		// check that network location is available
		if (((LocationManager)this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(
				LocationManager.NETWORK_PROVIDER) && 
				(mobile_network_info.isConnectedOrConnecting() || wifi_network_info.isConnectedOrConnecting()))
		{
			// start network location timeout
			network_timeout_runnable = new NetworkTimeoutMonitor(this, handler);
			network_timeout_thread = new Thread(network_timeout_runnable, "NetworkTimeoutMonitor");
			network_timeout_thread.start();
		
			// Register the listener with the Location Manager to receive location updates
			startNetworkLocationListener();
			
			// show acquiring network dialog
			startProgressDialog("Acquiring Network Location...");
		}
		else
		{
			NetworkTimeout();
		}
	}
	
	/**
	 * NetworkTimeout
	 */
	public void NetworkTimeout()
	{
		Log.i(TAG, "NetworkTimeout");
		
		// cancel acquiring location dialog
		cancelProgressDialog();
		
		// remove location updates 
		stopLocationListener();
		
		// show error
		Toast.makeText(this, "GPS and Network Connection Unavailable.", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * onLocationChanged
	 * 
	 * @param Locale location
	 */
	public void onLocationChanged(Location location) 
	{
		timeouts_cancelled = true;
		killTimeouts();
		
		// set current location in ads
		Ad.setLocation(location);
		
		// cancel further updates
		((LocationManager)getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
    	
		// cancel acquiring location dialog and start retrieving locations dialog
		startProgressDialog("Retrieving Locations...");
		
		// get longitude and latitude
		current_longitude = Double.toString(location.getLongitude());
		current_latitude = Double.toString(location.getLatitude());
		
		// get preferences for retrieving locations
		SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(this);
		Editor persistent_storage_editor = persistent_storage.edit();
		
		// store user's current longitude and latitude
		persistent_storage_editor.putString("current_longitude", current_longitude);
		persistent_storage_editor.putString("current_latitude", current_latitude);
		persistent_storage_editor.commit();

		locations_runnable = new LocationsRetriever(this, this, handler, current_query, current_longitude, current_latitude, persistent_storage);
		locations_thread = new Thread(locations_runnable, "LocationThread");
		locations_thread.start();
	}
	
	/**
	 * newLocationsAvailable
	 */
	public void newLocationsAvailable()
	{
		Log.i(TAG, "received new location data.");
		
		// setup list for retrieved locations
		current_locations = locations_runnable.getLocationsRetrieved();
		
		// set locations list
		setLocationsList();
				
		// cancel loading dialog
		cancelProgressDialog();
	}

	/**
	 * displayProgressDialog
	 */
	public void startProgressDialog(String text)
	{
		// cancel any existing dialog
		cancelProgressDialog();
		
		// create new dialog and display
		progress_dialog = new CleanableProgressDialog(this, this, "", text, true);
		progress_dialog.show();
	}
	
	/**
	 * cancelProgressDialog
	 */
	public void cancelProgressDialog()
	{
		// check if a progress dialog exists
		if (null != progress_dialog)
		{
			// cancel the dialog if it is showing
			if (progress_dialog.isShowing())
				progress_dialog.cancel();
			
			progress_dialog = null;
		}		
	}
	
	/**
	 * setLocationsList
	 */
	private void setLocationsList()
	{
		if (1 == current_locations.size())
		{
			LocaleAdapter adapter = new LocaleAdapter(
					this, GeneratedResources.getLayout("nearby_place_row"), current_locations);
			setListAdapter(adapter);
			storeLocationAndViewDetails(0);
		}
		else if (0 < current_locations.size())
		{
			LocaleAdapter adapter = new LocaleAdapter(
				this, GeneratedResources.getLayout("nearby_place_row"), current_locations);
			setListAdapter(adapter);
		}
		else
		{
			this.setListAdapter(null);
		}
	}
	
	/**
	 * getLocationsRetrievedCallback
	 */
	public Runnable getLocationsRetrievedCallback()
	{
		return locations_retrieved_callback;
	}
	
	/**
	 * getGPSTimeoutCallback
	 */
	public Runnable getGPSTimeoutCallback()
	{
		return gps_timeout_callback;
	}
	
	/**
	 * getNetworkTimeoutCallback
	 */
	public Runnable getNetworkTimeoutCallback()
	{
		return network_timeout_callback;
	}
	
	/**
	 * getKeyboardGoneCallback
	 */
	public Runnable getKeyboardGoneCallback()
	{
		return keyboard_gone_callback;
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
		boolean qr_scanner_installed = false;
		
		try 
		{
			getPackageManager().getPackageInfo("com.google.zxing.client.android", 0);
			qr_scanner_installed = true;
		} 
		catch (Exception e) 
		{
			Log.d(TAG, "QR scanner not installed");
		}
		
		// if no services are connected, don't display menu options
		if (Services.getInstance(this).atLeastOneConnected())
		{
			if (qr_scanner_installed)
				inflater.inflate(GeneratedResources.getMenu("nearby_places_with_qr_scanner"), menu);
			else
				inflater.inflate(GeneratedResources.getMenu("nearby_places"), menu);
		}
		else
		{
			inflater.inflate(GeneratedResources.getMenu("feedback_only"), menu);
		}
		
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
		
		if (GeneratedResources.getId("refresh") == id)
		{
			// check if a query already exists and prompt user to clear it
			if (current_query != null)
				displayClearQueryAlert();
			else
				requestCoordinates(false);

			result = true;
		}
		else if (GeneratedResources.getId("search") == id)
		{
			// this has to go through the tabbed container
			getParent().onSearchRequested(); 
			result = true;
		}
		else if (GeneratedResources.getId("feedback") == id)
		{
			startActivity(new Intent(this, Feedback.class));
			result = true;
		}
		else if (GeneratedResources.getId("qr_code") == id)
		{
	        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	        intent.setPackage("com.google.zxing.client.android");
	        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	        startActivityForResult(intent, 0);
			result = true;
		}
		else
		{
			// do nothing
		}
		
		return result;
	}
	
	/**
	 * onActivityResult
	 * 
	 * @param request_code
	 * @param result_code
	 * @param intent
	 */
	@Override
	public void onActivityResult(int request_code, int result_code, Intent intent) 
	{
		super.onActivityResult(request_code, result_code, intent);
		Log.i(TAG, "request_code = " + request_code);
		Log.i(TAG, "result_code = " + result_code);

		if (request_code == 0) 
		{
			if (result_code == RESULT_OK) 
			{
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				Log.i(TAG, "format = " + format);
				
				if (format.equals("QR_CODE"))
				{
					current_query = intent.getStringExtra("SCAN_RESULT");
					requestCoordinates(true);
				}
				else
				{
					Log.i(TAG, "not a QR_CODE. should not be hit.");
				}
			} 
			else if (result_code == RESULT_CANCELED) 
			{
				Log.i(TAG, "cancelled");
			}
		}
		else
		{
			Log.i(TAG, "something strange happened");
		}
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
		storeLocationAndViewDetails(position);
	}
	
	/**
	 * storeLocationAndViewDetails
	 * @param location_index
	 */
	public void storeLocationAndViewDetails(int location_index)
	{
		// get the settings and editor
		SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (0 != current_locations.size() && 0 <= location_index && current_locations.size() > location_index)
		{
			// store selected location
			Locale location = current_locations.get(location_index);
			location.store(persistent_storage);
		
			// load location details activity
			startActivity(new Intent(this, LocationDetails.class));
		}
		else
		{
			// weird case showing up in crash logs.  refresh current state with new list
			// FIXED by changing current locations to not be static
			// leaving check in for sanity's sake
			requestCoordinates(false);
		}
	}
	
	/**
	 * onKeyDown
	 * 
	 * @param key_code
	 * @param event
	 * @return boolean
	 */
	@Override
	public boolean onKeyDown(int key_code, KeyEvent event) 
	{
		// kill threads, timeouts, and location services on home button
		if (event.getAction() == KeyEvent.ACTION_DOWN) 
		{
			switch (key_code) 
			{
				case KeyEvent.KEYCODE_HOME:
				case KeyEvent.KEYCODE_BACK:
				case KeyEvent.KEYCODE_SEARCH:
					cleanUp();
			}
		}
		
		return super.onKeyDown(key_code, event);
	}

	/**
	 * onDialogInterruptedByBackButton
	 */
	public void onDialogInterruptedByBackButton() 
	{
		cleanUp();
	}

	/**
	 * onDialogInterruptedBySearchButton
	 */
	public void onDialogInterruptedBySearchButton() 
	{
		cleanUp();
	}
	
	/**
	 * displayClearQueryAlert
	 */
	public void displayClearQueryAlert()
	{ 
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setMessage("Would you like to clear your current search of \"" + current_query + "\" before refreshing?");
		alert.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				current_query = null;
				requestCoordinates(false);
			}
		}); 
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int button) {
				requestCoordinates(false);
			}
		});
		
		alert.show();
	}
	
	/**
	 * queriesAreTheSame
	 */
	private boolean queriesAreTheSame(String query1, String query2)
	{
		boolean result = false;
		
		if ((null == query1 && null == query2) ||
			(null != query1 && null != query2 && query1.equals(query2.trim())))
			result = true;
		
		return result;
	}
	
	/**
	 * startGPSLocationListener
	 */
	public void startGPSLocationListener()
	{
		((LocationManager)this.getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	/**
	 * startNetworkLocationListener
	 */
	public void startNetworkLocationListener()
	{
		((LocationManager)this.getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	/**
	 * stopLocationListener
	 */
	public void stopLocationListener()
	{
		((LocationManager)this.getSystemService(Context.LOCATION_SERVICE)).removeUpdates(this);
	}
	
	/**
	 * unused interface methods of GPS
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	public void onProviderEnabled(String provider) {}
	public void onProviderDisabled(String provider) {}
}