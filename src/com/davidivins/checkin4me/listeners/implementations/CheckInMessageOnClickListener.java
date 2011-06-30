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
package com.davidivins.checkin4me.listeners.implementations;

import com.davidivins.checkin4me.activities.LocationDetails;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
	
/**
 * CheckInMessageOnClickListener
 * 
 * processes button clicks when the user is adding a message to a check-in.
 */
public class CheckInMessageOnClickListener implements DialogInterface.OnClickListener
{
	private static final String TAG = CheckInMessageOnClickListener.class.getName();
	private LocationDetails activity;
	private EditText input;
	
	/**
	 * CheckInMessageOnClickListener
	 * 
	 * @param input
	 */
	public CheckInMessageOnClickListener(LocationDetails activity, EditText input)
	{
		this.activity = activity;
		this.input    = input;
	}
	
	/**
	 * onClick
	 * 
	 * @param DialogInterface dialog
	 * @param int button
	 */
	public void onClick(DialogInterface dialog, int button) 
	{
		// if check-in button was selected, get message and check-in
		if (DialogInterface.BUTTON_POSITIVE == button)
		{
			String message = input.getText().toString().trim();
			Log.i(TAG,"message = *" + message + "*");
			
			activity.checkIn(message);
		}
		else
		{
			// cancel dialog if user decides to cancel
			dialog.cancel();
		}
	}
}