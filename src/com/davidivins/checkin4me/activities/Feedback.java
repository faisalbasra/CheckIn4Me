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

import com.davidivins.checkin4me.core.GeneratedResources;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Feedback
 * 
 * @author david
 */
public class Feedback extends Activity 
{
	/**
	 * onCreate
	 * 
	 * @param saved_instance_state
	 */
	@Override
	public void onCreate(Bundle saved_instance_state) 
	{
		super.onCreate(saved_instance_state);
		GeneratedResources.generate(this);
		setContentView(GeneratedResources.getLayout("feedback"));

		// display getSatisfaction page in web view
	    WebView view = (WebView)findViewById(GeneratedResources.getId("feedback"));
	    view.getSettings().setJavaScriptEnabled(true);
	    view.loadUrl("https://getsatisfaction.com/checkin4me/");
	}
}