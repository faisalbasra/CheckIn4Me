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
package com.davidivins.checkin4me.util;

import com.davidivins.checkin4me.listeners.CleanableProgressDialogListener;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * CleanableProgressDialog
 * 
 * @author david
 */
public class CleanableProgressDialog extends ProgressDialog
{
	CleanableProgressDialogListener listener;
	
	/**
	 * CleanableProgressDialog
	 * @param listener
	 * @param activity
	 */
	public CleanableProgressDialog(CleanableProgressDialogListener listener, Activity activity, String title, String msg, boolean indeterminate)
	{
		super(activity);
		this.listener = listener;
		setTitle(title);
		setMessage(msg);
		setIndeterminate(indeterminate);
	}
	
	/**
	 * onSearchRequested
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onSearchRequested()
	{
		boolean result = super.onSearchRequested();
		listener.onDialogInterruptedBySearchButton();
		return result;
	}
	
	/**
	 * onBackPressed
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		listener.onDialogInterruptedByBackButton();
	}
}
