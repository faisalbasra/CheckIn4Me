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
package com.davidivins.checkin4me.adapters;

import java.util.ArrayList;
import java.util.Set;

import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Locale;
import com.davidivins.checkin4me.core.Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * LocationAdapter
 * 
 * @author david ivins
 */
public class LocaleAdapter extends ArrayAdapter<Locale>
{
	//private static final String TAG = "LocaleAdapter";
	private Context context;
	private int row_resource_id;
	private ArrayList<Locale> items;

	/**
	 * LocationAdapter
	 * 
	 * @param activity
	 * @param context
	 * @param row_resource_id
	 * @param items
	 */
	public LocaleAdapter(Context context, int row_resource_id, ArrayList<Locale> items) 
	{
		super(context, row_resource_id, items);
		this.context = context;
		this.row_resource_id = row_resource_id;
		this.items = items;
    }

	/**
	 * getView
	 * 
	 * @param int position
	 * @param View convert_view
	 * @param ViewGroup parent
	 * @return View
	 */
	@Override
	public View getView(int position, View convert_view, ViewGroup parent) 
	{
		View view = convert_view;
		
		if (view == null)
		{
			LayoutInflater layout_inflater = 
				(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout_inflater.inflate(row_resource_id, null);
		}
		
		Locale location = items.get(position);
		
		// items set blocks list from getting messed up on scrolling
		if (location != null) 
		{
			LinearLayout row = (LinearLayout)view.findViewById(GeneratedResources.getId("nearby_place"));
			row.removeAllViews(); // <-- this thing is pretty effing important...wasted hours on this...
			
			LinearLayout text_line = new LinearLayout(parent.getContext());
			LinearLayout icon_line = new LinearLayout(parent.getContext());
			text_line.setPadding(5, 5, 5, 5);
			icon_line.setPadding(5, 5, 5, 5);
			
			TextView tv = new TextView(parent.getContext());
			tv.setText(location.getName());
			tv.setTextColor(Color.BLACK);
			text_line.addView(tv);
			
			Set<Integer> services_with_location = location.getServiceIdToLocationIdMap().keySet();
			
			ImageView icon = null;
			for (int service_id : services_with_location)
			{
				icon = new ImageView(parent.getContext());
				icon.setImageResource(
						Services.getInstance((Activity)context).getServiceById(service_id).getIconDrawable());
				icon.setPadding(0, 0, 5, 0);
				icon_line.addView(icon);
			}

			row.addView(text_line);
			row.addView(icon_line);
		}
		
		return view;
    }
}
