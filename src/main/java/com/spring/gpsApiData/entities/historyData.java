package com.spring.gpsApiData.entities;

import lombok.Data;


import java.util.UUID;

//@Entity
//@Table(name = "historyData")
@Data
public class historyData {

    private UUID id;
    private String imei;
    private String lat;
    private String lng;
    private String speed;
    private String deviceName;
    private String powerValue;
    private String direction;
    private String timeStamp;
    private String gpsTime;
    private String hbTime;


}
