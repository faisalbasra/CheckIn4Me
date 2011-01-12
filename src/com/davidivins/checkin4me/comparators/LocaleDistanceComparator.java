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
package com.davidivins.checkin4me.comparators;

import java.util.Comparator;

import android.util.Log;

import com.davidivins.checkin4me.core.Locale;

/**
* LocaleDistanceComparator
* 
* @author david ivins
*/
public class LocaleDistanceComparator implements Comparator<Locale>
{
	static private final String TAG = "LocaleDistanceComparator";
	
	/**
	* compare
	* 
	* @param Locale location_1
	* @param Locale location_2
	* @return int
	*/
	public int compare(Locale location_1, Locale location_2)
	{
		int distance_1 = 0;
		int distance_2 = 0;
		
		try
		{
			distance_1 = Integer.parseInt(location_1.getDistance());
			distance_2 = Integer.parseInt(location_2.getDistance());
		}
		catch(Exception e)
		{
			Log.i(TAG, "Not numbers");
		}
		
		return distance_1 - distance_2;
	}
}