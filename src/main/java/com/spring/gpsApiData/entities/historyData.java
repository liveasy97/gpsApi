package com.spring.gpsApiData.entities;

import com.spring.gpsApiData.model.GpsDataModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "historyData")
@Data
public class historyData {
    @Id
    private UUID id;
    private String imei;
    private String lat;
    private String lng;
    private String speed;
    private String deviceName;
    private String powerValue;
    private String direction;
    private String timeStamp;
}
