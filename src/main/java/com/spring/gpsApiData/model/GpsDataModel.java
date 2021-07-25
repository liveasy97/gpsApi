package com.spring.gpsApiData.model;

import lombok.Data;


@Data
public class GpsDataModel {
    private String imei;
    private String lat;
    private String lng;
    private String speed;
    private String deviceName;
    private String powerValue;

}
