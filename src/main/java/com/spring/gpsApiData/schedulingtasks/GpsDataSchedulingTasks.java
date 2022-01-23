/*
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

        	}
        	catch (Exception e)
        	{
        		log.error("Failed to sync imeidata for :" + data.getImei(), e);
        	}
        }

	}
}*/
