/*package com.cyber.trafficmap; 

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.android.maps.GeoPoint;

public class DocParser {
	private Scanner  reader;

	@SuppressWarnings("unused")
	public DocParser(String path)
	{
		File f = new File(path);		
		if(f == null){
			throw new IllegalArgumentException("Invalid file path");
		}
			else{				
		try 
		{
			reader = new Scanner(f);
			reader.useDelimiter("[:]");
		}
		catch (FileNotFoundException e) 
		{
			//e.printStackTrace();
			e.getLocalizedMessage();
			throw new IllegalArgumentException("Invalid file path!!!");
		}}
	}
	*//**
	 * <b>Description:</b>
	 * <ul>
	 *	 	<li>scans the input file or fileLocation passed at the constructor.</li>
	 * 		<li>cleans the string format to remove unwanted spaces.</li>
	 * </ul>
	 * @return list of co-ordinates as GeoLocation.
	 *//*
	public ArrayList<GeoPoint> scanInputFile(){
		ArrayList<GeoPoint> inputs = new ArrayList<GeoPoint>();
		String input = null;
		
		while(reader.hasNext()){
			input = reader.next();

			if(input == null || input.trim().length() == 0){
				continue;
			}
			input = input.trim().toLowerCase();
			
			if(isValidInput(input)){
				String[] s= input.split(",");
				int latitude = (int) Double.parseDouble(s[0]);
				int longitude = (int) Double.parseDouble(s[1]);
				
				inputs.add(new GeoPoint(latitude , longitude));	
			}
		}
		return inputs;
	}
	
	
	
	*//**
	 * validates the input
	 * @param input the string to be validated
	 * @return	valid form of the input
	 *//*
	public boolean isValidInput(String input){
		if(input!=null && input.contains(",")){
			return true;
		}
		return false;
	}
	
	class GeoLocation{
		public double latitude;
		public double longitude;
		GeoLocation(double latitude2, double longitude2)
		{
			this.latitude =  latitude2;
			this.longitude =  longitude2;
		}
	}

}
*/