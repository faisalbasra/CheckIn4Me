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
package com.davidivins.checkin4me.monitors;

import android.util.Log;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;

/**
 * AdmobMonitor
 * 
 * @author david
 */
public class AdmobMonitor implements AdListener
{
	private static final String TAG = AdmobMonitor.class.getName();

	/**
	 * onDismissScreen
	 * 
	 * @param ad
	 */
	public void onDismissScreen(Ad ad) 
	{
		Log.i(TAG, "onDismissScreen called");
	}

	/**
	 * onFailedToReceiveAd
	 * 
	 * @param ad
	 * @param error
	 */
	public void onFailedToReceiveAd(Ad ad, ErrorCode error) 
	{
		Log.i(TAG, "onFailedToReceiveAd called");
	}

	/**
	 * onLeaveApplication
	 * 
	 * @param ad
	 */
	public void onLeaveApplication(Ad ad) 
	{
		Log.i(TAG, "onLeaveApplication called");
	}

	/**
	 * onPresentScreen
	 * 
	 * @param ad
	 */
	public void onPresentScreen(Ad ad) 
	{
		Log.i(TAG, "onPresentScreen called");
	}

	/**
	 * onReceiveAd
	 * 
	 * @param ad
	 */
	public void onReceiveAd(Ad ad) 
	{
		Log.i(TAG, "onReceiveAd called");
	}
}