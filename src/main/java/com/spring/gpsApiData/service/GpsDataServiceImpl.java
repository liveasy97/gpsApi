package com.spring.gpsApiData.service;

import java.util.*;
import java.util.stream.Stream;

import com.spring.gpsApiData.dao.DeviceDataDao;
import com.spring.gpsApiData.dao.TraccarDataDao;
import com.spring.gpsApiData.entities.DeviceData;
import com.spring.gpsApiData.entities.TraccarData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import com.spring.gpsApiData.entities.historyData;

@Service
public class GpsDataServiceImpl implements GpsDataService {

	private static final Logger log = LoggerFactory.getLogger(GpsDataServiceImpl.class);

	@Autowired
	private TraccarDataDao dao;

	@Autowired
	private DeviceDataDao deviceDataDao;

/*
	@Override
	public List<historyData> getgpsDataWithSaving(String imei) throws Exception {
		if (imei.contains("transporter") || imei.contains("shipper")) {
			List<historyData> dataList = dao.findByImei(imei);
			List<historyData> result = new ArrayList<>(Collections.emptyList());
			historyData lastEntry = dataList.get(dataList.size() - 1);
			result.add(lastEntry);
			return result;
		} else {
			List<historyData> result = new ArrayList<>(Collections.emptyList());
			historyData gpsData = getDataFromJimi.getGpsApiDataUsingImei(imei);
			result.add(gpsData);
//            gpsData.setId(UUID.randomUUID());
//            dao.save(gpsData);
			return result;
		}
	}
*/

