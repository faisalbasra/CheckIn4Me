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
package com.davidivins.checkin4me.gowalla;

import java.io.InputStream;
import java.util.Properties;

import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.interfaces.APIInterface;
import com.davidivins.checkin4me.interfaces.ServiceInterface;
import com.davidivins.checkin4me.oauth.OAuthConnector;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

/**
 * GowallaService
 * 
 * @author david ivins
 */
public class GowallaService implements ServiceInterface
{
	private static final String TAG = "GowallaService";
	private Properties config;
	private OAuthConnector oauth_connector;
	private APIInterface api_adapter;
	private int service_id;
	
	/**
	 * GowallaService
	 * 
	 * @param resources
	 */
	public GowallaService(Resources resources, int service_id)
	{
		this.service_id = service_id;
		config = new Properties();
		
		try 
		{
			InputStream config_file = resources.openRawResource(GeneratedResources.getRaw("gowalla"));
			config.load(config_file);
			
			oauth_connector = new GowallaOAuthConnector(config);
			api_adapter = new GowallaAPI(config, service_id);
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Failed to open config file");
		}
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
		return "Gowalla";
	}
	
	/**
	 * getLogoDrawable
	 * 
	 * @return int
	 */
	public int getLogoDrawable()
	{
		return GeneratedResources.getDrawable("gowalla_logo_resized"); 
	}
	
	/**
	 * getIconDrawable
	 * 
	 * @return int
	 */
	public int getIconDrawable()
	{
		return GeneratedResources.getDrawable("gowalla25x25");
	}

	/**
	 * getOAuthConnector
	 * 
	 * @return OAuthConnector
	 */
	public OAuthConnector getOAuthConnector() 
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
		return settings.contains("gowalla_access_token") && 
			(settings.getString("gowalla_access_token", null) != null);
	}
	
	/**
	 * hasSettings
	 */
	 public boolean hasSettings()
	 {
		 return true;
	 }
}
