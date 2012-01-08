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


import com.davidivins.checkin4me.oauth.OAuthResponse;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

/**
 * OAuthConnector
 * 
 * @author david ivins
 */
public abstract interface OAuthConnectorInterface
{
	abstract OAuthResponse beginHandshake();
	abstract boolean isSuccessfulInitialResponse(OAuthResponse response);
	abstract void storeNecessaryInitialResponseData(Editor persistent_storage_editor, OAuthResponse response);
	abstract String generateAuthorizationURL(SharedPreferences persistent_storage);
	abstract boolean isSuccessfulAuthorizationResponse(Uri response);
	abstract void storeNecessaryAuthorizationResponseData(Editor persistent_storage_editor, Uri response);
	abstract OAuthResponse completeHandshake(SharedPreferences persistent_storage, Uri previous_response);
	abstract boolean isSuccessfulCompletionResponse(OAuthResponse response);
	abstract void storeNecessaryCompletionResponseData(Editor persistent_storage_editor, OAuthResponse response);
	abstract void clearTemporaryData(Editor persistent_storage_editor);
	abstract OAuthResponse createTestUsers(SharedPreferences persistent_storage);
}