	@Override
	public Stream<Object> getgpsDataWithoutSaving(String imei) throws Exception {

		DeviceData device = deviceDataDao.findByImei(imei);

		List<TraccarData> result = dao.findByNId(device.getId());


		Stream<Object> resultF = result.stream().map((x)->{
			historyData d = new historyData();
			d.setId(UUID.randomUUID());
			d.setImei(String.valueOf(device.getUniqueid()));
			d.setLat(String.valueOf(x.getLatitude()));
			d.setLng(String.valueOf(x.getLongitude()));
			d.setDeviceName(device.getName());
			d.setDirection(String.valueOf(x.getCourse()));
			d.setHbTime(x.getFixtime().toString());
			d.setSpeed(String.valueOf(x.getSpeed()));
			d.setGpsTime(x.getDevicetime().toString());
			d.setPowerValue(null);
			d.setTimeStamp(x.getServertime().toString());
			return d;
		});
		return resultF;


//		Stream<Object> resultF = result.stream().map((x) -> {
//
//
//
//			historyData d = new historyData();
//			d.setId(UUID.randomUUID());
//			d.setImei(imei);
//			d.setLat(String.valueOf(x.getLatitude()));
//			d.setLng(String.valueOf(x.getLongitude()));
//			d.setSpeed(String.valueOf(x.getSpeed()));
//			d.setDeviceName(device.getName());
//			d.setPowerValue(null);
//			//-----------------------------
//			if (x.getCourse() != 0)
//				d.setDirection(String.valueOf(x.getCourse()));
//			else
//				d.setDirection("0");
//			//-----------------------------
//			d.setTimeStamp(x.getServertime().toString());
//			d.setHbTime(x.getFixtime().toString());
//			d.setGpsTime(x.getDevicetime().toString());
//			//
//			//
//			//
//			//
//			return d;
//
//		});
//
//		return resultF;
	}

/*
	@Override
	public String savegpsData(gpsData data) {
		return data.toString();
	}

	@Override
	public List<historyData> getHistoryDataWithSaving(String imei, String startTime, String endTime) {
		if (imei != null) {
			List<historyData> historyDataList = dao.findByImeiBetweenTimeRange(imei, startTime, endTime);

			return historyDataList;
		} else {
			return dao.findHistoryDataBetweenTimeRange(startTime, endTime);
		}
	}

	// fetching data directly from jimi //
	@Override
	public DeviceTrackListAndStoppagesListResponse getHistoryDataDirectFromJimi(String imei, String startTime,
			String endTime) throws Exception {

		DeviceTrackListAndStoppagesListResponse deviceTrackList = getDataFromJimi
				.getGpsApiDataUsingImeiStartTimeEndTime(imei, startTime, endTime);
		return deviceTrackList;
	}

	@Override
	public void addImei(String imei) throws Exception {

		RegisteredImeiData registeredimeidata = new RegisteredImeiData();
		registeredimeidata.setImei(imei);
		registeredimeidata.setStatus(Status.active.toString());
		;

		if (rdao.existsByImei(imei)) {
			throw new Exception("Imei Already exists");
		}

		rdao.save(registeredimeidata);
	}

	@Override
	public String saveHistoryData(historyData data) {
		data.setId(UUID.randomUUID());
		dao.save(data);
		return "done";
	}

	@Override
	public RelaySendCommandResponse commandToDevice(IgnitionOffPostRequest ignitionOffPostRequest) throws Exception {

		RelaySendCommandResponse relayInfo = getDataFromJimi.getGpsApiDeviceRelayData(ignitionOffPostRequest);

		RelaySendCommandResponse response = new RelaySendCommandResponse();
		response.setCode(relayInfo.getCode());
		response.setResult(relayInfo.getResult());

		if (relayInfo.getCode() == 12005) {
			if (relayInfo.getMessage().contains("225")) {
				response.setMessage("TimeOut");
			} else if (relayInfo.getMessage().contains("226")) {
				response.setMessage("Parameter error");
			} else if (relayInfo.getMessage().contains("227")) {
				response.setMessage("The command is not executed correctly");
			} else if (relayInfo.getMessage().contains("228")) {
				response.setMessage("The device is not online");
			} else if (relayInfo.getMessage().contains("229")) {
				response.setMessage("Network error, connection error, etc.");
			} else if (relayInfo.getMessage().contains("238")) {
				response.setMessage("Device interrupted");
			} else if (relayInfo.getMessage().contains("240")) {
				response.setMessage("Data format error");
			} else if (relayInfo.getMessage().contains("243")) {
				response.setMessage("Not supported by device");
			} else if (relayInfo.getMessage().contains("252")) {
				response.setMessage("The device is busy");
			}

		} else if (relayInfo.getCode() == 0) {
			response.setMessage("Send command successful");
		}
		return response;
	}

	@Override
	public CreateGeoFenceResponse createGeoFence(CreateGeoFencePostRequest createGeoFencePostRequest) throws Exception {

		CreateGeoFenceResponse createGeoFenceResponse = getDataFromJimi.CreatingGeoFence(createGeoFencePostRequest);
		CreateGeoFenceResponse response = new CreateGeoFenceResponse();
		response.setCode(createGeoFenceResponse.getCode());
		response.setResult(createGeoFenceResponse.getResult());
		if (createGeoFenceResponse.getCode() == 12003) {
			if (createGeoFenceResponse.getMessage().contains("41001")) {
				response.setMessage("Exceed max number of Geo-fences supported");
			} else if (createGeoFenceResponse.getMessage().contains("41002")) {
				response.setMessage("Fence name is already exists");
			} else if (createGeoFenceResponse.getMessage().contains("41003")) {
				response.setMessage("The device is not online");
			} else if (createGeoFenceResponse.getMessage().contains("41004")) {
				response.setMessage("Geo-fence operation failed");
			}
		} else if (createGeoFenceResponse.getCode() == 0) {
			response.setMessage("Create an electronic fence successfully");
		}
		return response;
	}

	@Override
	public RouteHistoryWithTotalDistanceModel routeHistory(String imei, String startTime, String endTime)
			throws Exception {

		return getDataFromJimi.routeHistory(imei, startTime, endTime);
	}

 */
}
