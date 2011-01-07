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
package com.davidivins.checkin4me.yelp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;

import com.davidivins.checkin4me.oauth.OAuthResponse;
import com.davidivins.checkin4me.util.Request;
import com.davidivins.checkin4me.util.Response;

import android.util.Log;

/**
 * YelpOAuthRequest
 * 
 * @author david ivins
 */
public class YelpOAuthRequest extends Request 
{
	private static final String TAG               = "YelpOAuthRequest";
	private static final String RESPONSE_ENCODING = "UTF-8";
	
	/**
	 * YelpOAuthRequest
	 * 
	 * @param method
	 * @param host
	 * @param endpoint
	 */
	public YelpOAuthRequest(String method, String host, String endpoint)
	{
		super(method, host, endpoint);
	}

	/**
	 * execute
	 * 
	 * @return Response from executing the request
	 */
	@Override
	public Response execute() 
	{
		BufferedReader page = null;
		OAuthResponse response = new OAuthResponse();

		Log.i(TAG, "executing Yelp OAuth request...");
		
		// make request
		String url_string  = generateURL();
		Log.i(TAG, "request url = " + url_string);
		
		// make http request
		try
		{
			// make background http request for temporary token
			HttpClient httpclient = getTolerantClient();//new DefaultHttpClient();
			HttpResponse http_response;
			
			if (method.equals("GET"))
			{
				HttpGet httpget = new HttpGet(url_string);
				http_response = httpclient.execute(httpget);
			}
			else
			{
				HttpPost httppost = new HttpPost(url_string);
				http_response = httpclient.execute(httppost);
			}

	    	// get content of request
	    	page = new BufferedReader(new InputStreamReader(
	    			http_response.getEntity().getContent(), RESPONSE_ENCODING));
	    	
	    	// read response into a string
	    	String line;
	    	while ((line = page.readLine()) != null)
	    	{
	    		Log.i(TAG, "line = " + line);
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
	 * generateURL
	 * 
	 * @return URL for the request
	 */
	private String generateURL()
	{
		return host + endpoint + "?" + getURIQueryParametersAsString();
	}
	
	/**
	 * getTolerantClient
	 * 
	 * Stolen from stackoverflow.com
	 * http://stackoverflow.com/questions/3135679/android-httpclient-hostname-in-certificate-didnt-match-example-com-exa
	 * 
	 * @return DefaultttpClient
	 */
	public DefaultHttpClient getTolerantClient() 
	{
		DefaultHttpClient client = new DefaultHttpClient();
		
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory)client
			.getConnectionManager().getSchemeRegistry().getScheme("https")
			.getSocketFactory();
		
		final X509HostnameVerifier delegate = sslSocketFactory.getHostnameVerifier();
		
		if(!(delegate instanceof TolerantVerifier)) 
			sslSocketFactory.setHostnameVerifier(new TolerantVerifier(delegate));
		
		return client;
	}
	
	/**
	 * TolerantVerifier
	 * 
	 * Stolen from stackoverflow.com
	 * http://stackoverflow.com/questions/3135679/android-httpclient-hostname-in-certificate-didnt-match-example-com-exa
	 * 
	 * @author noah@stackoverflow
	 */
	class TolerantVerifier extends AbstractVerifier 
	{
		private final X509HostnameVerifier delegate;

		/**
		 * TolerantVerfier
		 * 
		 * @param delegate
		 */
		public TolerantVerifier(final X509HostnameVerifier delegate) 
		{
			this.delegate = delegate;
		}

		/**
		 * verify
		 * 
		 * @param host
		 * @param cns
		 * @param subjectAlts
		 */
		public final void verify(String host, String[] cns, String[] subjectAlts) //throws SSLException 
		{
			boolean ok = false;
			
			try 
			{
				delegate.verify(host, cns, subjectAlts);
			} 
			catch (SSLException e) 
			{
				for (String cn : cns) 
				{
					if (cn.startsWith("*.")) 
					{
						try 
						{
							delegate.verify(host, new String[] { cn.substring(2) }, subjectAlts);
							ok = true;
						} 
						catch (Exception e1) 
						{
							Log.e(TAG, "We are here and I'm not sure why...");
						}
					}
				}
				
				if(!ok) 
					Log.i(TAG, "Failed verification"); //throw e;
			}
		}
	}
}
