package com.spring.gpsApiData.model;

import lombok.Data;

@Data
public class DeviceTrackListModel {
	
	double avgSpeed;
	double distance;
	String endLat ; 
    String endLng ;
    String endTime;
    String imei;
    String runTimeSecond;   
    String startLat;
    String startLng;
    String startTime;
    String startMileage;
    String endMileage;

}
