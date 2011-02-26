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
package com.davidivins.checkin4me.adapters;

import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * SettingsAdapter
 * 
 * @author david ivins
 */
public class SettingsAdapter extends ArrayAdapter<String> implements OnCheckedChangeListener
{
	//private static final String TAG = "ServiceCheckListAdapter";
	private Context context;
	private int row_resource_id;
	private String[] items;
	
	/**
	 * SettingsAdapter
	 * 
	 * @param activity
	 * @param context
	 * @param row_resource_id
	 * @param items
	 */
	public SettingsAdapter(Context context, int row_resource_id, String[] items) 
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
		String setting = items[position];
		
		if (view == null)
		{
			LayoutInflater layout_inflater = 
				(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layout_inflater.inflate(row_resource_id, null);
		}
		
		if (setting != null) 
		{
			LinearLayout settings_row = (LinearLayout)view.findViewById(GeneratedResources.getId("settings_row"));			
			settings_row.removeAllViews();

			ImageView icon = new ImageView(parent.getContext());
			icon.setImageResource(
				Services.getInstance((Activity)context).getServiceById(1).getIconDrawable());
			icon.setPadding(15, 15, 15, 15);
			
			TextView setting_title = new TextView(parent.getContext());
			setting_title.setText(setting);
			setting_title.setTextColor(Color.BLACK);
			setting_title.setPadding(0, 15, 15, 15);
			setting_title.setTextSize(20);

			settings_row.addView(icon);
			settings_row.addView(setting_title);
		}
			
		return view;
    }

	/**
	 * onCheckedChanged
	 * 
	 * @param CompountButton button_view
	 * @param boolean is_checked
	 */
	public void onCheckedChanged(CompoundButton button_view, boolean is_checked) 
	{
		//services_checked.put(button_view.getId(), is_checked);
	}
	
	/**
	 * getServicesChecked
	 * 
	 * @return HashMap<Integer, Boolean> services_checked
	 */
//	public HashMap<Integer, Boolean> getServicesChecked()
//	{
//		//return services_checked;
//	}
}