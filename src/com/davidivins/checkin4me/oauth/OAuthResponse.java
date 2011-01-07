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
package com.davidivins.checkin4me.oauth;

import java.util.TreeMap;

import com.davidivins.checkin4me.util.Response;

/**
 * OAuthResponse
 * 
 * @author david ivins
 */
public class OAuthResponse extends Response
{
	/**
	 * OAuthResponse
	 */
	public OAuthResponse()
	{
		super();
	}
	
	/**
	 * OAuthResponse
	 * 
	 * @param success_status
	 * @param response_string
	 */
	public OAuthResponse(boolean success_status, String response_string)
	{
		super(success_status, response_string);
	}
	
	/**
	 * getQueryParameters
	 * 
	 * @return TreeMap<String, String>
	 */
	public TreeMap<String, String> getQueryParameters()
	{
		TreeMap<String, String> query_parameters = new TreeMap<String, String>();
		
		// break out response query parameters and store in map
		String[] parameters = response_string.split("&");
		
		for (String parameter : parameters)
		{
			String[] key_value = parameter.split("=");
			
			if (key_value.length == 2)
				query_parameters.put(key_value[0], key_value[1]);
		}
		
		return query_parameters;
	}
}
