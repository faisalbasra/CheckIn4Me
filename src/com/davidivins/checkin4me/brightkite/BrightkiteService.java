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
package com.davidivins.checkin4me.brightkite;

import java.io.InputStream;
import java.util.Properties;

import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.interfaces.APIInterface;
import com.davidivins.checkin4me.interfaces.ServiceInterface;
import com.davidivins.checkin4me.oauth.OAuthConnectorInterface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

/**
 * BrightkiteService
 * 
 * @author david ivins
 */
public class BrightkiteService implements ServiceInterface
{
	private static final String TAG = "BrightkiteService";
	private Properties config;
	private int service_id;
	private OAuthConnectorInterface oauth_connector;
	private APIInterface api_adapter;
	
	/**
	 * BrightkiteService
	 * 
	 * @param resources
	 */
	public BrightkiteService(Resources resources, int service_id)
	{
		this.service_id = service_id;
		config = new Properties();
		
		try 
		{
			InputStream config_file = resources.openRawResource(GeneratedResources.getRaw("brightkite"));
			config.load(config_file);
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Failed to open config file");
		}
		
		oauth_connector = new BrightkiteOAuthConnector(config);
		api_adapter = new BrightkiteAPI(config, service_id);
	}
	
	/**
	 * getId
	 * 
	 * @return int id
	 */
	public int getId()
	{
		return service_id;
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getName() 
	{
		return "Brightkite";
	}
	
	/**
	 * getLogoDrawable
	 * 
	 * @return int
	 */
	public int getLogoDrawable()
	{
		return GeneratedResources.getDrawable("brightkite_logo_resized"); 
	}
	
	/**
	 * getIconDrawable
	 * 
	 * @return int
	 */
	public int getIconDrawable()
	{
		return GeneratedResources.getDrawable("brightkite25x25");
	}

	/**
	 * getOAuthConnector
	 * 
	 * @return null
	 */
	public OAuthConnectorInterface getOAuthConnector() 
	{
		return oauth_connector;
	}
	
	/**
	 * getAPIInterface
	 * 
	 * @return APIInterface
	 */
	public APIInterface getAPIInterface()
	{
		return api_adapter;
	}
	
	/**
	 * connected
	 * 
	 * @param SharedPreferences 
	 * @return boolean
	 */
	public boolean connected(SharedPreferences settings)
	{
		return settings.contains("brightkite_oauth_token") && 
			settings.contains("brightkite_oauth_token_secret");
	}
}