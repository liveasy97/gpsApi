package com.spring.gpsApiData.service;

import java.util.List;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;

import com.spring.gpsApiData.entities.gpsData;


public interface gpsDataService {

	public List<historyData> getgpsData(String imei) throws Exception;
	public String savegpsData(gpsData data);
	public Stream<historyData> getAllHistoryData();
	public String addImei(String imei);
	public String saveHistoryData(historyData data);
}
