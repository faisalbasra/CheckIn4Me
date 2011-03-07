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

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * SettingsAdapter
 * 
 * @author david
 */
public class SettingsAdapter extends BaseExpandableListAdapter 
{
	//private static final String TAG = "SettingsAdapter";
	private Activity activity = null;
	
	private String[] groups = 
	{ 
		"Facebook Settings", 
		"Foursquare Settings", 
		"Gowalla Settings", 
		"CheckIn4Me" 
	};
	
	private String[][] children = 
	{
		{ "X", "Y", "Z" },
		{ "Post to Facebook", "Post to Twitter" },
		{ "Post to Facebook", "Post to Twitter" },
		{ "Visit Feedback Site" }
	};

	/**
	 * setActivity
	 * 
	 * @param activity
	 */
	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	/**
	 * getChild
	 * 
	 * @param int group_position
	 * @param int child_position
	 * @return Object
	 */
	public Object getChild(int group_position, int child_position) 
	{
		return children[group_position][child_position];
	}

	/**
	 * getChildId
	 * 
	 * @param int group_position
	 * @param int child_position
	 * @return long
	 */
	public long getChildId(int group_position, int child_position) 
	{
		return child_position;
	}

	/**
	 * getChildrenCount
	 * 
	 * @param int group_position
	 * @return int
	 */
	public int getChildrenCount(int group_position) 
	{
		return children[group_position].length;
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
			ViewGroup.LayoutParams.MATCH_PARENT, 64);

		TextView textView = new TextView(activity);
		textView.setLayoutParams(lp);
		
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		// Set the text starting position
		textView.setPadding(45, 0, 0, 0);
		
		return textView;
	}

	/**
	 * getChildView
	 * 
	 * @param int group_position
	 * @param int child_position
	 * @param boolean is_last_child
	 * @param View convert_view
	 * @param ViewGroup parent
	 * @return View
	 */
	public View getChildView(int group_position, int child_position, boolean is_last_child, View convert_view, ViewGroup parent) 
	{
		// create a text layout
		AbsListView.LayoutParams text_layout_params = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout text_layout = new LinearLayout(activity);
		TextView text_view = getGenericView();
		
		text_view.setText(getChild(group_position, child_position).toString());
		text_view.setTextColor(Color.BLACK);
		text_layout.setLayoutParams(text_layout_params);
		text_layout.setOrientation(LinearLayout.HORIZONTAL);
		text_layout.addView(text_view);

		// create a check box layout
		AbsListView.LayoutParams check_box_layout_params = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		CheckBox check_box = new CheckBox(activity);
		LinearLayout check_box_layout = new LinearLayout(activity);
		
		check_box_layout.setLayoutParams(check_box_layout_params);
		check_box_layout.setGravity(Gravity.RIGHT);
		check_box_layout.setPadding(0, 0, 5, 0);
		check_box_layout.addView(check_box);
		
		// create row and add text and check box layouts to it
		AbsListView.LayoutParams main_layout_params = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		LinearLayout main_layout = new LinearLayout(activity);
		
		main_layout.setLayoutParams(main_layout_params);
		main_layout.setOrientation(LinearLayout.HORIZONTAL);
		main_layout.addView(text_layout);
		
		if (3 != group_position)
		{
			main_layout.setGravity(Gravity.CENTER);
			main_layout.addView(check_box_layout);
		}
		else
		{
			main_layout.setGravity(Gravity.CENTER_VERTICAL); 
		}
		
		return main_layout;
	}

	/**
	 * getGroup
	 * 
	 * @param int group_position
	 * @return Object
	 */
	public Object getGroup(int group_position) 
	{
		return groups[group_position];
	}

	/**
	 * getGroupCount
	 * 
	 * @return int
	 */
	public int getGroupCount() 
	{
		return groups.length;
	}

	/**
	 * getGroupId
	 * 
	 * @param int group_position
	 * @return long
	 */
	public long getGroupId(int group_position) 
	{
		return group_position;
	}

	/**
	 * getGroupView
	 * 
	 * @param int group_position
	 * @param boolean is_expanded
	 * @param View convert_view
	 * @param ViewGroup parent
	 * @return View
	 */
	public View getGroupView(int group_position, boolean is_expanded, View convert_view, ViewGroup parent) 
	{
		TextView textView = getGenericView();
		textView.setText(getGroup(group_position).toString());
		textView.setTextColor(Color.BLACK);
		
		LinearLayout layout = new LinearLayout(activity);
		layout.addView(textView);
		return layout;
	}

	/**
	 * isChildSelectable
	 * 
	 * @param int group_position
	 * @param int child_position
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