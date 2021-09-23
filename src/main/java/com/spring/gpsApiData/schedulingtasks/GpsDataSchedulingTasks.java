package com.spring.gpsApiData.schedulingtasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spring.gpsApiData.dao.RegisteredImeiDataDao;
import com.spring.gpsApiData.dao.historyDataDao;
import com.spring.gpsApiData.entities.RegisteredImeiData;
import com.spring.gpsApiData.entities.historyData;
import com.spring.gpsApiData.model.GpsDataModel;
import com.spring.gpsApiData.utils.getDataFromJimi;

@Component
public class GpsDataSchedulingTasks {
	
	private static final Logger log = LoggerFactory.getLogger(GpsDataSchedulingTasks.class);
	
	@Autowired
	private RegisteredImeiDataDao rdao;
	
	@Autowired
	private historyDataDao historydatadao;
	
	@Scheduled(fixedDelay = 10000)
    public void ImeiInfo() {
		
        List<RegisteredImeiData> imeiList = rdao.findEnabledImei();
        
        if(imeiList == null || imeiList.isEmpty()) 
    	{
    		log.info("No Imei devices registered for gpsTracking");
    		return;
    	}

        for(RegisteredImeiData data : imeiList)
        {
        	try
        	{
        		log.info("Tracking GpsData for Imei:" + data.getImei());
        		
        		GpsDataModel gpsData = getDataFromJimi.getGpsApiDataUsingImei(data.getImei());
                historyData historyData = new historyData();
                historyData.setId(UUID.randomUUID());
                historyData.setDeviceName(gpsData.getDeviceName());
                historyData.setImei(data.getImei());
                historyData.setLat(gpsData.getLat());
                historyData.setLng(gpsData.getLng());
                historyData.setPowerValue(gpsData.getPowerValue());
                historyData.setSpeed(gpsData.getSpeed());
                historyData.setDirection(gpsData.getDirection());
                historyData.setGpsTime(gpsData.getGpsTime());
                SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                historyData.setTimeStamp(gmtDateFormat.format(new Date()));
                
                log.info("historyData : " + historyData);
                
                if (Integer.parseInt(gpsData.getSpeed()) > 2) {
                	historydatadao.save(historyData);
                    //save data in every 5 seconds when device is moving
                    Thread.sleep(5 * 1000);
                } else {
                    Thread.sleep(600 * 1000);
                }
        	}
        	catch (Exception e)
        	{
        		log.error("Failed to sync imeidata for :" + data.getImei(), e);
        	}
        }

	}
}