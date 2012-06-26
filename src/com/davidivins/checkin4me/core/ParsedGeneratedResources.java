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

import android.util.Log;

import com.davidivins.checkin4me.interfaces.GeneratedResourcesInterface;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ParsedGeneratedResources implements GeneratedResourcesInterface
{
	private static final String TAG = ParsedGeneratedResources.class.getSimpleName();
	private Map<String, Map<String, Integer>> resources;
	
	/**
	 * ParsedGeneratedResources
	 * 
	 * @param class_name
	 */
	public ParsedGeneratedResources(String class_name) 
	{
		resources = new HashMap<String, Map<String, Integer>>();
		
		try
		{
			// reflection!
			Class<?> r_class = Class.forName(class_name);
			Class<?>[] r_classes = r_class.getClasses();
	            
			for (Class<?> current_class : r_classes)
			{
				Map<String, Integer> current_class_fields = new HashMap<String, Integer>();
				Field[] fields = current_class.getDeclaredFields();
				
				String[] current_class_name = current_class.getName().split("\\.");
				Log.i(TAG, "current_class = " + current_class_name[4]);
				
				// skip styleable
				if ("R$styleable".equals(current_class_name[4])) continue;
				
				for (Field current_field : fields)
				{
					current_class_fields.put(current_field.getName(), current_field.getInt(current_field));
					Log.i(TAG, "current_field = " + current_field.getName() + " = " + current_field.getInt(current_field));
				}
				
				resources.put(current_class_name[4], current_class_fields);
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "Failed to load generated resources file");
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * getAttr
	 * 
	 * @param name
	 * @return int
	 */
	public int getAttr(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$attr").containsKey(name));
			value = resources.get("R$attr").get(name);
			
		return value;
	}
	
	/**
	 * getColor
	 * 
	 * @param name
	 * @return int
	 */
	public int getColor(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$color").containsKey(name));
			value = resources.get("R$color").get(name);
		
		return value;
	}
	
	/**
	 * getDrawable
	 * 
	 * @param name
	 * @return int
	 */
	public int getDrawable(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$drawable").containsKey(name));
			value = resources.get("R$drawable").get(name);
		
		return value;
	}
	
	/**
	 * getId
	 * 
	 * @param name
	 * @return int
	 */
	public int getId(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$id").containsKey(name));
			value = resources.get("R$id").get(name);
		
		return value;
	}
	
	/**
	 * getLayout
	 * 
	 * @param name
	 * @return int
	 */
	public int getLayout(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$layout").containsKey(name));
			value = resources.get("R$layout").get(name);
		
		return value;
	}

	/**
	 * getMenu
	 * 
	 * @param name
	 * @return int
	 */
	public int getMenu(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$menu").containsKey(name));
			value = resources.get("R$menu").get(name);
		
		return value;
	}
	
	/**
	 * getRaw
	 * 
	 * @param name
	 * @return int
	 */
	public int getRaw(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$raw").containsKey(name));
			value = resources.get("R$raw").get(name);
		
		return value;
	}
	
	/**
	 * getString
	 * 
	 * @param name
	 * @return int
	 */
	public int getString(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$string").containsKey(name));
			value = resources.get("R$string").get(name);
		
		return value;
	}
	
	/**
	 * getXml
	 * 
	 * @param name
	 * @return int
	 */
	public int getXml(String name)
	{
		int value = 0x0;
		
		if (resources.get("R$xml").containsKey(name));
			value = resources.get("R$xml").get(name);
		
		return value;
	}
}
