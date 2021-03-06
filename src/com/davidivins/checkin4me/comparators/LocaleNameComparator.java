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
package com.davidivins.checkin4me.comparators;

import com.davidivins.checkin4me.core.Locale;

import java.util.Comparator;

/**
 * LocaleNameComparator
 * 
 * @author david ivins
 */
public class LocaleNameComparator implements Comparator<Locale> 
{
	/**
	 * compare
	 * 
	 * @param location_1
	 * @param location_2
	 * @return int
	 */
	public int compare(Locale location_1, Locale location_2)
	{
		return location_1.getName().compareToIgnoreCase(location_2.getName());
	}
}