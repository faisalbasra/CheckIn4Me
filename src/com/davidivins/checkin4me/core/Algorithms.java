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
package com.davidivins.checkin4me.core;

import com.davidivins.checkin4me.comparators.LocaleNameComparator;
import com.davidivins.checkin4me.comparators.LocaleServicesTotalComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class Algorithms 
{
	private static final double EARTH_MEAN_RADIUS = 6371.0; // mean radius of earth in km
	
	private Algorithms() {}
	
	/**
	 * mergeLocations
	 * 
	 * merges locations from service api provided location lists into one list
	 * 
	 * @param location_lists
	 * @return ArrayList<Locale>
	 */
	synchronized static public ArrayList<Locale> mergeLocations(ArrayList<ArrayList<Locale>> location_lists)
	{
		int current_index = 0;
		ArrayList<Locale> locations = new ArrayList<Locale>();
		HashMap< String, ArrayList<Integer> > name_indexes = new HashMap< String, ArrayList<Integer> >();
		
		// if we don't have an empty list of location lists
		if (!location_lists.isEmpty())
		{
			for (ArrayList<Locale> location_list : location_lists)
			{
				for (Locale location : location_list)
				{
					// if name already exists, add new map_id / location_id xref to existing location
					if (name_indexes.containsKey(location.getName().toLowerCase()))
					{
						boolean mapped = false;
						
						for (int index : name_indexes.get(location.getName().toLowerCase()))
						{
							double distance = Math.abs(getDistance(Double.valueOf(location.getLongitude()), Double.valueOf(location.getLatitude()),
									Double.valueOf(locations.get(index).getLongitude()), Double.valueOf(locations.get(index).getLatitude())));
							
							// if the two locations are further than 1 km from each other, treat as different places
							if (distance <= 1.0)
							{
								HashMap<Integer,String> map_id_location_id_xref = location.getServiceIdToLocationIdMap();
								Set<Integer> keys = map_id_location_id_xref.keySet();
								
								for (int key : keys) // will only be one item for locations coming directly from api calls
								{
									if (!locations.get(index).getServiceIdToLocationIdMap().containsKey(key))
									{
										locations.get(index).mapServiceIdToLocationId(key, map_id_location_id_xref.get(key));
										mapped = true;
										break;
									}
								}
							}
							
							if (mapped) break;
						}
						
						// if we failed to map the location to any existing locations, even if the name existed
						// (ie: it wasn't close enough to any of the existing similarly named locations, add it 
						// as a new location
						if (!mapped)
						{	
							// add location to list
							locations.add(current_index, location);
							
							// get all possible xref names
							ArrayList<String> name_variations = getNameVariations(location.getName().toLowerCase());	

							// create arrays if necessary
							for (String name_variation : name_variations)
							{
								// create array of xrefs if array doesn't exist yet
								if (null == name_indexes.get(name_variation))
									name_indexes.put(name_variation, new ArrayList<Integer>());

								// add xref
								name_indexes.get(name_variation).add(current_index);
							}
						
							// increment location counter
							current_index++;
						}
							
					}
					else // add location as a new location if it doesn't exist yet
					{
						// add location to list
						locations.add(current_index, location);
						
						// get all possible xref names
						ArrayList<String> name_variations = getNameVariations(location.getName().toLowerCase());	

						// create arrays if necessary
						for (String name_variation : name_variations)
						{
							// create array of xrefs if array doesn't exist yet
							if (null == name_indexes.get(name_variation))
								name_indexes.put(name_variation, new ArrayList<Integer>());

							// add xref
							name_indexes.get(name_variation).add(current_index);
						}
					
						// increment location counter
						current_index++;
					}
				}
			}
		}
		
		// sort by locations with most services, secondary sort alphabetically
		Collections.sort(locations, new LocaleNameComparator());
		Collections.sort(locations, new LocaleServicesTotalComparator());
		return locations;
	}

	/**
	 * getDistance
	 * 
	 * gets the distance between two places in km given the places' longitudes and latitudes.
	 * 
	 * @param longitude_1
	 * @param latitude_1
	 * @param longitude_2
	 * @param latitude_2
	 * @return double distance
	 */
	public static double getDistance(double longitude_1, double latitude_1, double longitude_2, double latitude_2)
	{
		double longitude_difference = Math.toRadians(longitude_2 - longitude_1);
		double latitude_difference =  Math.toRadians(latitude_2 - latitude_1);

		double a = (Math.sin(latitude_difference / 2) * Math.sin(latitude_difference / 2)) + 
			Math.cos(Math.toRadians(latitude_1)) * Math.cos(Math.toRadians(latitude_2)) * 
			(Math.sin(longitude_difference / 2) * Math.sin(longitude_difference / 2));
		double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		// distance in km
		return angle * EARTH_MEAN_RADIUS; 
	}
	
	/**
	 * getNameVariations
	 * 
	 * @param name
	 * @return ArrayList<String>
	 */
	private static ArrayList<String> getNameVariations(String name)
	{
		ArrayList<String> name_variations = new ArrayList<String>();
		
		name_variations.add(name);
		name_variations.add(name.replace("'", ""));
		name_variations.add(name.replace("-", " "));
		name_variations.add(name.replace("-", ""));
		name_variations.add(name.replace(".", ""));
		name_variations.add(name.replace("'", ""));
		name_variations.add(name.replaceAll("[^A-Za-z0-9]", ""));
		
		return name_variations;
	}
}
