package com.spring.gpsApiData.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.gpsApiData.dao.IGpsDataDao;
import com.spring.gpsApiData.dao.model.StoppageData;
import com.spring.gpsApiData.entities.Tc_position;
import com.spring.gpsApiData.entities.gpsData;
import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.CreateGeoFencePostRequest;
import com.spring.gpsApiData.model.CreateGeoFenceResponse;
import com.spring.gpsApiData.model.DeviceTrackListAndStoppagesListResponse;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import com.spring.gpsApiData.model.IgnitionOffPostRequest;
import com.spring.gpsApiData.model.JimiException;
import com.spring.gpsApiData.model.RelaySendCommandResponse;
import com.spring.gpsApiData.model.RouteHistoryWithTotalDistanceModel;
import com.spring.gpsApiData.model.StoppagesListModel;
import com.spring.gpsApiData.model.duration;
import com.spring.gpsApiData.utils.JimiApiresponseUtils;

@Service
public class GpsDataServiceImplTraccar implements GpsDataService {

	private static final Logger log = LoggerFactory.getLogger(GpsDataServiceImplTraccar.class);

	@Autowired
	private IGpsDataDao iGpsDao;

	@Autowired
	JimiApiresponseUtils resUtils;

	@Override
	public DeviceTrackListAndStoppagesListResponse getHistoryDataUsingTraccar(String imei, String startTime,
			String endTime) throws Exception {

		DeviceTrackListAndStoppagesListResponse response = new DeviceTrackListAndStoppagesListResponse();
		List<DeviceTrackListModel> deviceTrackDataList = new ArrayList<DeviceTrackListModel>();

		List<Tc_position> tcpData = iGpsDao.getMinuteWiseSummarizedData(imei, startTime, endTime);
		List<StoppageData> stoppagedata = iGpsDao.getStoppages(imei, startTime, endTime);

		if (tcpData == null || tcpData.isEmpty()) {
			throw new JimiException("No Data Found");
		}

		for (int i = 0; i < tcpData.size(); i++) {

			Tc_position position = tcpData.get(i);
			DeviceTrackListModel obj = new DeviceTrackListModel();

			obj.setGpsSpeed(position.getSpeed());
			obj.setLat(position.getLat());
			obj.setLng(position.getLng());
			obj.setGpsTime(position.getServerTime());
			deviceTrackDataList.add(obj);

		}
		Collections.reverse(deviceTrackDataList);
		response.setDeviceTrackList(deviceTrackDataList);

		List<StoppagesListModel> stoppagesList = new ArrayList<StoppagesListModel>();

		if (stoppagedata == null || stoppagedata.isEmpty()) {
			return response;
		}

		for (int i = 0; i < stoppagedata.size(); i++) {

			StoppageData data = stoppagedata.get(i);

			if (data.getType() == null || data.getType().equals("deviceMoving")) {
				continue;
			}

			StoppagesListModel obj = new StoppagesListModel();

			if (data.getType().equals("deviceStopped")) {

				obj.setStartTime(data.getEventTime());
				obj.setLat(data.getLatitude());
				obj.setLng(data.getLongitude());
				obj.setTruckStatus("stopped");
			}

			if (i + 1 < stoppagedata.size() && stoppagedata.get(i + 1).getType().equals("deviceMoving")) {

				obj.setEndTime(stoppagedata.get(i + 1).getEventTime());
			} else {
				obj.setEndTime(resUtils.convert_DateToString(new Date()));
			}
			duration durationObj = resUtils.findDuration(obj.getStartTime(), obj.getEndTime());
			if (durationObj.getDays() == 0 && durationObj.getHours() == 0 && durationObj.getMinutes() == 0
					&& durationObj.getSeconds() != 0) {
				continue;
			} else {
				obj.setDuration(durationObj.toString());

			}
			stoppagesList.add(obj);
		}
		Collections.reverse(stoppagesList);
		response.setStoppagesList(stoppagesList);
		return response;
	}

	@Override
	public List<historyData> getgpsDataWithSaving(String imei) throws Exception {
		return null;
	}

	// fetching data directly from jimi//
	@Override
	public List<historyData> getgpsDataWithoutSaving(String imei) throws Exception {

		return null;

	}

	@Override
	public String savegpsData(gpsData data) {
		return null;
	}

	@Override
	public List<historyData> getHistoryDataWithSaving(String imei, String startTime, String endTime) {
		return null;
	}

	@Override
	public void addImei(String imei) throws Exception {

	}

	@Override
	public String saveHistoryData(historyData data) {
		return null;
	}

	@Override
	public RelaySendCommandResponse commandToDevice(IgnitionOffPostRequest ignitionOffPostRequest) throws Exception {

		return null;
	}

	@Override
	public CreateGeoFenceResponse createGeoFence(CreateGeoFencePostRequest createGeoFencePostRequest) throws Exception {

		return null;
	}

	@Override
	public RouteHistoryWithTotalDistanceModel routeHistory(String imei, String startTime, String endTime)
			throws Exception {

		return null;
	}

	@Override
	public DeviceTrackListAndStoppagesListResponse getHistoryDataDirectFromJimi(String imei, String startTime,
			String endTime) throws Exception {

		return null;
	}
}
