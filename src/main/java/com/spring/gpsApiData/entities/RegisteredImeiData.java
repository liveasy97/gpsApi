package com.spring.gpsApiData.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "registeredImeiInfo")
public class RegisteredImeiData {
	
	@Id
	private String imei;
	
	private String status;
	
}