package com.spring.gpsApiData.model;
import lombok.Data;


@Data
public class HistoryDataModel {
    private String id;
    private String imei;
    private String lat;
    private String lng;
    private String speed;
    private String deviceName;
    private String powerValue;
    private String direction;
    private String timeStamp;
}
