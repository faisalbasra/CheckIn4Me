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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.davidivins.checkin4me.core.GeneratedResources;

import java.util.List;

/**
 * ServiceConnectionAdapter
 * 
 * @author david ivins
 */
public class ServiceConnectionAdapter extends ArrayAdapter<Integer>
{
	//private static final String TAG = LocaleAdapter.class.getSimpleName();
	private Context context;
	private int row_resource_id;
	private List<Integer> logos;

	/**
	 * ServiceConnectionAdapter
	 * 
	 * @param context
	 * @param row_resource_id
	 * @param logos
	 */
	public ServiceConnectionAdapter(Context context, int row_resource_id, List<Integer> logos)
	{
		super(context, row_resource_id, logos);
		this.context = context;
		this.row_resource_id = row_resource_id;
		this.logos = logos;
    }

	/**
	 * getView
	 * 
	 * @param position
	 * @param convert_view
	 * @param parent
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
		
		Integer logo = logos.get(position);
		
		// items set blocks list from getting messed up on scrolling
		if (logo != null) 
		{
			LinearLayout row = (LinearLayout)view.findViewById(GeneratedResources.getId("service_connection_logo"));
			row.removeAllViews(); // <-- this thing is pretty effing important...wasted hours on this...
			
			ImageView icon = new ImageView(parent.getContext());
			icon.setImageResource(logo.intValue());
			icon.setPadding(10, 10, 10, 10);
			row.addView(icon);
		}
		
		return view;
    }
}