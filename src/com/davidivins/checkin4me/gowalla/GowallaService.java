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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.davidivins.checkin4me.comparators.ServiceSettingComparator;
import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.ServiceSetting;
import com.davidivins.checkin4me.interfaces.APIInterface;
import com.davidivins.checkin4me.interfaces.OAuthConnectorInterface;
import com.davidivins.checkin4me.interfaces.ServiceInterface;

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
	private static final String TAG = GowallaService.class.getName();
	private int service_id;
	private SharedPreferences persistent_storage;
	private Properties config;
	private OAuthConnectorInterface oauth_connector;
	private APIInterface api_adapter;
	private HashMap<String, ServiceSetting> settings;
	
	/**
	 * GowallaService
	 * 
	 * @param resources
	 */
	public GowallaService(int service_id, SharedPreferences persistent_storage, Resources resources)
	{
		// store service id
		this.service_id = service_id;
		
		// save pointer to persistent storage object
		this.persistent_storage = persistent_storage;
		
		// read the configuration file
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
		
		// read settings xml
		settings = new HashMap<String, ServiceSetting>();
		
		try 
		{
			InputStream is = resources.openRawResource(GeneratedResources.getRaw("gowalla_settings"));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(is);
			Element root = dom.getDocumentElement();
			NodeList settings_nodes = root.getElementsByTagName("setting");
			
			if (settings_nodes != null && settings_nodes.getLength() > 0) 
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
			Log.e(TAG, "Failed to parse Gowalla settings");
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
	public boolean connected()
	{
		return persistent_storage.contains("gowalla_access_token") && 
			(persistent_storage.getString("gowalla_access_token", null) != null);
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
	 */
	public HashMap<String, ServiceSetting> getSettingsAsHashMap()
	{
		return settings;
	}
	 
	/**
	 * getSettingsAsArrayList
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
