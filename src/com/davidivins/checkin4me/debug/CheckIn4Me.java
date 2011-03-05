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
package com.davidivins.checkin4me.debug;

import com.davidivins.checkin4me.core.Analytics;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.StartProgramDelayer;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
//import android.preference.PreferenceManager;

/**
 * CheckIn4Me **DEBUG**
 * 
 * @author david ivins
 */
public class CheckIn4Me extends Activity
{
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
				
		Analytics analytics = new Analytics(this);
		GoogleAnalyticsTracker tracker = analytics.getTracker();
        tracker.trackPageView("/checkin4me_debug");
        tracker.dispatch();
        tracker.stop();
		
        // clear settings for this version
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//        String clear_settings_tag = "VERSION_1.6_HAS_CLEARED_SETTINGS";
//        
//        if (settings.getBoolean(clear_settings_tag, false) == false)
//        {
//        	Editor settings_editor = settings.edit();
//        	settings_editor.clear();
//        	settings_editor.putBoolean(clear_settings_tag, true);
//        	settings_editor.commit();
//        }
        
		runProgram();
	}
	
	/**
	 * onResume
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		runProgram();
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
