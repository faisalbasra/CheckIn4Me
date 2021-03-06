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

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.davidivins.checkin4me.core.ServiceSetting;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.interfaces.ServiceInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * SettingsAdapter
 * 
 * @author david
 */
public class SettingsAdapter extends BaseExpandableListAdapter 
{
	//private static final String TAG = SettingsAdapter.class.getSimpleName();
	private Activity activity = null;
	private List<String> groups;
	private List<List<ServiceSetting>> children;
	
	public SettingsAdapter(Activity activity)
	{
		this.activity = activity;

		groups = new ArrayList<String>();
		children = new ArrayList<List<ServiceSetting>>();
		
		List<ServiceInterface> services =
			Services.getInstance(activity).getConnectedServicesWithSettingsAsList();

		for (ServiceInterface service : services)
		{
			groups.add(service.getName() + " Settings");			
			List<ServiceSetting> settings = service.getSettingsAsList();
			children.add(settings);			
		}
	}
	
	/**
	 * getChild
	 * 
	 * @param group_position
	 * @param child_position
	 * @return Object
	 */
	public Object getChild(int group_position, int child_position) 
	{
		return children.get(group_position).get(child_position);
	}

	/**
	 * getChildId
	 * 
	 * @param group_position
	 * @param child_position
	 * @return long
	 */
	public long getChildId(int group_position, int child_position) 
	{
		return child_position;
	}

	/**
	 * getChildrenCount
	 * 
	 * @param group_position
	 * @return int
	 */
	public int getChildrenCount(int group_position) 
	{
		return children.get(group_position).size();
    }

	/**
	 * getGenericView
	 * 
	 * @return TextView
	 */
	public TextView getGenericView() 
	{
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, 64);

		TextView textView = new TextView(activity);
		textView.setLayoutParams(lp);
		
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		// Set the text starting position
		textView.setPadding(45, 0, 0, 0);		
		textView.setTextSize(20);
		
		return textView;
	}

	/**
	 * getChildView
	 * 
	 * @param group_position
	 * @param child_position
	 * @param is_last_child
	 * @param convert_view
	 * @param parent
	 * @return View
	 */
	public View getChildView(int group_position, int child_position, boolean is_last_child, View convert_view, ViewGroup parent) 
	{
		// get the current setting
		ServiceSetting setting = (ServiceSetting)getChild(group_position, child_position);
		
		// create a text layout
		AbsListView.LayoutParams text_layout_params = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout text_layout = new LinearLayout(activity);
		TextView text_view = getGenericView();

		text_view.setText(setting.getDisplayName());
		text_view.setTextColor(Color.BLACK);
		text_layout.setLayoutParams(text_layout_params);
		text_layout.setOrientation(LinearLayout.HORIZONTAL);
		text_layout.addView(text_view);

		// create checkbox
		CheckBox check_box = new CheckBox(activity);
		check_box.setChecked(setting.getPrefValue());
		check_box.setOnClickListener(setting);
		
		// create a check box layout
		AbsListView.LayoutParams check_box_layout_params = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout check_box_layout = new LinearLayout(activity);
		
		check_box_layout.setLayoutParams(check_box_layout_params);
		check_box_layout.setGravity(Gravity.RIGHT);
		check_box_layout.setPadding(0, 0, 5, 0);
		check_box_layout.addView(check_box);
		
		// create row and add text and check box layouts to it
		AbsListView.LayoutParams main_layout_params = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		LinearLayout main_layout = new LinearLayout(activity);
		
		// add to main layout
		main_layout.setLayoutParams(main_layout_params);
		main_layout.setOrientation(LinearLayout.HORIZONTAL);
		main_layout.addView(text_layout);
		main_layout.setGravity(Gravity.CENTER);
		main_layout.addView(check_box_layout);
		
		return main_layout;
	}

	/**
	 * getGroup
	 * 
	 * @param group_position
	 * @return Object
	 */
	public Object getGroup(int group_position) 
	{
		return groups.get(group_position);
	}

	/**
	 * getGroupCount
	 * 
	 * @return int
	 */
	public int getGroupCount() 
	{
		return groups.size();
	}

	/**
	 * getGroupId
	 * 
	 * @param group_position
	 * @return long
	 */
	public long getGroupId(int group_position) 
	{
		return group_position;
	}

	/**
	 * getGroupView
	 * 
	 * @param group_position
	 * @param is_expanded
	 * @param convert_view
	 * @param parent
	 * @return View
	 */
	public View getGroupView(int group_position, boolean is_expanded, View convert_view, ViewGroup parent) 
	{
		TextView text_view = getGenericView();
		text_view.setText(getGroup(group_position).toString());
		text_view.setTextColor(Color.BLACK);
		text_view.setPadding(55, 0, 0, 0);
		
		LinearLayout layout = new LinearLayout(activity);
		layout.addView(text_view);
		return layout;
	}

	/**
	 * isChildSelectable
	 * 
	 * @param group_position
	 * @param child_position
	 * @return boolean
	 */
	public boolean isChildSelectable(int group_position, int child_position) 
	{
		return true;
	}

	/**
	 * hasStableIds
	 * 
	 * @return boolean
	 */
	public boolean hasStableIds() 
	{
		return true;
	}
}