package com.tts.transitapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.tts.transitapp.model.Bus;
import com.tts.transitapp.model.BusRequest;
import com.tts.transitapp.model.Location;
import com.tts.transitapp.service.TransitService;

@Controller
public class TransitController {
	@Autowired
	private TransitService apiService;
	
	@GetMapping(path="/")
	public String redirectRoot() {
		return "redirect:/buses";
	}
	
	@GetMapping(path="/buses")
	public String getBusesPage(Model model) {
		model.addAttribute("request", new BusRequest());
		return "index";
	}
	
	@PostMapping(path="/buses")
	public String getNearbyBuses(BusRequest request, Model model) {
		Location userLocation = apiService.getCoordinates(request.address + " " + request.city);
		List<Bus> buses = apiService.getNearbyBuses(request);
		
		
		model.addAttribute("buses", buses);
		model.addAttribute("request", request);
		model.addAttribute("userLocation", userLocation);
		return "index";
	}
}
