package com.spring.gpsApiData.service;

import java.util.*;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.schedulingtasks.GpsDataSchedulingTasks;
import com.spring.gpsApiData.utils.addNewImei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public List<historyData> getgpsData(String imei) throws Exception {
        List<historyData> dataList = dao.findByImei(imei);
        List<historyData> result = new ArrayList<>(Collections.emptyList());
        historyData lastEntry = dataList.get(dataList.size() - 1);
        result.add(lastEntry);
        return result;
    }

    @Override
    public String savegpsData(gpsData data) {

        return data.toString();
    }

    @Override
    public List<historyData> getHistoryData(String imei, String startTime, String endTime) {
        if(imei != null){
	        List<historyData> historyDataList = dao.findByImeiBetweenTimeRange(imei, startTime, endTime);

	        return historyDataList;
        }
        else {
            return dao.findHistoryDataBetweenTimeRange(startTime,endTime);
        }
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
