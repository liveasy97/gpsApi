package com.spring.gpsApiData.utils;

import com.spring.gpsApiData.dao.historyDataDao;
import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.GpsDataModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public interface addNewImei {

    static void addImeiAndRecordHistory(String imei, historyDataDao dao) {
        MyThread myThread = new MyThread();
        myThread.run(imei, dao);
        // for every imei start a new thread and automate it to get gpsData every few seconds and store it.

    }
}

class MyThread extends Thread {

    public void run(String imei, historyDataDao dao) {
        // for every imei start a new thread and automate it to get gpsData every few seconds and store it.
        try {
            if (!imei.equals("")) {
                System.out.println(imei);
                try {
                    Boolean shouldRun = true;
                    while (shouldRun) {
                        GpsDataModel gpsData = getDataFromJimi.getGpsApiDataUsingImei(imei);
                        historyData historyData = new historyData();
                        historyData.setId(UUID.randomUUID());
                        historyData.setDeviceName(gpsData.getDeviceName());
                        historyData.setImei(imei);
                        historyData.setLat(gpsData.getLat());
                        historyData.setLng(gpsData.getLng());
                        historyData.setPowerValue(gpsData.getPowerValue());
                        historyData.setSpeed(gpsData.getSpeed());
                        historyData.setDirection(gpsData.getDirection());
                        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        historyData.setTimeStamp(gmtDateFormat.format(new Date()));
                        System.out.println("historyData : " + historyData);
                        dao.save(historyData);
                        if (Integer.parseInt(gpsData.getSpeed()) > 2) {
                            Thread.sleep(1000);
                        } else {
                            Thread.sleep(10 * 1000);
                        }
                    }
//                return null;
                } catch (Exception e) {
                    e.printStackTrace();
//                return "Unexpected Error";

                }
            } else {
//            return "Imei Empty";
            }
        } catch (Exception e) {
            System.out.println("failed with exception: " + e);
        }
    }
}