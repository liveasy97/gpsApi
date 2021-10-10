package com.spring.gpsApiData.service;

import java.util.*;
import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.DeviceTrackListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spring.gpsApiData.utils.getDataFromJimi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.gpsApiData.constants.Constants.Status;
import com.spring.gpsApiData.dao.RegisteredImeiDataDao;
import com.spring.gpsApiData.dao.historyDataDao;
import com.spring.gpsApiData.entities.RegisteredImeiData;
import com.spring.gpsApiData.entities.gpsData;

@Service
public class GpsDataServiceImpl implements GpsDataService {
	
	private static final Logger log = LoggerFactory.getLogger(GpsDataServiceImpl.class);

    @Autowired
    private historyDataDao dao;
    
    @Autowired
    private RegisteredImeiDataDao rdao;
    
    @Autowired
    private getDataFromJimi getDataFromJimi;

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
    
    //fetching data directly from jimi//
    @Override
    public List<historyData> getgpsDataWithoutSaving(String imei) throws Exception {
        
    	historyData datafromjimi = getDataFromJimi.getGpsApiDataUsingImei(imei);

    	List<historyData> result = new ArrayList<>(Collections.emptyList());
    	if(datafromjimi !=null)
    	{	
    		result.add(datafromjimi);
    	}
    	return result;
           
    }

    @Override
    public String savegpsData(gpsData data) {
        return data.toString();
    }

    @Override
    public List<historyData> getHistoryDataWithSaving(String imei, String startTime, String endTime) {
        if(imei != null){
	        List<historyData> historyDataList = dao.findByImeiBetweenTimeRange(imei, startTime, endTime);

	        return historyDataList;
        }
        else {
            return dao.findHistoryDataBetweenTimeRange(startTime,endTime);
        }
    }
    
    // fetching data directly from  jimi //
	@Override
    public List<DeviceTrackListModel> getHistoryDataDirectFromJimi(String imei, String startTime, String endTime) throws Exception {
		    		
		List<DeviceTrackListModel> deviceTrackList = getDataFromJimi.getGpsApiDataUsingImeiStartTimeEndTime(imei,startTime,endTime);
		return deviceTrackList; 	
    }

    @Override
    public void addImei(String imei) throws Exception {
    	
        RegisteredImeiData registeredimeidata = new RegisteredImeiData();	
    	registeredimeidata.setImei(imei);
    	registeredimeidata.setStatus(Status.active.toString());;
    	
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
}
