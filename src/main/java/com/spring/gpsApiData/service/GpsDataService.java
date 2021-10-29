package com.spring.gpsApiData.service;

import java.util.List;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.CreateGeoFencePostRequest;
import com.spring.gpsApiData.model.CreateGeoFenceResponse;
import com.spring.gpsApiData.model.DeviceTrackListAndStoppagesListResponse;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.model.IgnitionOffPostRequest;
import com.spring.gpsApiData.model.RelaySendCommandResponse;
import com.spring.gpsApiData.model.RouteHistoryResponse;
import com.spring.gpsApiData.entities.gpsData;


public interface GpsDataService {

	public List<historyData> getgpsDataWithSaving(String imei) throws Exception;
	public List<historyData> getgpsDataWithoutSaving(String imei) throws Exception;
	public String savegpsData(gpsData data);
	public List<historyData> getHistoryDataWithSaving(String imei, String startTime, String endTime);
	public void addImei(String imei) throws Exception;
	public String saveHistoryData(historyData data);
	public DeviceTrackListAndStoppagesListResponse getHistoryDataDirectFromJimi(String imei, String startTime, String endTime) throws Exception;
	public RelaySendCommandResponse commandToDevice(IgnitionOffPostRequest ignitionOffPostRequest) throws Exception;
	public CreateGeoFenceResponse createGeoFence(CreateGeoFencePostRequest createGeoFencePostRequest) throws Exception;
//	public List<RouteHistoryResponse> routeHistory(String imei, String startTime, String endTime) throws Exception;
	
}
