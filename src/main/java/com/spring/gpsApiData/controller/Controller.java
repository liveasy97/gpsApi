package com.spring.gpsApiData.controller;

import java.util.List;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.gpsApiData.service.GpsDataService;

@RestController
public class Controller {
	
	@Autowired
	private GpsDataService gpsdataService;
	
	@GetMapping("/locationbyimei/{imei}")
	private List<historyData> getGpsData(@PathVariable String imei) throws Exception {
		return gpsdataService.getgpsData(imei);
	}

	@GetMapping("/locationbyimei")
	public ResponseEntity<List<historyData>> getHistoryData(
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = false) String imei){
		return new ResponseEntity<>(gpsdataService.getHistoryData(imei, startTime, endTime) , HttpStatus.OK);
	}

//	@PostMapping("/locationbyimei")
//	private String saveGpsData(@RequestBody gpsData data) {
//		gpsdataService.savegpsData(data);
//		return "done";
//	}

	@PostMapping("/locationbyimei")
	private String saveHistoryData(@RequestBody historyData data) {
		gpsdataService.saveHistoryData(data);
		return "done";
	}

	@PostMapping("/addimei")
	private ResponseEntity<String> addImei(@RequestBody String imei) {
		try {
			
			gpsdataService.addImei(imei);
		}
		catch(Exception e) {
	
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Added Imei Successfully", HttpStatus.OK);
	}
}
