package com.spring.gpsApiData.service;

import java.util.List;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.entities.gpsData;


public interface GpsDataService {

	public List<historyData> getgpsDataWithSaving(String imei) throws Exception;
	public List<historyData> getgpsDataWithoutSaving(String imei) throws Exception;
	public String savegpsData(gpsData data);
	public List<historyData> getHistoryDataWithSaving(String imei, String startTime, String endTime);
	public void addImei(String imei) throws Exception;
	public String saveHistoryData(historyData data);
	List<DeviceTrackListModel> getHistoryDataDirectFromJimi(String imei, String startTime, String endTime) throws Exception;
}
