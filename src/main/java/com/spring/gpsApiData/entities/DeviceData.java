package com.spring.gpsApiData.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "tc_devices")
@Data
public class DeviceData {
    @Id
    private int id;
    private String name;
    private String uniqueid;
    private Timestamp lastupdate;
    private int positionid;
    //private int groupid;
    private String attributes;
    private String phone;
    private String model;
    private String contact;
    private String category;
    private int disabled;

}