package com.spring.gpsApiData.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.HistoryDataModel;
import com.spring.gpsApiData.utils.addNewImei;
import com.spring.gpsApiData.utils.getDataFromJimi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.gpsApiData.dao.historyDataDao;
import com.spring.gpsApiData.entities.gpsData;
import org.json.*;
import com.spring.gpsApiData.model.GpsDataModel;

@Service
public class gpsDataServiceImpl implements gpsDataService {

    @Autowired
    private historyDataDao dao;

    @Override
    public GpsDataModel getgpsData(String imei) throws Exception {

        return getDataFromJimi.getGpsApiDataUsingImei(imei);
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
        dao.save(data);
        return "done";
    }


}