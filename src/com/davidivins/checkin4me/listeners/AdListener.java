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
package com.davidivins.checkin4me.listeners;

import android.util.Log;

import com.admob.android.ads.AdView;
import com.admob.android.ads.SimpleAdListener;

public class AdListener extends SimpleAdListener
{
	private static final String TAG = "AdListener";
	
	@Override
	public void onFailedToReceiveAd(AdView ad) 
	{
		Log.w (TAG, "failed to receive ad");
		super.onFailedToReceiveAd(ad);
	}

	@Override
	public void onFailedToReceiveRefreshedAd(AdView ad) 
	{
		Log.w (TAG, "failed to receive refreshed ad");
		super.onFailedToReceiveRefreshedAd(ad);
	}

	@Override
	public void onReceiveAd(AdView ad) 
	{
		Log.w (TAG, "receive ad");
		super.onReceiveAd(ad);
	}

	@Override
	public void onReceiveRefreshedAd(AdView ad) 
	{
		Log.w (TAG, "receive refreshed ad");
		super.onReceiveRefreshedAd(ad);
	}
}