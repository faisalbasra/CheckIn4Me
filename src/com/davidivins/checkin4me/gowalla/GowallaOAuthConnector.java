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
package com.davidivins.checkin4me.gowalla;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;
import com.davidivins.checkin4me.interfaces.OAuthConnectorInterface;
import com.davidivins.checkin4me.oauth.OAuth2Request;
import com.davidivins.checkin4me.oauth.OAuthResponse;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Properties;

/**
 * GowallaOAuthConnector
 * 
 * @author david ivins
 */
public class GowallaOAuthConnector implements OAuthConnectorInterface
{
	private static final String TAG      = GowallaOAuthConnector.class.getSimpleName();
	private static final String ENCODING = "ISO-8859-1";

	private Properties config;
	private String oauth_redirect_uri;
	
	/**
	 * GowallaOAuthConnector
	 * 
	 * @param config
	 */
	GowallaOAuthConnector(Properties config) 
	{
		this.config = config;
		
		try
		{
			// must be encoded twice :(
			oauth_redirect_uri = URLEncoder.encode(config.getProperty("oauth_redirect_uri"), ENCODING);
		} 
		catch(Exception e) 
		{ 
			Log.e(TAG, ENCODING + " isn't a valid encoding!?");
		}
	}

	/**
	 * beginHandshake
	 * 
	 * @return OAuthResponse
	 */
	public OAuthResponse beginHandshake() 
	{
		return new OAuthResponse(true, "");
	}

	/**
	 * isSuccessfulInitialResponse
	 * 
	 * @param response
	 * @return boolean
	 */
	public boolean isSuccessfulInitialResponse(OAuthResponse response) 
	{
		return true;
	}

	/**
	 * storeNecessaryInitialResponseData
	 * 
	 * @param persistent_storage_editor
	 * @param response
	 */
	public void storeNecessaryInitialResponseData(Editor persistent_storage_editor, OAuthResponse response) { }

	/**
	 * generateAuthorizationURL
	 *
	 * @param persistent_storage
	 * @return String
	 */
	public String generateAuthorizationURL(SharedPreferences persistent_storage) 
	{
		String url = config.getProperty("oauth_host") + config.getProperty("oauth_new_token_endpoint")
			+ "?client_id=" + config.getProperty("oauth_client_id")
			+ "&redirect_uri=" + oauth_redirect_uri 
			+ "&scope=" + config.getProperty("oauth_api_scope");
		
		Log.i(TAG, "authorization url = " + url);
		return url;
	}

	/**
	 * isSuccessfulAuthorizationResponse
	 * 
	 * @param response
	 * @return boolean
	 */
	public boolean isSuccessfulAuthorizationResponse(Uri response) 
	{
		boolean is_successful = false;
		
		if ((null != response) && (response.getQueryParameter("code") != null))
			is_successful = true;
		
		Log.i(TAG, "isSuccessfulAuthorizationResponse = " + is_successful);
		return is_successful;
	}
	
	/**
	 * storeNecessaryAuthorizationResponseData
	 * 
	 * @param persistent_storage_editor
	 * @param response
	 */
	public void storeNecessaryAuthorizationResponseData(Editor persistent_storage_editor, Uri response)
	{
		Log.i(TAG, "code = " + response.getQueryParameter("code"));
		persistent_storage_editor.putString("gowalla_code", response.getQueryParameter("code"));
		persistent_storage_editor.commit();
	}

	/**
	 * completeHandshake
	 * 
	 * @param persistent_storage
	 * @param previous_response
	 * @return OAuthResponse
	 */
	public OAuthResponse completeHandshake(SharedPreferences persistent_storage, Uri previous_response) 
	{
		OAuthResponse response = new OAuthResponse();
		Log.i(TAG, "code in persistent_storage = " + persistent_storage.getString("gowalla_code", "-1"));
		
		if (persistent_storage.getString("gowalla_code", "-1") != "-1")
		{
			OAuth2Request request = new OAuth2Request(
					config.getProperty("oauth_http_method"), config.getProperty("oauth_host"), 
					config.getProperty("oauth_access_token_endpoint"));
			
			request.addQueryParameter("grant_type", "authorization_code");
			request.addQueryParameter("client_id", config.getProperty("oauth_client_id"));
			request.addQueryParameter("client_secret", config.getProperty("oauth_client_secret"));
			request.addQueryParameterAndEncode("code", persistent_storage.getString("gowalla_code", "CODE_HERE"));
			request.addQueryParameter("redirect_uri", oauth_redirect_uri);
			request.addQueryParameter("scope", config.getProperty("oauth_api_scope"));
			
			response = (OAuthResponse)request.execute();
		}
		else
		{
			Log.e(TAG, "Attempting to complete handshake without a code");
		}
		
		return response;
	}
	
	/**
	 * isSuccessfulCompletionResponse
	 * 
	 * @param response
	 * @return boolean
	 */
	public boolean isSuccessfulCompletionResponse(OAuthResponse response) 
	{
		boolean is_successful = false;
		
		try
		{
			JSONObject json = new JSONObject(response.getResponseString());			
			if (json.has("access_token") && json.has("refresh_token"))
				is_successful = true;
		}
		catch (Exception e)
		{
			Log.i(TAG, "response is not json - " + response.getResponseString());
		}
		
		Log.i(TAG, "isSuccessfulCompletionResponse = " + is_successful);
		return is_successful;
	}
	
	/**
	 * storeNecessaryCompletionResponseData
	 * 
	 * @param persistent_storage_editor
	 * @param response
	 */
	public void storeNecessaryCompletionResponseData(Editor persistent_storage_editor, OAuthResponse response) 
	{ 
		try
		{
			JSONObject json = new JSONObject(response.getResponseString());
			Log.i(TAG, "access_token = " + json.getString("access_token"));
			Log.i(TAG, "refresh_token = " + json.getString("refresh_token"));
			
			persistent_storage_editor.putString("gowalla_access_token", json.getString("access_token"));
			persistent_storage_editor.putString("gowalla_refresh_token", json.getString("refresh_token"));
			persistent_storage_editor.commit();
		}
		catch (Exception e)
		{
			Log.i(TAG, "response is not json - " + response.getResponseString());
		}
	}
	
	/**
	 * clearTemporarySettings
	 * 
	 * @param persistent_storage_editor
	 */
	public void clearTemporaryData(Editor persistent_storage_editor)
	{
		// clear initial values
		persistent_storage_editor.remove("gowalla_code");
		persistent_storage_editor.commit();
	}

	/**
	 * createTestUsers
	 * 
	 * creates any necessary gowalla test users.
	 */
	public OAuthResponse createTestUsers(SharedPreferences persistent_storage) { return new OAuthResponse(); }
}
