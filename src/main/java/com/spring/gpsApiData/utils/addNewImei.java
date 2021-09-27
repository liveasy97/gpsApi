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
                        historyData historyData = getDataFromJimi.getGpsApiDataUsingImei(imei);
                        historyData.setId(UUID.randomUUID());
//                        historyData.setDeviceName(gpsData.getDeviceName());
//                        historyData.setImei(imei);
//                        historyData.setLat(gpsData.getLat());
//                        historyData.setLng(gpsData.getLng());
//                        historyData.setPowerValue(gpsData.getPowerValue());
//                        historyData.setSpeed(gpsData.getSpeed());
//                        historyData.setDirection(gpsData.getDirection());
//                        historyData.setGpsTime(gpsData.getGpsTime());
                        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        historyData.setTimeStamp(gmtDateFormat.format(new Date()));
                        System.out.println("historyData : " + historyData);
                        if (Integer.parseInt(historyData.getSpeed()) > 2) {
                            dao.save(historyData);
                            //save data in every 5 seconds when device is moving
                            Thread.sleep(5 * 1000);
                        } else {
                            Thread.sleep(600 * 1000);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//            return "Imei Empty";
            }
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("failed with exception: " + e);
            try {
                Thread.sleep(600 * 1000);
                MyThread myThread = new MyThread();
                myThread.run(imei, dao);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

        }
    }
}
