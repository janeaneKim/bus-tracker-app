package com.tts.transitapp.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tts.transitapp.model.Bus;
import com.tts.transitapp.model.BusRequest;
import com.tts.transitapp.model.DistanceResponse;
import com.tts.transitapp.model.GeocodingResponse;
import com.tts.transitapp.model.Location;

@Service
public class TransitService {
	@Value("${transit_url}")
	public String transitUrl;
	
	@Value("${geocoding_url}")
	public String geocodingUrl;
	
	@Value("${distance_url}")
	public String distanceUrl;
	
	@Value("${google_api_key}")
	public String googleApiKey;
	
	
	private List<Bus> getBuses(){
		RestTemplate restTemplate = new RestTemplate();
		Bus[] buses = restTemplate.getForObject(transitUrl, Bus[].class);
		return Arrays.asList(buses);
	}
		
	//call to geocoding API
	public Location getCoordinates(String description) {
		try {
			description = URLEncoder.encode(description, "utf-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("error urlencoding");
			System.exit(1);;
		}
		String url = geocodingUrl + description + "+GA&key=" + googleApiKey;
		RestTemplate restTemplate = new RestTemplate();
		GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class);
		return response.results.get(0).geometry.location;
	}
	
	private double getDistance(Location origin, Location destination) {
		String url = distanceUrl + "origins=" + origin.lat + "," + origin.lng;
		url += "&destinations=" + destination.lat + "," + destination.lng;
		url += "&key=" + googleApiKey;

		RestTemplate restTemplate = new RestTemplate();
		DistanceResponse response = restTemplate.getForObject(url, DistanceResponse.class);
		//used to convert m to miles
		return response.rows.get(0).elements.get(0).distance.value * 0.000621371;
	}
	
	public List<Bus> getNearbyBuses(BusRequest request){
		//to get nearby buses, our strategy will be to get all the buses and filter them by distance
		
		//get all buses
		List<Bus> allBuses = this.getBuses();
		//use geocoding api to get lat/lng of request
		Location personLocation = getCoordinates(request.address + " " + request.city);
		
		List<Bus> nearbyBuses = new ArrayList<>();
		
		for(Bus bus: allBuses) {
			Location busLocation = new Location();
			busLocation.lat = bus.LATITUDE;
			busLocation.lng = bus.LONGITUDE;
			
			//we can use google distance api to figure out distance from each bus to person location
				//not ideal: may be many buses in the system -- many API calls to google distance matrix api
				//takes time and maybe money
			//do a prelim filtering to eliminate buses outside of a certain search range
			double latDistance = Double.parseDouble(busLocation.lat) 
					- Double.parseDouble(personLocation.lat);
			double lngDistance = Double.parseDouble(busLocation.lng) 
					- Double.parseDouble(personLocation.lng);
			if (Math.abs(latDistance) <= 0.02 && Math.abs(lngDistance) <= 0.02) {
				//now we query to distance api
				double distance = getDistance(busLocation, personLocation);
				if( distance <= 10) {
					//round to 2 decimal digits
					bus.distance = (double) Math.round(distance *100) /100; 
					nearbyBuses.add(bus);
				}
			}	
		}
		Collections.sort(nearbyBuses,
				(bus1,bus2) -> {
					if(bus1.distance < bus2.distance) {
						return -1;
					}
					if(bus1.distance > bus2.distance) {
						return 1;
					}
					return 0;
				}); // new BusComparator also work here
		return nearbyBuses;
	}
}
