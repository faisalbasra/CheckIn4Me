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
package com.davidivins.checkin4me.comparators;

import java.util.Comparator;

import com.davidivins.checkin4me.core.ServiceSetting;

/**
* ServiceSettingComparator
* 
* @author david ivins
*/
public class ServiceSettingComparator implements Comparator<ServiceSetting> 
{
	/**
	* compare
	* 
	* @param ServiceSetting location_1
	* @param ServiceSetting location_2
	* @return int
	*/
	public int compare(ServiceSetting setting_1, ServiceSetting setting_2)
	{
		return setting_1.getDisplayName().compareToIgnoreCase(setting_2.getDisplayName());
	}
}