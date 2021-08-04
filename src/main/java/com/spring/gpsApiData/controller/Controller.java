package com.spring.gpsApiData.controller;

import java.util.List;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.GpsDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	private Stream<historyData> getAllHistoryData(){
		return gpsdataService.getAllHistoryData();
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
	private String addImei(@RequestBody String imei) {
		return gpsdataService.addImei(imei);
	}
}
