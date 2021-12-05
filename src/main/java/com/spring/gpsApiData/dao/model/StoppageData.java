package com.spring.gpsApiData.dao.model;

import lombok.Data;

@Data
public class StoppageData {
	private String type;
	private String eventTime;
	private double latitude;
	private double longitude;
}
