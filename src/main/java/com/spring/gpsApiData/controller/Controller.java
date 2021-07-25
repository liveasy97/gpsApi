package com.spring.gpsApiData.controller;

import java.util.List;

import com.spring.gpsApiData.model.GpsDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spring.gpsApiData.entities.gpsData;
import com.spring.gpsApiData.service.gpsDataService;

@RestController
public class Controller {
	
	@Autowired
	private gpsDataService gpsdataService;
	
	@GetMapping("/locationbyimei/{imei}")
	private GpsDataModel getGpsData(@PathVariable String imei) throws Exception {
		return gpsdataService.getgpsData(imei);
	}
	@GetMapping("/locationbyimei")
	private List<gpsData> getAllGpsData(){
		return gpsdataService.getAllGpsData();
	}
	@PostMapping("/locationbyimei")
	private String saveGpsData(@RequestBody gpsData data) {
		gpsdataService.savegpsData(data);
		return "done";
	}

}
