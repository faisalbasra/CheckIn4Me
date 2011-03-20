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

import com.davidivins.checkin4me.interfaces.GeneratedResourcesInterface;

import android.app.Activity;
import android.util.Log;

public class GeneratedResources
{
	private static final String TAG = "GeneratedResources";
	private static GeneratedResourcesInterface generated_resources = null;
	private static boolean good_meta_data = false;
	
	/**
	 * GeneratedResources
	 */
	private GeneratedResources() { }
	
	/**
	 * generate
	 * 
	 * @param activity
	 */
	public static void generate(Activity activity)
	{
		if (null == generated_resources || !good_meta_data)
		{
			String version = MetaData.getInstance(activity).getString("VERSION");

			// if null, assume pro. don't screw up paying customers
			if (null == version)
			{
				version = "professional";
				good_meta_data = false;
			}
			else
				good_meta_data = true;

			// get generated resources for app version
			String class_name = "com.davidivins.checkin4me." + version + ".R";		
			generated_resources = new ParsedGeneratedResources(class_name);

			Log.i(TAG, "resources generated!");
		}
	}
	
	/**
	 * areNotGenerated
	 * 
	 * @return boolean
	 */
	public static boolean areNotGenerated()
	{
		return (null == generated_resources) ? true : false;
	}
	
	/**
	 * getAttr
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getAttr(String name)
	{
		return generated_resources.getAttr(name);
	}
	
	/**
	 * getColor
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getColor(String name)
	{
		return generated_resources.getColor(name);
	}
	
	/**
	 * getDrawable
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getDrawable(String name)
	{
		return generated_resources.getDrawable(name);
	}
	
	/**
	 * getId
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getId(String name)
	{
		return generated_resources.getId(name);
	}
	
	/**
	 * getLayout
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getLayout(String name)
	{
		return generated_resources.getLayout(name);
	}

	/**
	 * getMenu
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getMenu(String name)
	{
		return generated_resources.getMenu(name);
	}
	
	/**
	 * getRaw
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getRaw(String name)
	{
		return generated_resources.getRaw(name);
	}
	
	/**
	 * getString
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getString(String name)
	{
		return generated_resources.getString(name);
	}
	
	/**
	 * getXml
	 * 
	 * @param name
	 * @return int
	 */
	public static final int getXml(String name)
	{
		return generated_resources.getXml(name);
	}
}
