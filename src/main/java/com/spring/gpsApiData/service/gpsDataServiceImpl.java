package com.spring.gpsApiData.service;

import java.util.*;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.utils.addNewImei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.gpsApiData.dao.historyDataDao;
import com.spring.gpsApiData.entities.gpsData;

@Service
public class gpsDataServiceImpl implements gpsDataService {

    @Autowired
    private historyDataDao dao;

    @Override
    public List<historyData> getgpsData(String imei) throws Exception {
        List<historyData> dataList = dao.findByImei(imei);
        List<historyData> result = new ArrayList<>(Collections.emptyList());
        historyData lastEntry = dataList.get(dataList.size()-1);
        result.add(lastEntry);
        return result;
    }

    @Override
    public String savegpsData(gpsData data) {
//        gpsData newData = new gpsData();
//        newData.setImei(data.getImei());
//        newData.setLatitude(data.getLatitude());
//        newData.setLongitude(data.getLongitude());
//        newData.setSpeed(data.getSpeed());
//        newData.setDeviceName(data.getDeviceName());
//        newData.setPowerValue(data.getPowerValue());
//        dao.save(newData);
        return "done";
    }

    @Override
    public Stream<historyData> getAllHistoryData() {
        return dao.findAll().stream();
    }

    @Override
    public String addImei(String imei) {
        addNewImei.addImeiAndRecordHistory(imei, dao);
        return null;
    }

    @Override
    public String saveHistoryData(historyData data) {
        data.setId(UUID.randomUUID());
	    dao.save(data);
        return "done";
    }


}
