package com.spring.gpsApiData.model;

import lombok.Data;

@Data
public class DeviceTrackListModel {
	
	String gpsSpeed;
	String satellite;
	double lat ; 
    double lng ;
    String gpsTime;
    String direction;
    String posType;   
 
}
