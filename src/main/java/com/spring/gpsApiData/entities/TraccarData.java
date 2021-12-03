package com.spring.gpsApiData.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tc_positions")
@Data
public class TraccarData {

        @Id
        @GeneratedValue
        private int id;
        private String protocol;
        private int deviceid;
        private Date servertime;
        private Date devicetime;
        private Date fixtime;
        private int valid;
        private double latitude;
        private double longitude;
        private float altitude;
        private float speed;
        private float course;
        private String address;
        private String attributes;
        private double accuracy;
        private String network;
    }


