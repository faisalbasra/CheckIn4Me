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
package com.davidivins.checkin4me.util;

/**
 * Response
 * 
 * @author david ivins
 */
public class Response 
{
	protected boolean success_status;
	protected String response_string;
	
	/**
	 * Response
	 */
	public Response()
	{
		this.success_status = false;
		this.response_string = "";
	}
	
	/**
	 * Response
	 * 
	 * @param success_status
	 * @param response_string
	 */
	public Response(boolean success_status, String response_string)
	{
		this.success_status = success_status;
		this.response_string = response_string;
	}
	
	/**
	 * setSuccessStatus
	 * 
	 * @param success_status
	 */
	public void setSuccessStatus(boolean success_status)
	{
		this.success_status = success_status;
	}

	/**
	 * setResponseString
	 * 
	 * @param response_string
	 */
	public void setResponseString(String response_string)
	{
		this.response_string = response_string;
	}
	
	/**
	 * appendResponseString
	 * 
	 * @param additional_response_string
	 */
	public void appendResponseString(String additional_response_string)
	{
		response_string += additional_response_string;
	}
	
	/**
	 * set
	 * 
	 * @param success_status
	 * @param response_string
	 */
	public void set(boolean success_status, String response_string)
	{
		this.success_status = success_status;
		this.response_string = response_string;
	}
	
	/**
	 * getSuccessStatus
	 * 
	 * @return boolean
	 */
	public boolean getSuccessStatus()
	{
		return success_status;
	}
	
	/**
	 * getResponseString
	 * 
	 * @return String
	 */
	public String getResponseString()
	{
		return response_string;
	}
}
