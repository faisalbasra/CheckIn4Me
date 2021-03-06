//*****************************************************************************
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
package com.davidivins.checkin4me.interfaces;

import android.content.SharedPreferences;
import com.davidivins.checkin4me.core.Locale;

import java.util.List;

/**
 * APIInterface
 * 
 * @author david ivins
 */
public interface APIInterface 
{
	abstract public Runnable getLocationsThread(String query, String longitude, String latitude, SharedPreferences persistent_storage);
	abstract public List<Locale> getLatestLocations();
	abstract public Runnable getCheckInThread(Locale location, String message, SharedPreferences persistent_storage);
	abstract public boolean getLatestCheckInStatus();
}
