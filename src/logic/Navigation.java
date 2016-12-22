package logic;

import data.members.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
//import java.util.Set;


public class Navigation {
	
	public static boolean canPark(User user, ParkingSlot parkingSlot){
		return user.getSticker().ordinal() <= parkingSlot.getColor().ordinal();
	}
	
	private static JSONObject getInnerJSON(String url){
		JSONParser parser = new JSONParser();
		try{
			
		 	URL u = new URL(url);
		    String jsonString = IOUtils.toString(u, StandardCharsets.UTF_8);
		    
	        JSONObject theJSON = (JSONObject)  parser.parse(jsonString);
	        JSONArray rows = (JSONArray) theJSON.get("rows"); 
	        JSONObject elements = (JSONObject)rows.get(0);
	        JSONArray innerElements = (JSONArray) elements.get("elements");
	        JSONObject firstElement = (JSONObject)innerElements.get(0);
	        return firstElement;
            
		}catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	public static String createURL(MapLocation source, MapLocation target, boolean walkingMode){
		String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=";
		url += source.getLat() + "," + source.getLon();
		url += "&destinations="  + target.getLat() + "," + target.getLon();
		url += "&key=AIzaSyDw25loi0t1ms-bCuLPHS2Bm9aEIvyu9Wo";
		
		if(walkingMode == true)
			url += "&mode=walking";
		
		return url;
	}
	
	public static long getDistance(MapLocation source, MapLocation target, boolean walkingMode){
		// returns distance in meters
		String url = createURL(source, target, walkingMode);
		JSONObject element = getInnerJSON(url);
		if (element == null)
			return 0;
		
        JSONObject distance = (JSONObject)element.get("distance");
        return (long) distance.get("value");
	}
	
	public static long getDuration(MapLocation source, MapLocation target, boolean walkingMode){
		// returns duration in seconds
		String url = createURL(source, target, walkingMode);
		JSONObject element = getInnerJSON(url);
		if (element == null)
			return 0;
		
        JSONObject duration = (JSONObject)element.get("duration");
        return (long) duration.get("value");
	}	
	
	public static int getClosestParkingArea(MapLocation currentLocation, boolean walkingMode){
		
		JSONParser parser = new JSONParser();
		try{
			JSONArray a = (JSONArray) parser.parse(new FileReader("./src/logic/parkingAreas.json"));
			int minID = -1;
			long dist = Integer.MAX_VALUE;
			for (Object o : a)
			{
				
				JSONObject parkingArea = (JSONObject) o;
				int id = Integer.parseInt((String) parkingArea.get("id"));
				double targetLat = Double.parseDouble((String) parkingArea.get("locationX"));
				double targetLon =  Double.parseDouble((String) parkingArea.get("locationY"));
				MapLocation target = new MapLocation(targetLat, targetLon);
				long d = getDistance(currentLocation, target, walkingMode);
				if (d < dist){
					minID = id;
					dist = d;
				}
			}
			return minID;
			
		}catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return -1; 
	}

	public static ParkingSlot closestParkingSlot(User user,MapLocation currentLocation, ParkingAreas areas, Faculty faculty){

		ParkingSlot result = null;
		long minDuration = Integer.MAX_VALUE;
		
		for(ParkingArea parkingArea : areas.getParkingAreas()) {
			
			if(parkingArea.getNumOfFreeSlots() <= 0)
				continue;
			
			for(ParkingSlot parkingSlot : parkingArea.getFreeSlots()){
				
				if(!(canPark(user, parkingSlot))) 
					continue;
				
				long duration = getDuration(parkingSlot.getLocation(), faculty.getEntrance(), true);
				if(duration < minDuration){
					result = parkingSlot;
					minDuration = duration;
				}
			}
		}
		return result;
	}
	
	// returns the closest parking to the given faculty in the given parking area
	public static ParkingSlot parkingSlotAtParkingArea(User user, ParkingArea parkingArea, Faculty faculty){
		
		if(parkingArea.getNumOfFreeSlots() <= 0)
			return null;
		
		ParkingSlot result = null;
		long minDuration = Integer.MAX_VALUE;
	
		for(ParkingSlot parkingSlot : parkingArea.getFreeSlots()){
			
			if(!(canPark(user, parkingSlot))) 
				continue;
			
			long duration = getDuration(parkingSlot.getLocation(), faculty.getEntrance() , true);
			if(duration < minDuration){
				result = parkingSlot;
				minDuration = duration;
			}
		}
		return result;
	}
	
	
	public void parkAtArea(User user, ParkingArea parkingArea, Faculty faculty){
		ParkingSlot parkingSlot = parkingSlotAtParkingArea(user, parkingArea, faculty);
		if(parkingSlot == null){
			// TODO: throw exception
		}
		parkingArea.changeFreeToTaken(parkingSlot, user);
	}
	public void parkAtClosestSlot(User user, MapLocation currentLocation, ParkingAreas areas, Faculty faculty){
		ParkingSlot parkingSlot = closestParkingSlot(user, currentLocation, areas, faculty);
		if(parkingSlot == null){
			// TODO: throw exception
		}
	}
}




