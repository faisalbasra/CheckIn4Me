package com.davidivins.checkin4me.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import com.davidivins.checkin4me.comparators.LocaleNameComparator;
import com.davidivins.checkin4me.comparators.LocaleServicesTotalComparator;

public class Algorithms 
{
	private Algorithms() {}
	
//	/**
//	 * mergeLocations
//	 * 
//	 * @param ArrayList<ArrayList<Locale>> location_lists
//	 * @return ArrayList<Locale>
//	 */
//	static public ArrayList<Locale> mergeLocations(ArrayList<ArrayList<Locale>> location_lists)
//	{
//		ArrayList<Locale> locations;
//		
//		// if location lists is empty, use empty locations list
//		if (location_lists.isEmpty())
//			locations = new ArrayList<Locale>();
//		else // otherwise, start with first locations list as base, and merge with that
//		{
//			locations = location_lists.get(0);
//			location_lists.remove(0);
//		}
//		
//		// loop through location lists
//		for (ArrayList<Locale> location_list : location_lists)
//		{
//			// loop through incoming locations
//			for (Locale incoming_location : location_list)
//			{
//				boolean merged = false;
//				
//				// compare each incoming location against each existing locations
//				for (Locale existing_location : locations)
//				{
//					// if their names match, merge them
//					//if (existing_location.getName().equals(incoming_location.getName()))
//					if (namesAreTheSame(existing_location.getName(), incoming_location.getName()))
//					{
//						merged = true;
//						
//						// store description if it exists
//						existing_location.setDescription(incoming_location.getDescription());
//						
//						HashMap<Integer, String> mappings = incoming_location.getServiceIdToLocationIdMap();
//						Set<Integer> keys = mappings.keySet();
//						
//						for (int key : keys)
//						{
//							existing_location.mapServiceIdToLocationId(key, mappings.get(key));
//						}
//						
//						// only merge with one location
//						break;
//					}
//				}
//				
//				// if the location wasn't merged, add it to the list
//				if (!merged)
//					locations.add(incoming_location);
//			}
//		}
//		
//		Collections.sort(locations, new LocaleNameComparator());
//		Collections.sort(locations, new LocaleServicesTotalComparator());
//		
//		return locations;
//	}
	
	/**
	 * mergeLocations
	 * 
	 * @param ArrayList<ArrayList<Locale>> location_lists
	 * @return ArrayList<Locale>
	 */
	static public ArrayList<Locale> mergeLocations(ArrayList<ArrayList<Locale>> location_lists)
	{
		int current_index = 0;
		ArrayList<Locale> locations = new ArrayList<Locale>();
		HashMap<String, Integer> name_indexes = new HashMap<String, Integer>();
		
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
						int index = name_indexes.get(location.getName().toLowerCase());
						HashMap<Integer,String> map_id_location_id_xref = location.getServiceIdToLocationIdMap();
						
						Set<Integer> keys = map_id_location_id_xref.keySet();
						for (int key : keys)
						{
							locations.get(index).mapServiceIdToLocationId(key, map_id_location_id_xref.get(key));
						}
					}
					else // add location if it doesn't exist yet
					{
						// get all possible xref names
						String name                               = location.getName().toLowerCase();
						String name_without_apostrophe            = name.replace("'", "");
						String name_with_spaces_for_dashes        = name.replace("-", " ");
						String name_without_dashes                = name.replace("-", "");
						String name_without_punctuation_or_spaces = name.replaceAll("[^A-Za-z0-9]", "");
						
						// add location to list
						locations.add(current_index, location);
						
						// add cross references for alternate names
						name_indexes.put(location.getName(), current_index);
						name_indexes.put(name_without_apostrophe, current_index);
						name_indexes.put(name_with_spaces_for_dashes, current_index);
						name_indexes.put(name_without_dashes, current_index);
						name_indexes.put(name_without_punctuation_or_spaces, current_index);
						
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
	
//	/**
//	 * namesAreTheSame
//	 * 
//	 * @param String existing_name
//	 * @param String incoming_name
//	 */
//	static private boolean namesAreTheSame(String existing_name, String incoming_name)
//	{
//		boolean result = false;
//		
//		String incoming_name_without_apostrophe = incoming_name.replace("'", "");
//		String incoming_name_with_spaces_for_dashes = incoming_name.replace("-", " ");
//		String incoming_name_without_dashes = incoming_name.replace("-", "");
//		String incoming_name_without_punctuation_or_spaces = incoming_name.replaceAll("[^A-Za-z0-9]", "");
//		
//		String existing_name_without_apostrophe = existing_name.replace("'", "");
//		String existing_name_with_spaces_for_dashes = existing_name.replace("-", " ");
//		String existing_name_without_dashes = existing_name.replace("-", "");
//		String existing_name_without_punctuation_or_spaces = existing_name.replaceAll("[^A-Za-z0-9]", "");
//		
//		if (existing_name.equalsIgnoreCase(incoming_name) ||
//				existing_name_without_apostrophe.equalsIgnoreCase(incoming_name_without_apostrophe) ||
//				existing_name_with_spaces_for_dashes.equalsIgnoreCase(incoming_name_with_spaces_for_dashes) ||
//				existing_name_without_dashes.equalsIgnoreCase(incoming_name_without_dashes) ||
//				existing_name_without_punctuation_or_spaces.equalsIgnoreCase(incoming_name_without_punctuation_or_spaces))
//			result = true;
//		
//		return result;
//	}
}
