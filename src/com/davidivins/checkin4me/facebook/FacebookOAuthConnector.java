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
package com.davidivins.checkin4me.facebook;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;
import com.davidivins.checkin4me.interfaces.OAuthConnectorInterface;
import com.davidivins.checkin4me.oauth.OAuth2Request;
import com.davidivins.checkin4me.oauth.OAuthResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.TreeMap;

/**
 * FacebookOAuthConnector
 * 
 * @author david ivins
 */
public class FacebookOAuthConnector implements OAuthConnectorInterface
{
	private static final String TAG      = FacebookOAuthConnector.class.getSimpleName();
	private static final String ENCODING = "ISO-8859-1";

	private Properties config;
	private String oauth_redirect_uri;
	
	/**
	 * FacebookOAuthConnector
	 * 
	 * @param config
	 */
	FacebookOAuthConnector(Properties config) 
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

	public String generateAuthorizationURL(SharedPreferences persistent_storage) 
	{
		String url = config.getProperty("oauth_host") + config.getProperty("oauth_authorize_endpoint")
			+ "?client_id="     + config.getProperty("oauth_client_id", "OAUTH_CLIENT_ID_HERE")
			+ "&redirect_uri="  + oauth_redirect_uri 
			+ "&scope="         + config.getProperty("oauth_scope", "OAUTH_API_SCOPE_HERE")
			+ "&display="       + config.getProperty("oauth_display", "OAUTH_DISPLAY_HERE");
		
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
		persistent_storage_editor.putString("facebook_code", response.getQueryParameter("code"));
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
		Log.i(TAG, "code in persistent_storage = " + persistent_storage.getString("facebook_code", "-1"));
		
		if (persistent_storage.getString("facebook_code", "-1") != "-1")
		{
			OAuth2Request request = new OAuth2Request(
					config.getProperty("oauth_http_method"), config.getProperty("oauth_host"), 
					config.getProperty("oauth_access_token_endpoint"));
			
			request.addQueryParameter("client_id", config.getProperty("oauth_client_id", "OAUTH_CLIENT_ID_HERE"));
			request.addQueryParameter("redirect_uri", oauth_redirect_uri);
			request.addQueryParameter("client_secret", config.getProperty("oauth_client_secret", "OAUTH_CLIENT_SECRET_HERE"));
			request.addQueryParameterAndEncode("code", persistent_storage.getString("facebook_code", "CODE_HERE"));
			
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
		
		TreeMap<String, String> query_parameters = response.getQueryParameters();
		
		if (query_parameters.containsKey("access_token"))
			is_successful = true;
		
		Log.i(TAG, "isSuccessfulAuthorizationResponse = " + is_successful);
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
		TreeMap<String, String> query_parameters = response.getQueryParameters();
		Log.i(TAG, "access_token = " + query_parameters.get("access_token"));
		
		try 
		{
			// encode access code because facebook's contains illegal characters
			persistent_storage_editor.putString("facebook_access_token", URLEncoder.encode(query_parameters.get("access_token"), ENCODING));
			persistent_storage_editor.commit();
		} 
		catch (UnsupportedEncodingException e) 
		{
			Log.e(TAG, ENCODING + " isn't a valid encoding!?");
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
		persistent_storage_editor.remove("facebook_code");
		persistent_storage_editor.commit();
	}
	
	/**
	 * createTestUsers
	 * 
	 * this is currently disabled by facebook for native apps.
	 * 
	 * creates any necessary test users.
	 */
	public OAuthResponse createTestUsers(SharedPreferences persistent_storage) 
	{
		return null;
//		Log.i(TAG, "creating test users");
//		
//		OAuthResponse response                   = new OAuthResponse();
//		OAuthResponse app_access_token_response  = getAppAccessToken();
//		TreeMap<String, String> query_parameters = app_access_token_response.getQueryParameters();
//		
//		if (query_parameters.containsKey("access_token"))
//		{
//			String access_token = query_parameters.get("access_token");
//			
//			OAuth2Request request = new OAuth2Request(
//				config.getProperty("oauth_http_method"), config.getProperty("oauth_host"), 
//				"/" + config.getProperty("app_id") + config.getProperty("oauth_test_user_endpoint"));
//			
//			request.addQueryParameter("installed", "true");
//			request.addQueryParameter("permissions", config.getProperty("oauth_scope"));
//			request.addQueryParameter("method", config.getProperty("api_checkin_http_method").toLowerCase());
//			request.addQueryParameterAndEncode("access_token", access_token);
//			
//			response = (OAuthResponse)request.execute();
//			
//			Log.i(TAG, "response string = " + response.getResponseString());
//		}
//		else
//		{
//			Log.i(TAG, "failed to get app access token");
//		}
//		
//		return response;
	}
	
//	private OAuthResponse getAppAccessToken()
//	{
//		Log.i(TAG, "getting app access token");
//
//		OAuthResponse response = new OAuthResponse();
//		
//		OAuth2Request request = new OAuth2Request(
//			config.getProperty("oauth_http_method"), config.getProperty("oauth_host"), 
//			config.getProperty("oauth_access_token_endpoint"));
//		
//		request.addQueryParameter("client_id", config.getProperty("app_id"));
//		request.addQueryParameter("client_secret", config.getProperty("oauth_client_secret"));
//		request.addQueryParameter("grant_type", "client_credentials");
//		request.addQueryParameter("redirect_uri", oauth_redirect_uri);
//
//		response = (OAuthResponse)request.execute();
//		
//		Log.i(TAG, "response string = " + response.getResponseString());
//		return response;
//	}
}
