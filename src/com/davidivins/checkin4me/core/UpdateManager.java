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
package com.davidivins.checkin4me.core;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class UpdateManager 
{
	/**
	 * constructor
	 */
	private UpdateManager() { }
	
	/**
	 * performCleanInstallDefaultsIfNecessary
	 *
	 * @param activity
	 */
	public static void performCleanInstallDefaultsIfNecessary(Activity activity)
	{
		if (null != activity)
		{
	        SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(activity);
	        String updated_tag = "CLEAN_INSTALL_DEFAULTS_PERFORMED";
	        
	        // if update has already been performed, skip it.
	        if (persistent_storage.getBoolean(updated_tag, false) == false)
	        {
	        	Editor persistent_storage_editor = persistent_storage.edit();
				
	        	// make updates
	        	persistent_storage_editor.putBoolean("facebook_check_in_default", true);
	        	persistent_storage_editor.putBoolean("foursquare_check_in_default", true);
	        	persistent_storage_editor.putBoolean("gowalla_check_in_default", true);
	        	
	        	// set updated flag and store updates
	        	persistent_storage_editor.putBoolean(updated_tag, true);
	        	persistent_storage_editor.commit();
	        }
		}
	}
	
	/**
	 * performUpdateIfNecessary
	 *
	 * @param activity
	 */
	public static void performUpdateIfNecessary(Activity activity)
	{
		if (null != activity)
		{
//	        SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(activity);
//	        String updated_tag = "VERSION_2.0_UPDATE_PERFORMED";
//	        
//	        // if update has already been performed, skip it.
//	        if (persistent_storage.getBoolean(updated_tag, false) == false)
//	        {
//	        	Editor persistent_storage_editor = persistent_storage.edit();
//				
//	        	// make updates
//	        	persistent_storage_editor.putBoolean("facebook_check_in_default", true);
//	        	persistent_storage_editor.putBoolean("foursquare_check_in_default", true);
//	        	persistent_storage_editor.putBoolean("gowalla_check_in_default", true);
//	        	
//	        	// set updated flag and store updates
//	        	persistent_storage_editor.putBoolean(updated_tag, true);
//	        	persistent_storage_editor.commit();
//	        }
		}
	}
}