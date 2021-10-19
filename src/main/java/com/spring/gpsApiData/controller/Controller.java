package com.spring.gpsApiData.controller;

import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.model.IgnitionOffPostRequest;
import com.spring.gpsApiData.model.RelaySendCommandResponseModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import com.spring.gpsApiData.service.GpsDataService;

import lombok.extern.java.Log;

@RestController
public class Controller {
	
	@Autowired
	private GpsDataService gpsdataService;
	
	@GetMapping("/locationbyimei/{imei}")
	private List<historyData> getGpsData(@PathVariable String imei) throws Exception {
		return gpsdataService.getgpsDataWithoutSaving(imei);
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
	    String name = ex.getParameterName();
	    System.out.println(name + " parameter is missing");
	    return new ResponseEntity<>(name + " parameter is missing", HttpStatus.BAD_REQUEST);
	}
	@GetMapping("/locationbyimei")
	public ResponseEntity<List<DeviceTrackListModel>> getHistoryData(
			@RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = true) String imei) throws Exception {
		
			return new ResponseEntity<>(gpsdataService.getHistoryDataDirectFromJimi(imei, startTime, endTime) , HttpStatus.OK);
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
	
	@PostMapping("/ignitionoff")
	private ResponseEntity<RelaySendCommandResponseModel> sendInstructionToDevice(@RequestBody IgnitionOffPostRequest ignitionOffPostRequest) throws Exception {
				
			RelaySendCommandResponseModel response = gpsdataService.commandToDevice(ignitionOffPostRequest);
		   
			if(response.getCode()==12005)
			{
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		    
		    return new ResponseEntity<>(response, HttpStatus.OK);

	}
}