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
        List<historyData> list = new ArrayList<>(Collections.emptyList());
        List<historyData> historyDataList = dao.findByImei(imei);
        for (int i = 0; i < historyDataList.toArray().length; i++) {
            if (historyDataList.get(i).getTimeStamp() != null) {
                if (Integer.parseInt(startTime.substring(0, 8)) <= Integer.parseInt(historyDataList.get(i).getTimeStamp().substring(0, 8)) && Integer.parseInt(startTime.substring(9, 15)) <= Integer.parseInt(historyDataList.get(i).getTimeStamp().substring(9, 15))) {
                    if (Integer.parseInt(endTime.substring(0, 8)) >= Integer.parseInt(historyDataList.get(i).getTimeStamp().substring(0, 8)) && Integer.parseInt(endTime.substring(9, 15)) >= Integer.parseInt(historyDataList.get(i).getTimeStamp().substring(9, 15))) {
                        list.add(historyDataList.get(i));
                    }
                }
            }
        }
        return list;}
        else {
            return dao.findAll();
        }
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
