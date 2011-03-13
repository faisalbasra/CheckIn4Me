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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * ServiceSetting
 * 
 * @author david
 */
public class ServiceSetting 
{
	private static final String TAG = "ServiceSetting";
	private SharedPreferences persistent_storage;
	private Editor persistent_storage_editor;
	private String display_name;
	private String pref_name;
	
	/**
	 * constructor
	 */
	public ServiceSetting(Element xml, SharedPreferences persistent_storage)
	{
		NodeList display_names = xml.getElementsByTagName("display_name");
		NodeList pref_names = xml.getElementsByTagName("pref_name");

		Element display_name = (Element)display_names.item(0);
		Element pref_name = (Element)pref_names.item(0);

		this.display_name = display_name.getFirstChild().getNodeValue();
		this.pref_name = pref_name.getFirstChild().getNodeValue();
		
		this.persistent_storage = persistent_storage;
		this.persistent_storage_editor = persistent_storage.edit();
		
		Log.i(TAG, "display_name = " + this.display_name);
		Log.i(TAG, "pref_name = " + this.pref_name);
	}
	
	/**
	 * getDisplayName
	 * 
	 * @return String
	 */
	public String getDisplayName()
	{
		return display_name;
	}
	
	/**
	 * getPrefName
	 * 
	 * @return String
	 */
	public String getPrefName()
	{
		return pref_name;
	}
	
	/**
	 * setPrefValue
	 * 
	 * @param pref_value
	 */
	public void setPrefValue(boolean pref_value)
	{
		persistent_storage_editor.putBoolean(pref_name, pref_value);
		persistent_storage_editor.commit();
	}
	
	/**
	 * getPrefValue
	 * 
	 * @return boolean
	 */
	public boolean getPrefValue()
	{
		return persistent_storage.getBoolean(pref_name, false);
	}
}
