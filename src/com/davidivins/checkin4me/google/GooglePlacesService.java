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
package com.davidivins.checkin4me.google;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import com.davidivins.checkin4me.comparators.ServiceSettingComparator;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.ServiceSetting;
import com.davidivins.checkin4me.interfaces.APIInterface;
import com.davidivins.checkin4me.interfaces.OAuthConnectorInterface;
import com.davidivins.checkin4me.interfaces.ServiceInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

/**
 * GooglePlacesService
 * 
 * @author david ivins
 */
public class GooglePlacesService implements ServiceInterface
{
	private static final String TAG = GooglePlacesService.class.getSimpleName();

	private int service_id;
	private SharedPreferences persistent_storage;
	private Properties config;
	private OAuthConnectorInterface oauth_connector;
	private APIInterface api_adapter;
	private HashMap<String, ServiceSetting> settings;
	
	/**
	 * GooglePlacesService
	 * 
	 * @param resources
	 */
	public GooglePlacesService(int service_id, SharedPreferences persistent_storage, Resources resources)
	{
		// store service_id
		this.service_id = service_id;
		
		// save pointer to persistent storage object
		this.persistent_storage = persistent_storage;
		
		// read configuration file
		config = new Properties();
		
		try 
		{
			InputStream config_file = resources.openRawResource(GeneratedResources.getRaw("google_places"));
			config.load(config_file);
			
			// create oauth connector with current configuration
			oauth_connector =  new GooglePlacesOAuthConnector(config);
			api_adapter = new GooglePlacesAPI(config, service_id);
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Failed to open config file");
		}
		
		// read settings xml
		settings = new HashMap<String, ServiceSetting>();
		
		try 
		{
			InputStream is = resources.openRawResource(GeneratedResources.getRaw("google_places_settings"));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			NodeList settings_nodes = root.getElementsByTagName("setting");

			if (settings != null && settings_nodes.getLength() > 0) 
			{
				for (int i = 0 ; i < settings_nodes.getLength(); i++) 
				{
					Element setting_xml = (Element)settings_nodes.item(i);
					ServiceSetting current_setting = new ServiceSetting(setting_xml, persistent_storage);
					settings.put(current_setting.getPrefName(), current_setting);
				}
			}
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Failed to parse GooglePlaces settings");
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
		return "GooglePlaces";
	}
	
	/**
	 * getLogoDrawable
	 * 
	 * @return int
	 */
	public int getLogoDrawable()
	{
		return GeneratedResources.getDrawable("google_logo_resized");
	}
	
	/**
	 * getIconDrawable
	 * 
	 * @return int
	 */
	public int getIconDrawable()
	{
		return GeneratedResources.getDrawable("google25x25");
	}

	/**
	 * getOAuthConnector
	 * 
	 * @return OAuthConnector
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
	 * @return boolean
	 */
	public boolean connected()
	{
		return persistent_storage.contains("google_places_oauth_token_secret") && 
			(persistent_storage.getString("google_places_oauth_token_secret", null) != null);
	}
	
	/**
	 * hasSettings
	 */
	public boolean hasSettings()
	{
		return (settings.size() > 0) ? true : false;
	}
	 
	/**
	 * getSettingsAsHashMap
	 *
	 * @return HashMap<String, ServiceSetting>
	 */
	public HashMap<String, ServiceSetting> getSettingsAsHashMap()
	{
		return settings;
	}
	 
	/**
	 * getSettingsAsArrayList
	 *
	 * ArrayList<ServiceSetting>
	 */
	public ArrayList<ServiceSetting> getSettingsAsArrayList()
	{
		ArrayList<ServiceSetting> settings_list = new ArrayList<ServiceSetting>();
	 
		for (String key : settings.keySet())
		{
			settings_list.add(settings.get(key));
		}
	 
		Collections.sort(settings_list, new ServiceSettingComparator());
		return settings_list;
	}
}