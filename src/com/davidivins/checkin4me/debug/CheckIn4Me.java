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
package com.davidivins.checkin4me.debug;

import com.davidivins.checkin4me.core.Analytics;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.StartProgramDelayer;
import com.davidivins.checkin4me.core.UpdateManager;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * CheckIn4Me **DEBUG**
 * 
 * @author david ivins
 */
public class CheckIn4Me extends Activity
{
	private static final String TAG = "CheckIn4Me";
	private Handler handler = new Handler();
	private StartProgramDelayer program = null;
	
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
		setContentView(GeneratedResources.getLayout("checkin4me"));
		
		// tracker
		Analytics analytics = new Analytics(this);
		GoogleAnalyticsTracker tracker = analytics.getTracker();
		tracker.trackPageView("/checkin4me_debug");
		tracker.dispatch();
		tracker.stop();
		
		// stuff to do on first run of updated version
		UpdateManager.performCleanInstallDefaultsIfNecessary(this);
		UpdateManager.performUpdateIfNecessary(this);
	}
	
	/**
	 * onResume
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		determineAction();
	}
	
	/**
	 * determineAction
	 */
	private void determineAction()
	{
		Intent starting_intent = getIntent();
		String url_from_data   = starting_intent.getDataString().trim();
		String url_from_extra  = starting_intent.getStringExtra("url");
		
		Log.i(TAG, "action = " + starting_intent.getAction());

		// handle intercepted url from qr code, nfc tag, etc.
		if (starting_intent.getAction().equals(Intent.ACTION_VIEW))
		{
			// verify valid mobile url with id for at least one connected service
			Toast.makeText(this, url_from_data, Toast.LENGTH_SHORT).show();
		}
		// handle url from NFC add-on app
		else if (starting_intent.getAction().equals("com.davidivins.checkin4me.action.NFC"))
		{
			Toast.makeText(this, url_from_extra, Toast.LENGTH_SHORT).show();
		}
		// run regular program if no intercepts for add-on calls
		else
		{
			runProgram();
		}
	}
	
	/**
	 * onStop
	 */
	@Override
	public void onStop()
	{
		super.onStop();
		
		if (program != null)
		{
			handler.removeCallbacks(program);
			program = null;
		}		
	}
	
	/**
	 * runProgram
	 */
	private void runProgram()
	{
		if (program == null)
			program = new StartProgramDelayer(this);
		
		handler.postDelayed(program, 2000);
	}
}
