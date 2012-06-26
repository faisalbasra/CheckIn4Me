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
package com.davidivins.checkin4me.util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;

/**
 * LocationOverlay
 * 
 * @author david ivins
 */
public class LocationOverlay extends ItemizedOverlay<OverlayItem>
{
	private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
	private Context context;
	
	/**
	 * LocationOverlay
	 * 
	 * @param defaultMarker
	 * @param context
	 */
	public LocationOverlay(Drawable defaultMarker, Context context) 
	{
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}
	
	/**
	 * addOverlay
	 * 
	 * @param overlay
	 */
	public void addOverlay(OverlayItem overlay) 
	{
		overlays.add(overlay);
		populate();
	}
	
	/**
	 * onTap
	 * 
	 * @param index
	 * @return boolean
	 */
	@Override
	protected boolean onTap(int index) 
	{
		OverlayItem item = overlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
	
	/**
	 * createItem
	 * 
	 * @param i
	 * @return OverlayItem
	 */
	@Override
	protected OverlayItem createItem(int i) 
	{
		return overlays.get(i);
	}

	/**
	 * size
	 * 
	 * @return int
	 */
	@Override
	public int size() 
	{
		return overlays.size();
	}
}
