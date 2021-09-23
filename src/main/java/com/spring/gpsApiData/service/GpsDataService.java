package com.spring.gpsApiData.service;

import java.util.List;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;

import com.spring.gpsApiData.entities.gpsData;


public interface GpsDataService {

	public List<historyData> getgpsData(String imei) throws Exception;
	public String savegpsData(gpsData data);
	public List<historyData> getHistoryData(String imei, String startTime, String endTime);
	public void addImei(String imei) throws Exception;
	public String saveHistoryData(historyData data);
}
