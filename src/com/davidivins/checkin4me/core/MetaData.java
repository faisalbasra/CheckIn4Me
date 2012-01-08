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
package com.davidivins.checkin4me.core;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * MetaData
 * 
 * @author david
 */
public class MetaData 
{
	private static final String TAG    = MetaData.class.getSimpleName();
	private static Bundle meta_data    = null;
	private static boolean good_bundle = false;
	
	/**
	 * MetaData
	 */
	private MetaData() { }
	
	/**
	 * initialize
	 * 
	 * @param activity
	 */
	public static Bundle getInstance(Activity activity)
	{
		if (null == meta_data || !good_bundle)
		{
			try
			{
				ApplicationInfo app_info = activity.getPackageManager().getApplicationInfo(
					activity.getPackageName(), PackageManager.GET_META_DATA);
				
				meta_data   = app_info.metaData;
				good_bundle = true;
			}
			catch(Exception e)
			{
				meta_data   = new Bundle();
				good_bundle = false;
				
				Log.e(TAG, "Failed to get app info. Returning empty bundle.");
			}
		}
		
		return meta_data;
	}
}
