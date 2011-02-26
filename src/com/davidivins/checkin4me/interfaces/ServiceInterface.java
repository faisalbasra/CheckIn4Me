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
package com.davidivins.checkin4me.interfaces;

import com.davidivins.checkin4me.oauth.OAuthConnector;

import android.content.SharedPreferences;

/**
 * ServiceInterface
 * 
 * @author david ivins
 */
public interface ServiceInterface
{
	abstract public int getId();
	abstract public String getName();
	abstract public int getLogoDrawable();
	abstract public int getIconDrawable();
	abstract public OAuthConnector getOAuthConnector();
	abstract public APIInterface getAPIInterface();
	abstract public boolean connected(SharedPreferences settings);
	abstract public boolean hasSettings();
}
