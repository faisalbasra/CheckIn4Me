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
package com.davidivins.checkin4me.activities;

import com.davidivins.checkin4me.core.GeneratedResources;
import com.davidivins.checkin4me.core.Services;
import com.davidivins.checkin4me.oauth.OAuthConnector;
import com.davidivins.checkin4me.oauth.OAuthResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Authorization
 * 
 * @author david ivins
 */
public class Authorization extends Activity
{	
	private static final String TAG = "Authorization";

	/**
	 * onCreate
	 * 
	 * @param Bundle saved_instance_state
	 */
	@Override
	public void onCreate(Bundle saved_instance_state)
	{
		super.onCreate(saved_instance_state);
		
		if (GeneratedResources.areNotGenerated())
			GeneratedResources.generate(this);
		
		setContentView(GeneratedResources.getLayout("authorization"));
		Intent i = new Intent(Intent.ACTION_VIEW);
		
		SharedPreferences persistent_storage = PreferenceManager.getDefaultSharedPreferences(this);
		Editor persistent_storage_editor = persistent_storage.edit();

		// check if a service was clicked to get here
		if (getIntent().getIntExtra("service_id", -1) != -1)
		{
			// retrieve service id, default to -1 for none/error
			int service_id = getIntent().getIntExtra("service_id", -1);
			Log.i(TAG, "service_id = " + service_id);
			
			// get oauth connector
			OAuthConnector oauth_connector = 
				Services.getInstance(this).getServiceById(service_id).getOAuthConnector();
			
			// begin handshake process
			OAuthResponse response = (OAuthResponse)oauth_connector.beginHandshake();
			
			// check if successful initial response is received
			// if so, store necessary data and generate authorization url
			if (oauth_connector.isSuccessfulInitialResponse(response))
			{
				persistent_storage_editor.putBoolean("handshake_in_progress", true);
				persistent_storage_editor.putInt("handshake_service_id", service_id);
				
				oauth_connector.storeNecessaryInitialResponseData(persistent_storage_editor, response);
				i.setData(Uri.parse(oauth_connector.generateAuthorizationURL(persistent_storage)));
			}
			else
			{
				Log.e(TAG, "Failed to begin handshake: " + response.getResponseString());
				Toast.makeText(getApplicationContext(), "Failed to begin handshake.", Toast.LENGTH_SHORT).show();
				i = new Intent(this, ServiceConnection.class);
			}
		}
		// check if we are returning here from the middle of an oauth handshake
		else if (persistent_storage.getBoolean("handshake_in_progress", false) && 
				persistent_storage.getInt("handshake_service_id", -1) != -1)
		{
			// get the oauth connector for the service currently in the middle of a handshake
			int service_id = persistent_storage.getInt("handshake_service_id", -1);
			OAuthConnector oauth_connector = 
				Services.getInstance(this).getServiceById(service_id).getOAuthConnector();
			
			// get the response for the authorization request
			Uri uri = this.getIntent().getData();
			
			// check if the authorization request was successful
			if (oauth_connector.isSuccessfulAuthorizationResponse(uri))
			{				
				// store necessary response data
				oauth_connector.storeNecessaryAuthorizationResponseData(persistent_storage_editor, uri);
				
				// attempt to complete the handshake
				OAuthResponse response = (OAuthResponse)oauth_connector.completeHandshake(persistent_storage, uri);
				
				// check if the completion response is valid
				if (oauth_connector.isSuccessfulCompletionResponse(response))
				{
					persistent_storage_editor.putBoolean("handshake_in_progress", false);
					persistent_storage_editor.putInt("handshake_service_id", -1);
					
					// store necessary response data
					oauth_connector.storeNecessaryCompletionResponseData(persistent_storage_editor, response);
					
					// start nearby places event
					i.putExtra("tab_to_display", MainTabbedContainer.NEARBY_PLACES_TAB);
					i = new Intent(this, MainTabbedContainer.class);
					
					// clear temporary data
					oauth_connector.clearTemporaryData(persistent_storage_editor);
				}
				else
				{
					Log.e(TAG, "Failed to complete handshake: " + response.getResponseString());
					Toast.makeText(getApplicationContext(), "Failed to complete handshake.", Toast.LENGTH_SHORT).show();
					
					i.putExtra("tab_to_display", MainTabbedContainer.SERVICE_CONNECTION_TAB);
					i = new Intent(this, MainTabbedContainer.class);
				}
			}
			else
			{
				Log.e(TAG, "Failed to authorize app: " + uri.toString());
				Toast.makeText(getApplicationContext(), "Failed to authorize app.", Toast.LENGTH_SHORT).show();
				
				i.putExtra("tab_to_display", MainTabbedContainer.SERVICE_CONNECTION_TAB);
				i = new Intent(this, MainTabbedContainer.class);
			}
		}
		else
		{
			Log.i(TAG, "No service clicked and no handshake in progress");
			Toast.makeText(getApplicationContext(), "No service clicked and no handshake in progress.", Toast.LENGTH_SHORT).show();

			i.putExtra("tab_to_display", MainTabbedContainer.SERVICE_CONNECTION_TAB);
			i = new Intent(this, MainTabbedContainer.class);			
		}
		
		persistent_storage_editor.commit();
		startActivity(i);
	}
}