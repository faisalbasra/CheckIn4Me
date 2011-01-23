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

import com.davidivins.checkin4me.util.Base64;
import com.davidivins.checkin4me.util.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * OAuth1Request
 * 
 * @author david ivins
 */
public class OAuth1Request extends Request 
{
	private static final String TAG               = "OAuth1Request";
	private static final String HASHING_ALGO      = "HmacSHA1";
	private static final String RESPONSE_ENCODING = "UTF-8";
	
	private String signing_key;
	
	/**
	 * OAuth1Request
	 * 
	 * @param method
	 * @param host
	 * @param endpoint
	 */
	public OAuth1Request(String signing_key, String method, String host, String endpoint)
	{
		super(method, host, endpoint);
		this.signing_key = signing_key;
		Log.i(TAG, "signing_key = " + signing_key);
	}
	
	/**
	 * getNonce
	 * 
	 * @return String
	 */
	public String generateNonce()
	{
		Random random = new Random();
		return Long.toString(Math.abs(random.nextLong()), 60000);
	}
	
	/**
	 * getTimestamp
	 * 
	 * @return String
	 */
	public String generateTimestamp()
	{
		return Integer.toString((int)(System.currentTimeMillis() / 1000L));
	}

	/**
	 * execute
	 * 
	 * @return Response from executing the request
	 */
	@Override
	public OAuthResponse execute() 
	{
		BufferedReader page = null;
		OAuthResponse response = new OAuthResponse();

		Log.i(TAG, "executing Foursquare OAuth request...");
		
		// make request
		String base_string = generateBaseString();
		String signature   = calculateSignature(signing_key, base_string);
		String url_string  = generateURL(signature);
		
		Log.i(TAG, "base string generated = " + base_string);
		Log.i(TAG, "signature calculated  = " + signature);
		Log.i(TAG, "request url generated = " + url_string);
		
		// make http request
		try
		{
			// make background http request for temporary token
			HttpClient   httpclient    = new DefaultHttpClient();
			HttpResponse http_response;
			
			if (method.equals("GET"))
			{
				HttpGet httpget = new HttpGet(url_string);
				
				Set<String> keys = headers.keySet();
				for (String key : keys)
				{
					httpget.addHeader(key, headers.get(key));
				}
				
				http_response = httpclient.execute(httpget);
			}
			else
			{
				HttpPost httppost = new HttpPost(url_string);
				
				Set<String> keys = headers.keySet();
				for (String key : keys)
				{
					httppost.addHeader(key, headers.get(key));
				}
				
				http_response = httpclient.execute(httppost);
			}
	    	
	    	// get content of request
	    	page = new BufferedReader(new InputStreamReader(
	    			http_response.getEntity().getContent(), RESPONSE_ENCODING));
	    		    	
	    	// read response into a string
	    	String line;
	    	while ((line = page.readLine()) != null)
	    	{
	    		response.appendResponseString(line);
	    	}

	    	response.setSuccessStatus(true);	    	
		}
		catch (IOException e)
		{
			response.set(false, e.getMessage());
			Log.e(TAG, "EXCEPTION: " + e.getMessage());
		}

		Log.i(TAG, "response.getSuccessStatus = " + response.getSuccessStatus());
		Log.i(TAG, "response.getResponseString = " + response.getResponseString());
		return response;
	}
	
	/**
	 * generateBaseString
	 * 
	 * @return Base string generated for request
	 */
	private String generateBaseString()
	{
		String base_string = "";
		
		try
		{
			base_string = URLEncoder.encode(method, ENCODING) + "&" + 
				URLEncoder.encode(host, ENCODING)             + 
				URLEncoder.encode(endpoint, ENCODING)         + "&" + 
				URLEncoder.encode(getURIQueryParametersAsString(), ENCODING);
		}
		catch (Exception e)
		{
			Log.wtf(TAG, ENCODING + " doesn't exist!?");
		}
		
		return base_string;
	}
	
	/**
	 * calculateSignature
	 * 
	 * @param secret_key
	 * @param base_string
	 * @return Signature calculated for base string
	 */
	private String calculateSignature(String secret_key, String base_string)
	{
		String out_str = "";

		try
		{
			SecretKey key = new SecretKeySpec(secret_key.getBytes(), HASHING_ALGO);

			Mac m = Mac.getInstance(HASHING_ALGO);
			m.init(key);

			byte[] mac = m.doFinal(base_string.getBytes());

			out_str = Base64.encodeToString(mac, Base64.NO_WRAP);
			out_str = URLEncoder.encode(out_str, ENCODING);
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
		
		Log.i(TAG, "signature calculated = " + out_str);
		return out_str;
	}
	
	/**
	 * generateURL
	 * 
	 * @param signature
	 * @return URL for the request
	 */
	private String generateURL(String signature)
	{
		return host + endpoint + "?" + getURIQueryParametersAsString() + "&" +
			"oauth_signature=" + signature;
	}
}