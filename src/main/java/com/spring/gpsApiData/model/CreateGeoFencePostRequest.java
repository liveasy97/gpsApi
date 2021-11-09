package com.spring.gpsApiData.model;

import lombok.Data;

@Data
public class CreateGeoFencePostRequest {
	
	String imei;
	String radius;
	String lat;
	String lng;
	String alarmType;
	String reportMode;
	String alarmSwitch;
	String zoomLevel;
	String fenceName;
}
